// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Label;
import static org.objectweb.asm.Opcodes.*;
import jodd.proxetta.ProxettaException;

/**
 * Method adapter that remembers previous opcode of 'insn' and 'intInsn' instructions.
 * Used to detect single (last) int argument value of a method call.
 */
abstract class IntArgHistoryMethodAdapter extends MethodAdapter {

	protected IntArgHistoryMethodAdapter(MethodVisitor methodVisitor) {
		super(methodVisitor);
	}

	// ---------------------------------------------------------------- history

	protected int opcode;
	protected int operand;
	protected boolean isPrevious;       // true only if previous opcode is of the correct type
	protected boolean traceNext;        // true only to trace very next opcode

	// ---------------------------------------------------------------- get index

	/**
	 * Returns argument index from the history.
	 * <b>Must</b> POP value from the stack after the execution.
	 */
	protected int getArgumentIndex() {
		if (isPrevious == false) {
			throw new ProxettaException("Unexpected previous instruction type used for setting argument index.");
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
				throw new ProxettaException("Unexpected previous instruction used for setting argument index.");
		}
		return argIndex;
	}

	// ---------------------------------------------------------------- visitors

	@Override
	public void visitInsn(int opcode) {
		this.opcode = opcode;
		isPrevious = true;
		traceNext = false;
		super.visitInsn(opcode);
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		this.opcode = opcode;
		this.operand = operand;
		isPrevious = true;
		traceNext = false;
		super.visitIntInsn(opcode, operand);
	}

	@Override
	public void visitVarInsn(int i, int i1) {
		isPrevious = false;
		traceNext = false;
		super.visitVarInsn(i, i1);
	}

	@Override
	public void visitTypeInsn(int i, String string) {
		isPrevious = false;
		traceNext = false;
		super.visitTypeInsn(i, string);
	}

	@Override
	public void visitFieldInsn(int i, String string, String string1, String string2) {
		isPrevious = false;
		traceNext = false;
		super.visitFieldInsn(i, string, string1, string2);
	}

	@Override
	public void visitMethodInsn(int i, String string, String string1, String string2) {
		isPrevious = false;
		traceNext = false;
		super.visitMethodInsn(i, string, string1, string2);
	}

	@Override
	public void visitJumpInsn(int i, Label label) {
		isPrevious = false;
		traceNext = false;
		super.visitJumpInsn(i, label);
	}

	@Override
	public void visitLdcInsn(Object object) {
		isPrevious = false;
		traceNext = false;
		super.visitLdcInsn(object);
	}

	@Override
	public void visitIincInsn(int i, int i1) {
		isPrevious = false;
		traceNext = false;
		super.visitIincInsn(i, i1);
	}

	@Override
	public void visitTableSwitchInsn(int i, int i1, Label label, Label[] labels) {
		isPrevious = false;
		traceNext = false;
		super.visitTableSwitchInsn(i, i1, label, labels);
	}

	@Override
	public void visitLookupSwitchInsn(Label label, int[] ints, Label[] labels) {
		isPrevious = false;
		traceNext = false;
		super.visitLookupSwitchInsn(label, ints, labels);
	}

	@Override
	public void visitMultiANewArrayInsn(String string, int i) {
		isPrevious = false;
		traceNext = false;
		super.visitMultiANewArrayInsn(string, i);
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
	}

	@Override
	public void visitLineNumber(int i, Label label) {
	}
}