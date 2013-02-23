// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import jodd.asm4.MethodVisitor;
import jodd.asm4.AnnotationVisitor;
import static jodd.asm4.Opcodes.INVOKESPECIAL;
import static jodd.asm4.Opcodes.ALOAD;
import static jodd.asm4.Opcodes.RETURN;
import static jodd.proxetta.asm.ProxettaAsmUtil.loadSpecialMethodArguments;
import static jodd.proxetta.asm.ProxettaAsmUtil.DESC_VOID;
import static jodd.proxetta.asm.ProxettaNaming.INIT_METHOD_NAME;
import jodd.asm.EmptyMethodVisitor;

/**
 * Destination ctor builder
 */
public class ProxettaCtorBuilder extends EmptyMethodVisitor {

	protected final MethodSignatureVisitor msign;
	protected final MethodVisitor mv;
	protected final WorkData wd;

	public ProxettaCtorBuilder(MethodVisitor mv, MethodSignatureVisitor msign, WorkData wd) {
		this.mv = mv;
		this.msign = msign;
		this.wd = wd;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		mv.visitAnnotation(desc, visible);
		return null;
	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
		mv.visitParameterAnnotation(parameter, desc, visible);
		return null;
	}

	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		mv.visitAnnotationDefault();
		return null;
	}

	@Override
	public void visitEnd() {
		mv.visitCode();

		// call super ctor
		loadSpecialMethodArguments(mv, msign);
		mv.visitMethodInsn(INVOKESPECIAL, wd.superReference, msign.getMethodName(), msign.getDescription());

		// invoke advice ctors
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, wd.thisReference, INIT_METHOD_NAME, DESC_VOID);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
}
