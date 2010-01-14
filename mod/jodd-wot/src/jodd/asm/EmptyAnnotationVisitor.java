// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import org.objectweb.asm.AnnotationVisitor;

/**
 * Empty annotation visitor.
 */
public class EmptyAnnotationVisitor implements AnnotationVisitor {

	/**
	 * Visits a primitive value of the annotation.
	 *
	 * @param name the value name.
	 * @param value the actual value, whose type must be <code>Byte</code>,
	 * <code>Boolean</code>, <code>Character</code>, <code>Short</code>,
	 * <code>Integer</code>, <code>Long</code>, <code>Float</code>, <code>Double</code>,
	 * <code>String</code> or <code>Type</code>. This value can also be an array
	 * of byte, boolean, short, char, int, long, float or double values
	 * (this is equivalent to using {@link #visitArray visitArray} and
	 * visiting each array element in turn, but is more convenient).
	 */
	public void visit(String name, Object value) {}

	/**
	 * Visits an enumeration value of the annotation.
	 *
	 * @param name the value name.
	 * @param desc the class descriptor of the enumeration class.
	 * @param value the actual enumeration value.
	 */
	public void visitEnum(String name, String desc, String value) {}

	/**
	 * Visits a nested annotation value of the annotation.
	 *
	 * @param name the value name.
	 * @param desc the class descriptor of the nested annotation class.
	 * @return a visitor to visit the actual nested annotation value, or
	 *         <code>null</code> if this visitor is not interested in visiting
	 *         this nested annotation. <i>The nested annotation value must be
	 *         fully visited before calling other methods on this annotation
	 *         visitor</i>.
	 */
	public AnnotationVisitor visitAnnotation(String name, String desc) {return this;}

	/**
	 * Visits an array value of the annotation. Note that arrays of primitive
	 * types (such as byte, boolean, short, char, int, long, float or double)
	 * can be passed as value to <code>#visit visit}. This is what
	 * <code>ClassReader} does.
	 *
	 * @param name the value name.
	 * @return a visitor to visit the actual array value elements, or
	 *         <code>null</code> if this visitor is not interested in visiting
	 *         these values. The 'name' parameters passed to the methods of this
	 *         visitor are ignored. <i>All the array values must be visited
	 *         before calling other methods on this annotation visitor</i>.
	 */
	public AnnotationVisitor visitArray(String name) {return this;}

	/**
	 * Visits the end of the annotation.
	 */
	public void visitEnd() {}
}
