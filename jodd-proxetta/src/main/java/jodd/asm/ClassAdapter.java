// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import jodd.asm4.ClassVisitor;
import jodd.asm4.Opcodes;

/**
 * An empty ClassVisitor that delegates to another ClassVisitor.
 * This class can be used as a super class to quickly implement useful class
 * adapter classes, just by overriding the necessary methods.
 */
public abstract class ClassAdapter extends ClassVisitor {

	protected ClassAdapter(ClassVisitor cv) {
		super(Opcodes.ASM4, cv);
	}
}
