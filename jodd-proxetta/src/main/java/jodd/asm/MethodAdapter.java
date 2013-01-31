// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * An empty MethodVisitor that delegates to another MethodVisitor. This class
 * can be used as a super class to quickly implement useful method adapter
 * classes, just by overriding the necessary methods.
 */
public abstract class MethodAdapter extends MethodVisitor {

	public MethodAdapter(MethodVisitor mv) {
		super(Opcodes.ASM4, mv);
	}
}
