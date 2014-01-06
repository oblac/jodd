// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import jodd.asm4.MethodVisitor;
import jodd.asm4.AnnotationVisitor;
import static jodd.asm4.Opcodes.INVOKESPECIAL;
import static jodd.asm4.Opcodes.ALOAD;
import static jodd.asm4.Opcodes.RETURN;
import static jodd.proxetta.asm.ProxettaAsmUtil.loadSpecialMethodArguments;
import static jodd.proxetta.asm.ProxettaAsmUtil.DESC_VOID;
import static jodd.JoddProxetta.initMethodName;
import jodd.asm.EmptyMethodVisitor;

/**
 * Destination ctor builder
 */
public class ProxettaCtorBuilder extends EmptyMethodVisitor {

	protected final MethodSignatureVisitor msign;
	protected final MethodVisitor methodVisitor;
	protected final WorkData wd;

	public ProxettaCtorBuilder(MethodVisitor methodVisitor, MethodSignatureVisitor msign, WorkData wd) {
		this.methodVisitor = methodVisitor;
		this.msign = msign;
		this.wd = wd;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		methodVisitor.visitAnnotation(desc, visible);
		return null;
	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
		methodVisitor.visitParameterAnnotation(parameter, desc, visible);
		return null;
	}

	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		methodVisitor.visitAnnotationDefault();
		return null;
	}

	@Override
	public void visitEnd() {
		methodVisitor.visitCode();

		// call super ctor
		loadSpecialMethodArguments(methodVisitor, msign);
		methodVisitor.visitMethodInsn(INVOKESPECIAL, wd.superReference, msign.getMethodName(), msign.getDescription());

		// invoke advice ctors
		methodVisitor.visitVarInsn(ALOAD, 0);
		methodVisitor.visitMethodInsn(INVOKESPECIAL, wd.thisReference, initMethodName, DESC_VOID);
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(0, 0);
		methodVisitor.visitEnd();
	}
}
