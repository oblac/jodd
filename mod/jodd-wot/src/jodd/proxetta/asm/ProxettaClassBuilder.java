// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static jodd.proxetta.asm.ProxettaNaming.PROXY_CLASS_NAME_SUFFIX;
import static jodd.proxetta.asm.ProxettaNaming.INIT_METHOD_NAME;
import static jodd.proxetta.asm.ProxettaAsmUtil.createMethodSignature;
import static jodd.proxetta.asm.ProxettaAsmUtil.INIT;
import static jodd.proxetta.asm.ProxettaAsmUtil.CLINIT;
import static jodd.proxetta.asm.ProxettaAsmUtil.DESC_VOID;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxettaException;
import jodd.proxetta.ProxyAspect;
import jodd.util.ClassLoaderUtil;
import jodd.io.StreamUtil;
import jodd.mutable.MutableInteger;

import java.io.InputStream;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

/**
 * Proxetta class builder.
 */
public class ProxettaClassBuilder extends EmptyClassVisitor {

	protected final ProxyAspect[] aspects;
	protected final MutableInteger suffix;
	protected final Set<String> topMethodSignatures;      // set of all top methods

	protected final WorkData wd;

	public ProxettaClassBuilder(ClassVisitor dest, ProxyAspect[] aspects, MutableInteger suffix) {
		this.wd = new WorkData(dest);
		this.aspects = aspects;
		this.suffix = suffix;
		this.topMethodSignatures = new HashSet<String>();
	}


	// ---------------------------------------------------------------- header


	/**
	 * Creates destination subclass header from current target class. Destination name is created from targets by
	 * adding a suffix and, optionaly, a number. Destination extends the target.
	 */
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		int lastSlash = name.lastIndexOf('/');
		wd.targetPackage = name.substring(0, lastSlash).replace('/', '.');
		wd.targetClassname = name.substring(lastSlash + 1);
		wd.nextSupername = superName;
		wd.hierarchyLevel = 0;
		superName = name;
		name += PROXY_CLASS_NAME_SUFFIX;
		if (suffix != null) {
			name += suffix.value;
			suffix.value++;
		}
		wd.thisReference = name;
		wd.superReference = superName;
		wd.dest.visit(version, access, name, signature, superName, null);

		wd.proxyAspects = new ProxyAspectData[aspects.length];
		for (int i = 0; i < aspects.length; i++) {
			wd.proxyAspects[i] = new ProxyAspectData(wd, aspects[i], i);
		}

	}


	// ---------------------------------------------------------------- methods and fields

	/**
	 * Creates proxified methods and constructors.
	 * Destination proxy will have all constructors as a target class, using {@link jodd.proxetta.asm.ProxettaCtorBuilder}.
	 * Static initializers are removed, since they will be execute in target anyway.
	 * For each method, {@link ProxyMethodBuilder} determines if method matches pointcut. If so, method will be proxified.
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodSignatureVisitor msign = createMethodSignature(access, name, desc, wd.superReference);

		// destination constructors [A1]
		if (name.equals(INIT) == true) {
			MethodVisitor mv = wd.dest.visitMethod(access, name, desc, msign.getSignature(), null);
			return new ProxettaCtorBuilder(mv, msign, wd);
		}
		// ignore destination static block
		if (name.equals(CLINIT) == true) {
			return null;
		}
		topMethodSignatures.add(msign.getSignature());
		return new ProxyMethodBuilder(msign, wd);
	}


	/**
	 * Ignores fields. Fields are not copied to the destination.
	 */
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		return null;
	}


	// ---------------------------------------------------------------- annotation

	/**
	 * Copies all destination type annotations to the target.
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		AnnotationVisitor destAnn = wd.dest.visitAnnotation(desc, visible); // [A3]
		return new AnnotationVisitorAdapter(destAnn);
	}

	// ---------------------------------------------------------------- end

	/**
	 * Finalizes creation of destination proxy class.
	 */
	@Override
	public void visitEnd() {

		// creates static initialization block that simply calls all advice static init methods in correct order
		if (wd.adviceClinits != null) {
			MethodVisitor mv = wd.dest.visitMethod(MethodInfo.ACC_STATIC, CLINIT, DESC_VOID, null, null);
			mv.visitCode();
			for (String name : wd.adviceClinits) {
				mv.visitMethodInsn(INVOKESTATIC, wd.thisReference, name, DESC_VOID);
			}
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}

		// creates init method that simply calls all advice constructor methods in correct order
		// this created init method is called from each destination's constructor
		MethodVisitor mv = wd.dest.visitMethod(MethodInfo.ACC_PRIVATE | MethodInfo.ACC_FINAL, INIT_METHOD_NAME, DESC_VOID, null, null);
		mv.visitCode();
		if (wd.adviceInits != null) {
			for (String name : wd.adviceInits) {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKESPECIAL, wd.thisReference, name, DESC_VOID);
			}
		}
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// check all public super methods that are not overriden in superclass
		while (wd.nextSupername != null) {
			InputStream inputStream = null;
			try {
				inputStream = ClassLoaderUtil.getClassAsStream(wd.nextSupername);
				ClassReader cr = new ClassReader(inputStream);
				cr.accept(new EmptyClassVisitor() {

					String declaredClassName;

					@Override
					public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
	                    wd.nextSupername = superName;
						wd.hierarchyLevel++;
						declaredClassName = name;
					}

					@Override
					public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
						if (name.equals(INIT) || name.equals(CLINIT)) {
							return null;
						}
						MethodSignatureVisitor msign = createMethodSignature(access, name, desc, wd.superReference);
						int acc = msign.getAccessFlags();
						if ((acc & MethodInfo.ACC_PUBLIC) == 0) {   // skip non-public
						    return null;
						}
						if ((acc & MethodInfo.ACC_FINAL) != 0) {    // skip finals
							return null;
						}
						if (topMethodSignatures.contains(msign.getSignature())) {
							return null;
						}
						msign.setDeclaredClassName(declaredClassName);
						return new ProxyMethodBuilder(msign, wd);
					}
				}, 0);
			} catch (IOException ioex) {
				throw new ProxettaException("Unable to inspect super class: " + wd.nextSupername, ioex);
			} finally {
				StreamUtil.close(inputStream);
			}
		}
		wd.dest.visitEnd();
	}



	// ---------------------------------------------------------------- not used

	/**
     * Visits the source of the class (not used).
     */
    @Override
	public void visitSource(String source, String debug) {
		// not used
	}

	/**
     * Visits the enclosing class of the class (not supported).
	 */
	@Override
	public void visitOuterClass(String owner, String name, String desc) {
		throw new ProxettaException("Outer classes are not supported: " + owner + ' ' + wd.dest);		// todo da li ovo moze da se ignorise?
	}

    /**
     * Visits a non standard attribute of the class (not used).
     */
	@Override
	public void visitAttribute(Attribute attr) {
		// not used
	}

	/**
     * Visits information about an inner class (not supported). This inner class is not
     * necessarily a member of the class being visited.
	 */
	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		throw new ProxettaException("Inner classes are not supported: " + outerName + ' ' + innerName);		// todo da li ovo moze da se ignroise
	}

}