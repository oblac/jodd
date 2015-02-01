// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import jodd.asm5.ClassVisitor;
import jodd.asm5.Opcodes;

/**
 * An empty ClassVisitor that delegates to another ClassVisitor.
 * This class can be used as a super class to quickly implement useful class
 * adapter classes, just by overriding the necessary methods.
 */
public abstract class ClassAdapter extends ClassVisitor {

	protected ClassAdapter(ClassVisitor cv) {
		super(Opcodes.ASM5, cv);
	}
}
