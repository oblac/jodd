// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Opcodes;

/**
 * Empty class visitor.
 */
public class EmptyClassVisitor extends ClassVisitor {

	public EmptyClassVisitor() {
		super(Opcodes.ASM4);
	}

}