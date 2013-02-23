// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import jodd.asm4.AnnotationVisitor;
import jodd.asm4.Opcodes;

/**
 * Empty annotation visitor.
 */
public abstract class EmptyAnnotationVisitor extends AnnotationVisitor {

	protected EmptyAnnotationVisitor() {
		super(Opcodes.ASM4);
	}

}
