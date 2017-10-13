// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.proxetta.asm;

import jodd.asm.AsmUtil;
import jodd.proxetta.ProxettaException;
import jodd.asm5.ClassVisitor;
import jodd.asm5.MethodVisitor;
import jodd.asm5.AnnotationVisitor;
import jodd.asm5.ClassReader;
import jodd.asm5.Attribute;
import jodd.asm5.FieldVisitor;

import static jodd.asm5.Opcodes.ACC_ABSTRACT;
import static jodd.asm5.Opcodes.INVOKESTATIC;
import static jodd.asm5.Opcodes.RETURN;
import static jodd.asm5.Opcodes.ALOAD;
import static jodd.asm5.Opcodes.INVOKESPECIAL;
import static jodd.proxetta.JoddProxetta.initMethodName;
import static jodd.proxetta.asm.ProxettaAsmUtil.INIT;
import static jodd.proxetta.asm.ProxettaAsmUtil.CLINIT;
import static jodd.proxetta.asm.ProxettaAsmUtil.DESC_VOID;
import jodd.proxetta.ProxyAspect;
import jodd.asm.AnnotationVisitorAdapter;
import jodd.asm.EmptyClassVisitor;

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
	 * @param aspects		set of aspects to apply
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

	/**
	 * Returns working data.
	 */
	public WorkData getWorkData() {
		return wd;
	}

	// ---------------------------------------------------------------- header


	/**
	 * Creates destination subclass header from current target class. Destination name is created from targets by
	 * adding a suffix and, optionally, a number. Destination extends the target.
	 */
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		wd.init(name, superName, this.suffix, this.reqProxyClassName);

		// change access of destination
		access &= ~AsmUtil.ACC_ABSTRACT;

		// write destination class
		wd.dest.visit(version, access, wd.thisReference, signature, wd.superName, null);

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
		if (name.equals(INIT)) {
			MethodVisitor mv = wd.dest.visitMethod(access, name, desc, msign.getAsmMethodSignature(), null);
			return new ProxettaCtorBuilder(mv, msign, wd);
		}
		// ignore destination static block
		if (name.equals(CLINIT)) {
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
		makeStaticInitBlock();

		makeProxyConstructor();

		processSuperMethods();

		wd.dest.visitEnd();
	}

	/**
	 * Creates static initialization block that simply calls all
	 * advice static init methods in correct order.
	 */
	protected void makeStaticInitBlock() {
		if (wd.adviceClinits != null) {
			MethodVisitor mv = wd.dest.visitMethod(AsmUtil.ACC_STATIC, CLINIT, DESC_VOID, null, null);
			mv.visitCode();
			for (String name : wd.adviceClinits) {
				mv.visitMethodInsn(
					INVOKESTATIC,
					wd.thisReference,
					name, DESC_VOID,
					false);
			}
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
	}

	/**
	 * Creates init method that simply calls all advice constructor methods in correct order.
	 * This created init method is called from each destination's constructor.
	 */
	protected void makeProxyConstructor() {
		MethodVisitor mv = wd.dest.visitMethod(AsmUtil.ACC_PRIVATE | AsmUtil.ACC_FINAL, initMethodName, DESC_VOID, null, null);
		mv.visitCode();
		if (wd.adviceInits != null) {
			for (String name : wd.adviceInits) {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn
					(INVOKESPECIAL,
						wd.thisReference,
						name, DESC_VOID,
						false);
			}
		}
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	/**
	 * Checks for all public super methods that are not overridden.
	 */
	protected void processSuperMethods() {
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
		List<ProxyAspectData> aspectList = matchMethodPointcuts(msign);

		if (aspectList == null) {
			// no pointcuts on this method, return
			return null;
		}

		int access = msign.getAccessFlags();
		if ((access & ACC_ABSTRACT) != 0) {
			throw new ProxettaException("Unable to process abstract method: " + msign);
		}

		wd.proxyApplied = true;
		return new ProxettaMethodBuilder(msign, wd, aspectList);
	}

	/**
	 * Matches pointcuts on method. If no pointcut found, returns <code>null</code>.
	 */
	protected List<ProxyAspectData> matchMethodPointcuts(MethodSignatureVisitor msign) {
		List<ProxyAspectData> aspectList = null;
		for (ProxyAspectData aspectData : wd.proxyAspects) {
			if (aspectData.apply(msign)) {
				if (aspectList == null) {
					aspectList = new ArrayList<>(wd.proxyAspects.length);
				}
				aspectList.add(aspectData);
			}
		}
		return aspectList;
	}

}
