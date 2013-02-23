// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import jodd.asm4.ClassVisitor;
import jodd.asm4.Opcodes;

/**
 * Empty class visitor.
 */
public abstract class EmptyClassVisitor extends ClassVisitor {

	protected EmptyClassVisitor() {
		super(Opcodes.ASM4);
	}

}