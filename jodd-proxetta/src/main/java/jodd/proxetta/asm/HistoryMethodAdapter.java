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

import jodd.asm.MethodAdapter;
import jodd.asm7.Label;
import jodd.asm7.MethodVisitor;
import jodd.proxetta.ProxettaException;

import static jodd.asm7.Opcodes.BIPUSH;
import static jodd.asm7.Opcodes.ICONST_0;
import static jodd.asm7.Opcodes.ICONST_1;
import static jodd.asm7.Opcodes.ICONST_2;
import static jodd.asm7.Opcodes.ICONST_3;
import static jodd.asm7.Opcodes.ICONST_4;
import static jodd.asm7.Opcodes.ICONST_5;
import static jodd.asm7.Opcodes.SIPUSH;

/**
 * Method adapter that tracks history of previous instructions.
 * <ul>
 * <li>that remembers previous opcode of 'insn' and 'intInsn' instructions.
  * Used to detect single (last) int argument value of a method call.</li>
 * <li>Stores last two arguments as strings</li>
 * </ul>
 */
abstract class HistoryMethodAdapter extends MethodAdapter {

	protected HistoryMethodAdapter(final MethodVisitor methodVisitor) {
		super(methodVisitor);
	}

	// ---------------------------------------------------------------- history

	protected int opcode;
	protected int operand;
	protected boolean isPrevious;       // true only if previous opcode is of the correct type
	protected boolean traceNext;        // true only to trace very next opcode
	protected String[] strArgs = new String[2];

	// ---------------------------------------------------------------- get index

	/**
	 * Returns argument index from the history.
	 * <b>Must</b> POP value from the stack after the execution.
	 */
	protected int getArgumentIndex() {
		if (!isPrevious) {
			throw new ProxettaException("Unexpected previous instruction type used for setting argument index");
		}
		int argIndex;
		switch (opcode) {
			case ICONST_0: argIndex = 0; break;
			case ICONST_1: argIndex = 1; break;
			case ICONST_2: argIndex = 2; break;
			case ICONST_3: argIndex = 3; break;
			case ICONST_4: argIndex = 4; break;
			case ICONST_5: argIndex = 5; break;
			case BIPUSH:
			case SIPUSH:
				argIndex = operand; break;
			default:
				throw new ProxettaException("Unexpected previous instruction used for setting argument index");
		}
		return argIndex;
	}

	/**
	 * Returns last two string arguments.
	 */
	public String[] getLastTwoStringArguments() {
		return strArgs;
	}

	/**
	 * Adds last LDC arguments to {@link #getLastTwoStringArguments() string arguments}.
	 */
	private void keepStringArgument(final Object value) {
		strArgs[0] = strArgs[1];
		strArgs[1] = value.toString();
	}

	// ---------------------------------------------------------------- visitors

	@Override
	public void visitInsn(final int opcode) {
		this.opcode = opcode;
		isPrevious = true;
		traceNext = false;
		super.visitInsn(opcode);
	}

	@Override
	public void visitIntInsn(final int opcode, final int operand) {
		this.opcode = opcode;
		this.operand = operand;
		isPrevious = true;
		traceNext = false;
		super.visitIntInsn(opcode, operand);
	}

	@Override
	public void visitVarInsn(final int opcode, final int var) {
		isPrevious = false;
		traceNext = false;
		super.visitVarInsn(opcode, var);
	}

	@Override
	public void visitTypeInsn(final int opcode, final String type) {
		isPrevious = false;
		traceNext = false;
		super.visitTypeInsn(opcode, type);
	}

	@Override
	public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
		isPrevious = false;
		traceNext = false;
		super.visitFieldInsn(opcode, owner, name, desc);
	}

	@Override
	public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean isInterface) {
		isPrevious = false;
		traceNext = false;
		super.visitMethodInsn(opcode, owner, name, desc, isInterface);
	}

	@Override
	public void visitJumpInsn(final int opcode, final Label label) {
		isPrevious = false;
		traceNext = false;
		super.visitJumpInsn(opcode, label);
	}

	@Override
	public void visitLdcInsn(final Object cst) {
		isPrevious = false;
		traceNext = false;

		keepStringArgument(cst);

		super.visitLdcInsn(cst);
	}

	@Override
	public void visitIincInsn(final int var, final int increment) {
		isPrevious = false;
		traceNext = false;
		super.visitIincInsn(var, increment);
	}

	@Override
	public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label[] labels) {
		isPrevious = false;
		traceNext = false;
		super.visitTableSwitchInsn(min, max, dflt, labels);
	}

	@Override
	public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
		isPrevious = false;
		traceNext = false;
		super.visitLookupSwitchInsn(dflt, keys, labels);
	}

	@Override
	public void visitMultiANewArrayInsn(final String desc, final int dims) {
		isPrevious = false;
		traceNext = false;
		super.visitMultiANewArrayInsn(desc, dims);
	}

	@Override
	public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
		//super.visitLocalVariable(name, desc, signature, start, end, index);
	}

	@Override
	public void visitLineNumber(final int line, final Label start) {
		//super.visitLineNumber(line, start);
	}

}