// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

/**
 * Empty method visitor.
 */
public abstract class EmptyMethodVisitor extends MethodVisitor {

	public EmptyMethodVisitor() {
		super(Opcodes.ASM4);
	}

}