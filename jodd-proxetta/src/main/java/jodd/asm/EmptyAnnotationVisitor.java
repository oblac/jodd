// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Empty annotation visitor.
 */
public class EmptyAnnotationVisitor extends AnnotationVisitor {

	public EmptyAnnotationVisitor() {
		super(Opcodes.ASM4);
	}

}
