// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;

/**
 * Empty class visitor.
 */
public class EmptyClassVisitor implements ClassVisitor {
	
	/**
     * Visits the header of the class.
     * 
     * @param version the class version.
     * @param access the class's access flags. This
     *        parameter also indicates if the class is deprecated.
     * @param name the internal name of the class.
     * @param signature the signature of this class. May be <code>null</code> if
     *        the class is not a generic one, and does not extend or implement
     *        generic classes or interfaces.
     * @param superName the internal of name of the super class. For interfaces,
     *        the super class is Object. May be <code>null</code>, but
     *        only for the Object class.
     * @param interfaces the internal names of the class's interfaces. May be
     *        <code>null</code>.
     */
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {}

    /**
     * Visits the source of the class.
     * 
     * @param source the name of the source file from which the class was
     *        compiled. May be <code>null</code>.
     * @param debug additional debug information to compute the correspondance
     *        between source and compiled elements of the class. May be
     *        <code>null</code>.
     */
	public void visitSource(String source, String debug) {}

    /**
     * Visits the enclosing class of the class. This method must be called only
     * if the class has an enclosing class.
     * 
     * @param owner internal name of the enclosing class of the class.
     * @param name the name of the method that contains the class, or
     *        <code>null</code> if the class is not enclosed in a method of its
     *        enclosing class.
     * @param desc the descriptor of the method that contains the class, or
     *        <code>null</code> if the class is not enclosed in a method of its
     *        enclosing class.
     */
	public void visitOuterClass(String owner, String name, String desc) {}

    /**
     * Visits an annotation of the class.
     * 
     * @param desc the class descriptor of the annotation class.
     * @param visible <code>true</code> if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or <code>null</code> if
     *         this visitor is not interested in visiting this annotation.
     */
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {return null;}

    /**
     * Visits a non standard attribute of the class.
     * 
     * @param attr an attribute.
     */
	public void visitAttribute(Attribute attr) {}

    /**
     * Visits information about an inner class. This inner class is not
     * necessarily a member of the class being visited.
     * 
     * @param name the internal name of an inner class.
     * @param outerName the internal name of the class to which the inner class
     *        belongs. May be <code>null</code> for not member classes.
     * @param innerName the (simple) name of the inner class inside its
     *        enclosing class. May be <code>null</code> for anonymous inner
     *        classes.
     * @param access the access flags of the inner class as originally declared
     *        in the enclosing class.
     */
	public void visitInnerClass(String name, String outerName, String innerName, int access) {}

    /**
     * Visits a field of the class.
     * 
     * @param access the field's access flags. This
     *        parameter also indicates if the field is synthetic and/or
     *        deprecated.
     * @param name the field's name.
     * @param desc the field's descriptor.
     * @param signature the field's signature. May be <code>null</code> if the
     *        field's type does not use generic types.
     * @param value the field's initial value. This parameter, which may be
     *        <code>null</code> if the field does not have an initial value, must
     *        be an {@link Integer}, a {@link Float}, a {@link Long}, a
     *        {@link Double} or a {@link String} (for <code>int</code>,
     *        <code>float</code>, <code>long</code> or <code>String</code> fields
     *        respectively). <i>This parameter is only used for static fields</i>.
     *        Its value is ignored for non static fields, which must be
     *        initialized through bytecode instructions in constructors or
     *        methods.
     * @return a visitor to visit field annotations and attributes, or
     *         <code>null</code> if this class visitor is not interested in
     *         visiting these annotations and attributes.
     */
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {return null;}

    /**
     * Visits a method of the class. This method <i>must</i> return a new
     * {@link MethodVisitor} instance (or <code>null</code>) each time it is
     * called, i.e., it should not return a previously returned visitor.
     * 
     * @param access the method's access flags. This
     *        parameter also indicates if the method is synthetic and/or
     *        deprecated.
     * @param name the method's name.
     * @param desc the method's descriptor.
     * @param signature the method's signature. May be <code>null</code> if the
     *        method parameters, return type and exceptions do not use generic
     *        types.
     * @param exceptions the internal names of the method's exception classes. May be
     *        <code>null</code>.
     * @return an object to visit the byte code of the method, or <code>null</code>
     *         if this class visitor is not interested in visiting the code of
     *         this method.
     */
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {return null;}

    /**
     * Visits the end of the class. This method, which is the last one to be
     * called, is used to inform the visitor that all the fields and methods of
     * the class have been visited.
     */
	public void visitEnd() {}	
}
