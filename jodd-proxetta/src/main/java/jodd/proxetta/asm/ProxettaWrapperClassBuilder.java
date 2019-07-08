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
import jodd.asm7.ClassVisitor;
import jodd.asm7.FieldVisitor;
import jodd.asm7.MethodVisitor;
import jodd.asm7.Opcodes;
import jodd.proxetta.ProxyAspect;

import java.lang.reflect.Modifier;
import java.util.List;

import static jodd.asm7.Opcodes.ACC_ABSTRACT;
import static jodd.asm7.Opcodes.ACC_NATIVE;
import static jodd.asm7.Opcodes.ALOAD;
import static jodd.asm7.Opcodes.GETFIELD;
import static jodd.asm7.Opcodes.INVOKEINTERFACE;
import static jodd.asm7.Opcodes.INVOKEVIRTUAL;
import static jodd.proxetta.asm.ProxettaAsmUtil.CLINIT;
import static jodd.proxetta.asm.ProxettaAsmUtil.INIT;
import static jodd.proxetta.asm.ProxettaAsmUtil.loadVirtualMethodArguments;
import static jodd.proxetta.asm.ProxettaAsmUtil.visitReturn;

public class ProxettaWrapperClassBuilder extends ProxettaClassBuilder {

	protected final Class targetClassOrInterface;
	protected final Class targetInterface;
	protected final String targetFieldName;
	protected final boolean createTargetInDefaultCtor;

	public ProxettaWrapperClassBuilder(
		final Class targetClassOrInterface,
		final Class targetInterface,
		final String targetFieldName,
		final ClassVisitor dest,
		final ProxyAspect[] aspects,
		final String suffix,
		final String reqProxyClassName,
		final TargetClassInfoReader targetClassInfoReader,
		final boolean createTargetInDefaultCtor
		) {

		super(dest, aspects, suffix, reqProxyClassName, targetClassInfoReader);
		this.targetClassOrInterface = targetClassOrInterface;
		this.targetInterface = targetInterface;
		this.targetFieldName = targetFieldName;
		this.createTargetInDefaultCtor = createTargetInDefaultCtor;

		wd.allowFinalMethods = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final int version, int access, final String name, final String signature, final String superName, String[] interfaces) {

		wd.init(name, superName, this.suffix, this.reqProxyClassName);

		// no superclass
		wd.superName = AsmUtil.SIGNATURE_JAVA_LANG_OBJECT;

		// change access of destination
		access &= ~AsmUtil.ACC_ABSTRACT;
		access &= ~AsmUtil.ACC_INTERFACE;

		// write destination class
		if (targetClassOrInterface.isInterface()) {
			// target is interface
			wd.wrapInterface = true;

			interfaces = new String[] {targetClassOrInterface.getName().replace('.', '/')};
		} else {
			// target is class
			wd.wrapInterface = false;

			if (targetInterface != null) {
				// interface provided
				interfaces = new String[] {targetInterface.getName().replace('.', '/')};
			} else {
				// no interface provided, use all
				//interfaces = null;
			}
		}
		final int v = ProxettaAsmUtil.resolveJavaVersion(version);
		wd.dest.visit(v, access, wd.thisReference, signature, wd.superName, interfaces);

		wd.proxyAspects = new ProxyAspectData[aspects.length];
		for (int i = 0; i < aspects.length; i++) {
			wd.proxyAspects[i] = new ProxyAspectData(wd, aspects[i], i);
		}

		// create new field wrapper field and store it's reference into work-data
		wd.wrapperRef = targetFieldName;
		wd.wrapperType = 'L' + name + ';';


		if (createTargetInDefaultCtor) {
			// create private, final field
			final FieldVisitor fv = wd.dest.visitField(AsmUtil.ACC_PRIVATE | AsmUtil.ACC_FINAL, wd.wrapperRef, wd.wrapperType, null, null);
			fv.visitEnd();

			createEmptyCtorThatCreatesTarget();
		}
		else {
			// create public, non-final field
			final FieldVisitor fv = wd.dest.visitField(AsmUtil.ACC_PUBLIC, wd.wrapperRef, wd.wrapperType, null, null);
			fv.visitEnd();

			createEmptyCtor();
		}
	}

	/**
	 * Created empty default constructor.
	 */
	protected void createEmptyCtor() {
		final MethodVisitor mv = wd.dest.visitMethod(AsmUtil.ACC_PUBLIC, INIT, "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			AsmUtil.SIGNATURE_JAVA_LANG_OBJECT,
			INIT, "()V",
			false);
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	protected void createEmptyCtorThatCreatesTarget() {
		final MethodVisitor mv = wd.dest.visitMethod(AsmUtil.ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			AsmUtil.SIGNATURE_JAVA_LANG_OBJECT,
			INIT, "()V",
			false);
		mv.visitVarInsn(ALOAD, 0);

		mv.visitTypeInsn(Opcodes.NEW, wd.superReference);
		mv.visitInsn(Opcodes.DUP);
		mv.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			wd.superReference,
			INIT, "()V",
			false);

		mv.visitFieldInsn(Opcodes.PUTFIELD,
			wd.thisReference,
			wd.wrapperRef,
			wd.wrapperType);

		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(3, 1);
		mv.visitEnd();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
		MethodSignatureVisitor msign = targetClassInfo.lookupMethodSignatureVisitor(access, name, desc, wd.superReference);
		if (msign == null) {
			return null;
		}

		// ignore all destination constructors
		if (name.equals(INIT)) {
			return null;
		}
		// ignore all destination static block
		if (name.equals(CLINIT)) {
			return null;
		}

		// skip all static methods
		if (Modifier.isStatic(access)) {
			return null;
		}

		return applyProxy(msign);
	}

	@Override
	protected ProxettaMethodBuilder applyProxy(final MethodSignatureVisitor msign) {
		List<ProxyAspectData> aspectList = matchMethodPointcuts(msign);

		if (aspectList == null) {
			wd.proxyApplied = true;
			createSimpleMethodWrapper(msign);
			return null;
		}

		wd.proxyApplied = true;
		return new ProxettaMethodBuilder(msign, wd, aspectList);

	}

	/**
	 * Creates simple method wrapper without proxy.
	 */
	protected void createSimpleMethodWrapper(final MethodSignatureVisitor msign) {

		int access = msign.getAccessFlags();

		access &= ~ACC_ABSTRACT;
		access &= ~ACC_NATIVE;

		MethodVisitor mv = wd.dest.visitMethod(
				access, msign.getMethodName(), msign.getDescription(), msign.getAsmMethodSignature(), msign.getExceptions());
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, wd.thisReference, wd.wrapperRef, wd.wrapperType);
		loadVirtualMethodArguments(mv, msign);

		if (wd.wrapInterface) {
			mv.visitMethodInsn(
				INVOKEINTERFACE,
				wd.wrapperType.substring(1, wd.wrapperType.length() - 1),
				msign.getMethodName(),
				msign.getDescription(),
				true);
		} else {
			mv.visitMethodInsn(
				INVOKEVIRTUAL,
				wd.wrapperType.substring(1, wd.wrapperType.length() - 1),
				msign.getMethodName(),
				msign.getDescription(),
				false);
		}

		ProxettaAsmUtil.prepareReturnValue(mv, msign, 0);
		visitReturn(mv, msign, true);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	@Override
	public void visitEnd() {
		makeStaticInitBlock();

		processSuperMethods();

		wd.dest.visitEnd();

	}
}
