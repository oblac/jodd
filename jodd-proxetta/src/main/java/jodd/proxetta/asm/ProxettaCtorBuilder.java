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

import jodd.asm5.MethodVisitor;
import jodd.asm5.AnnotationVisitor;
import static jodd.asm5.Opcodes.INVOKESPECIAL;
import static jodd.asm5.Opcodes.ALOAD;
import static jodd.asm5.Opcodes.RETURN;
import static jodd.proxetta.asm.ProxettaAsmUtil.loadSpecialMethodArguments;
import static jodd.proxetta.asm.ProxettaAsmUtil.DESC_VOID;
import static jodd.proxetta.JoddProxetta.initMethodName;
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
		methodVisitor.visitMethodInsn(
			INVOKESPECIAL,
			wd.superReference,
			msign.getMethodName(),
			msign.getDescription(),
			false);

		// invoke advice ctors
		methodVisitor.visitVarInsn(ALOAD, 0);

		methodVisitor.visitMethodInsn(
			INVOKESPECIAL,
			wd.thisReference,
			initMethodName, DESC_VOID,
			false);

		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(0, 0);
		methodVisitor.visitEnd();
	}
}
