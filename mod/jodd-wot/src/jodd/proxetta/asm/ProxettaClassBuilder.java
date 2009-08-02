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
import static jodd.proxetta.asm.ProxettaNaming.INIT_METHOD_NAME;
import static jodd.proxetta.asm.ProxettaAsmUtil.INIT;
import static jodd.proxetta.asm.ProxettaAsmUtil.CLINIT;
import static jodd.proxetta.asm.ProxettaAsmUtil.DESC_VOID;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxyAspect;
import static jodd.util.StringPool.DOT;

import java.util.List;
import java.util.ArrayList;

/**
 * Proxetta class builder.
 */
public class ProxettaClassBuilder extends EmptyClassVisitor {

	protected final ProxyAspect[] aspects;
	protected final String suffix;
	protected final String reqProxyClassName;
	protected final TargetClassInfoReader targetClassInfo;

	protected final WorkData wd;

	/**
	 * Constructs new Proxetta class builder.
	 * @param dest			destination visitor
	 * @param aspects		set of asspects to apply
	 * @param suffix		proxy class name suffix, may be <code>null</code>
	 * @param reqProxyClassName		requested proxy class name, may be <code>null</code>s
	 * @param targetClassInfoReader	target info reader, already invoked.
	 */
	public ProxettaClassBuilder(ClassVisitor dest, ProxyAspect[] aspects, String suffix, String reqProxyClassName, TargetClassInfoReader targetClassInfoReader) {
		this.wd = new WorkData(dest);
		this.aspects = aspects;
		this.suffix = suffix;
		this.reqProxyClassName = reqProxyClassName;
		this.targetClassInfo = targetClassInfoReader;
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
		superName = name;

		// create proxy name
		if (this.reqProxyClassName != null) {
			if (reqProxyClassName.startsWith(DOT)) {
				name = name.substring(0, lastSlash) + '/' + reqProxyClassName.substring(1);
			} else if (reqProxyClassName.endsWith(DOT)) {
				name = reqProxyClassName.replace('.', '/') + wd.targetClassname;
			} else {
				name = reqProxyClassName.replace('.', '/');
			}
		}
		// add optional suffix
		if (suffix != null) {
			name += suffix;
		}
		wd.thisReference = name;
		wd.superReference = superName;

		// change access of destination
		access &= ~MethodInfo.ACC_ABSTRACT;

		// write destination class
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
	 * For each method, {@link ProxettaMethodBuilder} determines if method matches pointcut. If so, method will be proxified.
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodSignatureVisitor msign = targetClassInfo.lookupMethodSignatureVisitor(access, name, desc, wd.superReference);
		if (msign == null) {
			return null;
		}

		// destination constructors [A1]
		if (name.equals(INIT) == true) {
			MethodVisitor mv = wd.dest.visitMethod(access, name, desc, msign.getSignature(), null);
			return new ProxettaCtorBuilder(mv, msign, wd);
		}
		// ignore destination static block
		if (name.equals(CLINIT) == true) {
			return null;
		}
		return applyProxy(msign);
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

		// check all public super methods that are not overriden
		for (ClassReader cr : targetClassInfo.superClassReaders) {
			cr.accept(new EmptyClassVisitor() {

				String declaredClassName;

				@Override
				public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
					declaredClassName = name;
				}


				@Override
				public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
					if (name.equals(INIT) || name.equals(CLINIT)) {
						return null;
					}					
					MethodSignatureVisitor msign = targetClassInfo.lookupMethodSignatureVisitor(access, name, desc, declaredClassName);
					if (msign == null) {
						return null;
					}
					return applyProxy(msign);
				}
			}, 0);
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
     * Visits the enclosing class of the class (not used).
	 */
	@Override
	public void visitOuterClass(String owner, String name, String desc) {
		// not used
	}

    /**
     * Visits a non standard attribute of the class (not used).
     */
	@Override
	public void visitAttribute(Attribute attr) {
		// not used
	}

	/**
     * Visits information about an inner class (not used).
	 */
	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		// not used
	}



	// ---------------------------------------------------------------- create proxy method builder if needed


	/**
	 * Check if proxy should be applied on method and return proxy method builder if so.
	 * Otherwise, returns <code>null</code>.
	 */
	protected ProxettaMethodBuilder applyProxy(MethodSignatureVisitor msign) {
		List<ProxyAspectData> aspectList = null;
		for (ProxyAspectData aspectData : wd.proxyAspects) {
			if (aspectData.apply(msign) == true) {
				if (aspectList == null) {
					aspectList = new ArrayList<ProxyAspectData>(wd.proxyAspects.length);
				}
				aspectList.add(aspectData);
			}
		}
		if (aspectList == null) {
			return null; // no pointcut on this method, return
		}
		wd.proxyApplied = true;
		return new ProxettaMethodBuilder(msign, wd, aspectList);
	}

}