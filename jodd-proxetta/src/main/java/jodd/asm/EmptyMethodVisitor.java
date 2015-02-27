// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import jodd.asm5.MethodVisitor;
import jodd.asm5.Opcodes;

/**
 * Empty method visitor.
 */
public abstract class EmptyMethodVisitor extends MethodVisitor {

	protected EmptyMethodVisitor() {
		super(Opcodes.ASM5);
	}

}