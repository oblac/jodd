// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import jodd.asm4.MethodVisitor;
import jodd.asm4.Opcodes;

/**
 * Empty method visitor.
 */
public abstract class EmptyMethodVisitor extends MethodVisitor {

	protected EmptyMethodVisitor() {
		super(Opcodes.ASM4);
	}

}