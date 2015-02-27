// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import jodd.asm5.AnnotationVisitor;
import jodd.asm5.Opcodes;

/**
 * Empty annotation visitor.
 */
public abstract class EmptyAnnotationVisitor extends AnnotationVisitor {

	protected EmptyAnnotationVisitor() {
		super(Opcodes.ASM5);
	}

}
