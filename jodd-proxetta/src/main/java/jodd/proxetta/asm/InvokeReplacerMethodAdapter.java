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
import jodd.proxetta.InvokeAspect;
import jodd.proxetta.InvokeInfo;
import jodd.proxetta.InvokeReplacer;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxettaException;
import jodd.proxetta.ProxyTargetReplacement;
import jodd.util.StringPool;
import jodd.asm5.Label;
import jodd.asm5.MethodVisitor;
import jodd.asm5.Type;

import static jodd.asm5.Opcodes.ASTORE;
import static jodd.asm5.Opcodes.POP;
import static jodd.proxetta.asm.ProxettaAsmUtil.INIT;
import static jodd.asm5.Opcodes.ALOAD;
import static jodd.asm5.Opcodes.DUP;
import static jodd.asm5.Opcodes.INVOKEINTERFACE;
import static jodd.asm5.Opcodes.INVOKESPECIAL;
import static jodd.asm5.Opcodes.INVOKESTATIC;
import static jodd.asm5.Opcodes.INVOKEVIRTUAL;
import static jodd.asm5.Opcodes.NEW;
import static jodd.proxetta.asm.ProxettaAsmUtil.isArgumentMethod;
import static jodd.proxetta.asm.ProxettaAsmUtil.isArgumentTypeMethod;
import static jodd.proxetta.asm.ProxettaAsmUtil.isInfoMethod;
import static jodd.proxetta.asm.ProxettaAsmUtil.isTargetClassAnnotationMethod;
import static jodd.proxetta.asm.ProxettaAsmUtil.isTargetMethodAnnotationMethod;

/**
 * Invocation replacer method adapter.
 */
public class InvokeReplacerMethodAdapter extends HistoryMethodAdapter {

	protected final WorkData wd;
	protected final MethodInfo methodInfo;
	protected final InvokeAspect[] aspects;

	public InvokeReplacerMethodAdapter(MethodVisitor mv, MethodInfo methodInfo, WorkData wd, InvokeAspect[] aspects) {
		super(mv);
		this.wd = wd;
		this.aspects = aspects;
		this.methodInfo = methodInfo;
	}

	/**
	 * Detects super ctor invocation.
	 */
	protected boolean firstSuperCtorInitCalled;

	protected boolean proxyInfoRequested;

	/**
	 * New object creation matched.
	 */
	protected InvokeReplacer newInvokeReplacer;

	/**
	 * Invoked on INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE or INVOKEDYNAMIC.
	 */
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean isInterface) {

		// replace NEW.<init>
		if ((newInvokeReplacer != null) && (opcode == INVOKESPECIAL)) {
			String exOwner = owner;
			owner = newInvokeReplacer.getOwner();
			name = newInvokeReplacer.getMethodName();
			desc = changeReturnType(desc, 'L' + exOwner + ';');
			super.visitMethodInsn(INVOKESTATIC, owner, name, desc, isInterface);
			newInvokeReplacer = null;
			return;
		}


		InvokeInfo invokeInfo = new InvokeInfo(owner, name, desc);

		// [*]
		// creating FooClone.<init>; inside the FOO constructor
		// replace the very first invokespecial <init> call (SUB.<init>)
		// to targets subclass with target (FOO.<init>).
		if (methodInfo.getMethodName().equals(INIT)) {
			if (
					(!firstSuperCtorInitCalled) &&
							(opcode == INVOKESPECIAL) &&
							name.equals(INIT) &&
							owner.equals(wd.nextSupername)
					) {
				firstSuperCtorInitCalled = true;
				owner = wd.superReference;
				super.visitMethodInsn(opcode, owner, name, desc, isInterface);
				return;
			}
		}

		// detection of super calls
		if ((opcode == INVOKESPECIAL) && (owner.equals(wd.nextSupername) && (!name.equals(INIT)))) {
			throw new ProxettaException("Super call detected in class " + methodInfo.getClassname() + " method: " + methodInfo.getSignature() +
				"\nProxetta can't handle super calls due to VM limitations.");
		}


		InvokeReplacer ir = null;

		// find first matching aspect
		for (InvokeAspect aspect : aspects) {
			ir = aspect.pointcut(invokeInfo);
			if (ir != null) {
				break;
			}
		}

		if (ir == null || ir.isNone()) {

			if (ProxettaAsmUtil.isCreateArgumentsArrayMethod(name, desc)) {
				ProxyTargetReplacement.createArgumentsArray(mv, methodInfo);
				wd.proxyApplied = true;
				return;
			}

			if (ProxettaAsmUtil.isCreateArgumentsClassArrayMethod(name, desc)) {
				ProxyTargetReplacement.createArgumentsClassArray(mv, methodInfo);
				wd.proxyApplied = true;
				return;
			}

			if (ProxettaAsmUtil.isArgumentsCountMethod(name, desc)) {
				ProxyTargetReplacement.argumentsCount(mv, methodInfo);
				wd.proxyApplied = true;
				return;
			}

			if (ProxettaAsmUtil.isTargetMethodNameMethod(name, desc)) {
				ProxyTargetReplacement.targetMethodName(mv, methodInfo);
				wd.proxyApplied = true;
				return;
			}

			if (ProxettaAsmUtil.isTargetMethodDescriptionMethod(name, desc)) {
				ProxyTargetReplacement.targetMethodDescription(mv, methodInfo);
				wd.proxyApplied = true;
				return;
			}

			if (ProxettaAsmUtil.isTargetMethodSignatureMethod(name, desc)) {
				ProxyTargetReplacement.targetMethodSignature(mv, methodInfo);
				wd.proxyApplied = true;
				return;
			}

			if (ProxettaAsmUtil.isReturnTypeMethod(name, desc)) {
				ProxyTargetReplacement.returnType(mv, methodInfo);
				wd.proxyApplied = true;
				return;
			}

			if (ProxettaAsmUtil.isTargetClassMethod(name, desc)) {
				ProxyTargetReplacement.targetClass(mv, methodInfo);
				wd.proxyApplied = true;
				return;
			}

			if (isArgumentTypeMethod(name, desc)) {
				int argIndex = this.getArgumentIndex();
				ProxyTargetReplacement.argumentType(mv, methodInfo, argIndex);
				wd.proxyApplied = true;
				return;
			}

			if (isArgumentMethod(name, desc)) {
				int argIndex = this.getArgumentIndex();
				ProxyTargetReplacement.argument(mv, methodInfo, argIndex);
				wd.proxyApplied = true;
				return;
			}

			if (isInfoMethod(name, desc)) {
				proxyInfoRequested = true;
				// we are NOT calling the replacement here, as we would expect.
				// NO, we need to wait for the very next ASTORE method so we
				// can read the index and use it for replacement method!!!

				//ProxyTargetReplacement.info(mv, methodInfo);
				wd.proxyApplied = true;
				return;
			}

			if (isTargetMethodAnnotationMethod(name, desc)) {
				String[] args = getLastTwoStringArguments();

				// pop current two args
				mv.visitInsn(POP);
				mv.visitInsn(POP);

				ProxyTargetReplacement.targetMethodAnnotation(mv, methodInfo, args);
				wd.proxyApplied = true;
				return;
			}

			if (isTargetClassAnnotationMethod(name, desc)) {
				String[] args = getLastTwoStringArguments();

				// pop current two args
				mv.visitInsn(POP);
				mv.visitInsn(POP);

				ProxyTargetReplacement.targetClassAnnotation(mv, methodInfo.getClassInfo(), args);
				wd.proxyApplied = true;
				return;
			}


			super.visitMethodInsn(opcode, owner, name, desc, isInterface);
			return;
		}

		wd.proxyApplied = true;

		String exOwner = owner;
		owner = ir.getOwner();
		name = ir.getMethodName();

		switch (opcode) {
			case INVOKEINTERFACE:
				desc = prependArgument(desc, AsmUtil.L_SIGNATURE_JAVA_LANG_OBJECT);
				break;
			case INVOKEVIRTUAL:
				desc = prependArgument(desc, AsmUtil.L_SIGNATURE_JAVA_LANG_OBJECT);
				break;
			case INVOKESTATIC:
				break;
			default:
				throw new ProxettaException("Unsupported opcode: " + opcode);
		}

		// additional arguments
		if (ir.isPassOwnerName()) {
			desc = appendArgument(desc, AsmUtil.L_SIGNATURE_JAVA_LANG_STRING);
			super.visitLdcInsn(exOwner);
		}
		if (ir.isPassMethodName()) {
			desc = appendArgument(desc, AsmUtil.L_SIGNATURE_JAVA_LANG_STRING);
			super.visitLdcInsn(methodInfo.getMethodName());
		}
		if (ir.isPassMethodSignature()) {
			desc = appendArgument(desc, AsmUtil.L_SIGNATURE_JAVA_LANG_STRING);
			super.visitLdcInsn(methodInfo.getSignature());
		}
		if (ir.isPassTargetClass()) {
			desc = appendArgument(desc, AsmUtil.L_SIGNATURE_JAVA_LANG_CLASS);
			super.mv.visitLdcInsn(Type.getType('L' + wd.superReference + ';'));
		}
		if (ir.isPassThis()) {
			desc = appendArgument(desc, AsmUtil.L_SIGNATURE_JAVA_LANG_OBJECT);
			super.mv.visitVarInsn(ALOAD, 0);
		}

		super.visitMethodInsn(INVOKESTATIC, owner, name, desc, false);
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		// [*]
		// Fix all Foo.<field> to FooClone.<field>
		if (owner.equals(wd.superReference)) {
			owner = wd.thisReference;
		}
		super.visitFieldInsn(opcode, owner, name, desc);
	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		if (opcode == NEW) {
			InvokeInfo invokeInfo = new InvokeInfo(type, INIT, StringPool.EMPTY);
			for (InvokeAspect aspect : aspects) {
				InvokeReplacer ir = aspect.pointcut(invokeInfo);
				if (ir != null && !ir.isNone()) {
					newInvokeReplacer = ir;

					// new pointcut found, skip the new instruction and the following dup.
					// and then go to the invokespecial
					return;
				}
			}
		}
		super.visitTypeInsn(opcode, type);
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		if (proxyInfoRequested) {
			proxyInfoRequested = false;
			if (opcode == ASTORE) {
				ProxyTargetReplacement.info(mv, methodInfo, var);
			}
		}
		super.visitVarInsn(opcode, var);
	}

	@Override
	public void visitInsn(int opcode) {
		if ((newInvokeReplacer != null) && (opcode == DUP)) {
			return;	// skip dup after new
		}
		super.visitInsn(opcode);
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
	}

	@Override
	public void visitLineNumber(int line, Label start) {
	}


	// ---------------------------------------------------------------- util

	/**
	 * Appends argument to the existing description.
	 */
	protected static String appendArgument(String desc, String type) {
		int ndx = desc.indexOf(')');
		return desc.substring(0, ndx) + type + desc.substring(ndx);
	}

	/**
	 * Prepends argument to the existing description.
	 */
	protected static String prependArgument(String desc, String type) {
		int ndx = desc.indexOf('(');
		ndx++;
		return desc.substring(0, ndx) + type + desc.substring(ndx);
	}

	/**
	 * Changes return type.
	 */
	protected static String changeReturnType(String desc, String type) {
		int ndx = desc.indexOf(')');
		return desc.substring(0, ndx + 1) + type;
	} 

}
