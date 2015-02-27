// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import jodd.asm5.ClassVisitor;
import jodd.asm5.Opcodes;

/**
 * Empty class visitor.
 */
public abstract class EmptyClassVisitor extends ClassVisitor {

	protected EmptyClassVisitor() {
		super(Opcodes.ASM5);
	}

}