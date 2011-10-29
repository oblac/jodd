// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;

/**
 * Empty method visitor.
 */
public class EmptyMethodVisitor implements MethodVisitor {

	// -------------------------------------------------------------------------
	// Annotations and non standard attributes
	// -------------------------------------------------------------------------

	/**
	 * Visits the default value of this annotation interface method.
	 * 
	 * @return a visitor to the visit the actual default value of this
	 *         annotation interface method, or <code>null</code> if this visitor
	 *         is not interested in visiting this default value. The 'name'
	 *         parameters passed to the methods of this annotation visitor are
	 *         ignored. Moreover, exactly one visit method must be called on this
	 *         annotation visitor, followed by visitEnd.
	 */
	public AnnotationVisitor visitAnnotationDefault() {return null;}

	/**
	 * Visits an annotation of this method.
	 * 
	 * @param desc the class descriptor of the annotation class.
	 * @param visible <code>true</code> if the annotation is visible at runtime.
	 * @return a visitor to visit the annotation values, or <code>null</code> if
	 *         this visitor is not interested in visiting this annotation.
	 */
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {return null;}

	/**
	 * Visits an annotation of a parameter this method.
	 * 
	 * @param parameter the parameter index.
	 * @param desc the class descriptor of the annotation class.
	 * @param visible <code>true</code> if the annotation is visible at runtime.
	 * @return a visitor to visit the annotation values, or <code>null</code> if
	 *         this visitor is not interested in visiting this annotation.
	 */
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {return null;}
	
	/**
	 * Visits a non standard attribute of this method.
	 * 
	 * @param attr an attribute.
	 */
	public void visitAttribute(Attribute attr) {}

	/**
	 * Starts the visit of the method's code, if any (i.e. non abstract method).
	 */
	public void visitCode() {}

	/**
	 * Visits the current state of the local variables and operand stack
	 * elements. This method must(*) be called <i>just before</i> any
	 * instruction <b>i</b> that follows an unconditional branch instruction
	 * such as GOTO or THROW, that is the target of a jump instruction, or that
	 * starts an exception handler block. The visited types must describe the
	 * values of the local variables and of the operand stack elements <i>just
	 * before</i> <b>i</b> is executed. <br> <br> (*) this is mandatory only
	 * for classes whose version is greater than or equal to
	 * V1_6. <br> <br> Packed frames are basically
	 * "deltas" from the state of the previous frame (very first frame is
	 * implicitly defined by the method's parameters and access flags): <ul>
	 * <liOpcodes#F_SAME representing frame with exactly the same
	 * locals as the previous frame and with the empty stack.</li> <li>Opcodes#F_SAME1
	 * representing frame with exactly the same locals as the previous frame and
	 * with single value on the stack (<code>nStack</code> is 1 and
	 * <code>stack[0]</code> contains value for the type of the stack item).</li>
	 * <li>Opcodes#F_APPEND representing frame with current locals are
	 * the same as the locals in the previous frame, except that additional
	 * locals are defined (<code>nLocal</code> is 1, 2 or 3 and
	 * <code>local</code> elements contains values representing added types).</li>
	 * <li>Opcodes#F_CHOP representing frame with current locals are
	 * the same as the locals in the previous frame, except that the last 1-3
	 * locals are absent and with the empty stack (<code>nLocals</code> is 1,
	 * 2 or 3). </li> <li>Opcodes#F_FULL representing complete frame
	 * data.</li> </li> </ul>
	 * 
	 * @param type the type of this stack map frame. Must be
	 *        Opcodes#F_NEW for expanded frames, or
	 *        Opcodes#F_FULL, Opcodes#F_APPEND,
	 *        Opcodes#F_CHOP, Opcodes#F_SAME or
	 *        Opcodes#F_APPEND, Opcodes#F_SAME1 for compressed
	 *        frames.
	 * @param nLocal the number of local variables in the visited frame.
	 * @param local the local variable types in this frame. This array must not
	 *        be modified. Primitive types are represented by
	 *        Opcodes#TOP, Opcodes#INTEGER,
	 *        Opcodes#FLOAT, Opcodes#LONG,
	 *        Opcodes#DOUBLE,Opcodes#NULL or
	 *        Opcodes#UNINITIALIZED_THIS (long and double are
	 *        represented by a single element). Reference types are represented
	 *        by String objects (representing internal names), and uninitialized 
	 *        types by Label objects (this label designates the NEW instruction 
	 *        that created this uninitialized value).
	 * @param nStack the number of operand stack elements in the visited frame.
	 * @param stack the operand stack types in this frame. This array must not
	 *        be modified. Its content has the same format as the "local" array.
	 */
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {}

	// -------------------------------------------------------------------------
	// Normal instructions
	// -------------------------------------------------------------------------

	/**
	 * Visits a zero operand instruction.
	 * 
	 * @param opcode the opcode of the instruction to be visited. This opcode is
	 *        either NOP, ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1, ICONST_2,
	 *        ICONST_3, ICONST_4, ICONST_5, LCONST_0, LCONST_1, FCONST_0,
	 *        FCONST_1, FCONST_2, DCONST_0, DCONST_1, IALOAD, LALOAD, FALOAD,
	 *        DALOAD, AALOAD, BALOAD, CALOAD, SALOAD, IASTORE, LASTORE, FASTORE,
	 *        DASTORE, AASTORE, BASTORE, CASTORE, SASTORE, POP, POP2, DUP,
	 *        DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2, SWAP, IADD, LADD, FADD,
	 *        DADD, ISUB, LSUB, FSUB, DSUB, IMUL, LMUL, FMUL, DMUL, IDIV, LDIV,
	 *        FDIV, DDIV, IREM, LREM, FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL,
	 *        LSHL, ISHR, LSHR, IUSHR, LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR,
	 *        I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B,
	 *        I2C, I2S, LCMP, FCMPL, FCMPG, DCMPL, DCMPG, IRETURN, LRETURN,
	 *        FRETURN, DRETURN, ARETURN, RETURN, ARRAYLENGTH, ATHROW,
	 *        MONITORENTER, or MONITOREXIT.
	 */
	public void visitInsn(int opcode) {}

	/**
	 * Visits an instruction with a single int operand.
	 * 
	 * @param opcode the opcode of the instruction to be visited. This opcode is
	 *        either BIPUSH, SIPUSH or NEWARRAY.
	 * @param operand the operand of the instruction to be visited.<br> When
	 *        opcode is BIPUSH, operand value should be between Byte.MIN_VALUE
	 *        and Byte.MAX_VALUE.<br> When opcode is SIPUSH, operand value
	 *        should be between Short.MIN_VALUE and Short.MAX_VALUE.<br> When
	 *        opcode is NEWARRAY, operand value should be one of
	 *        Opcodes#T_BOOLEAN}, Opcodes#T_CHAR},
	 *        Opcodes#T_FLOAT}, Opcodes#T_DOUBLE},
	 *        Opcodes#T_BYTE}, Opcodes#T_SHORT},
	 *        Opcodes#T_INT} or Opcodes#T_LONG}.
	 */
	public void visitIntInsn(int opcode, int operand) {}

	/**
	 * Visits a local variable instruction. A local variable instruction is an
	 * instruction that loads or stores the value of a local variable.
	 * 
	 * @param opcode the opcode of the local variable instruction to be visited.
	 *        This opcode is either ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE,
	 *        LSTORE, FSTORE, DSTORE, ASTORE or RET.
	 * @param var the operand of the instruction to be visited. This operand is
	 *        the index of a local variable.
	 */
	public void visitVarInsn(int opcode, int var) {}

	/**
	 * Visits a type instruction. A type instruction is an instruction that
	 * takes the internal name of a class as parameter.
	 * 
	 * @param opcode the opcode of the type instruction to be visited. This
	 *        opcode is either NEW, ANEWARRAY, CHECKCAST or INSTANCEOF.
	 * @param type the operand of the instruction to be visited. This operand
	 *        must be the internal name of an object or array class.
	 */
	public void visitTypeInsn(int opcode, String type) {}

	/**
	 * Visits a field instruction. A field instruction is an instruction that
	 * loads or stores the value of a field of an object.
	 * 
	 * @param opcode the opcode of the type instruction to be visited. This
	 *        opcode is either GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
	 * @param owner the internal name of the field's owner class.
	 * @param name the field's name.
	 * @param desc the field's descriptor.
	 */
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {}

	/**
	 * Visits a method instruction. A method instruction is an instruction that
	 * invokes a method.
	 * 
	 * @param opcode the opcode of the type instruction to be visited. This
	 *        opcode is either INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC,
	 *        INVOKEINTERFACE or INVOKEDYNAMIC.
	 * @param owner the internal name of the method's owner class.
	 * @param name the method's name.
	 * @param desc the method's descriptor.
	 */
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {}

	/**
	 * Visits a jump instruction. A jump instruction is an instruction that may
	 * jump to another instruction.
	 * 
	 * @param opcode the opcode of the type instruction to be visited. This
	 *        opcode is either IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ,
	 *        IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ,
	 *        IF_ACMPNE, GOTO, JSR, IFNULL or IFNONNULL.
	 * @param label the operand of the instruction to be visited. This operand
	 *        is a label that designates the instruction to which the jump
	 *        instruction may jump.
	 */
	public void visitJumpInsn(int opcode, Label label) {}

	/**
	 * Visits a label. A label designates the instruction that will be visited
	 * just after it.
	 * 
	 * @param label a {@link Label Label} object.
	 */
	public void visitLabel(Label label) {}

	// -------------------------------------------------------------------------
	// Special instructions
	// -------------------------------------------------------------------------

	/**
	 * Visits a LDC instruction.
	 * 
	 * @param cst the constant to be loaded on the stack. This parameter must be
	 *        a non null {@link Integer}, a {@link Float}, a {@link Long}, a
	 *        {@link Double} a {@link String} (or a Type for
	 *        <code>.class</code> constants, for classes whose version is 49.0 or
	 *        more).
	 */
	public void visitLdcInsn(Object cst) {}

	/**
	 * Visits an IINC instruction.
	 * 
	 * @param var index of the local variable to be incremented.
	 * @param increment amount to increment the local variable by.
	 */
	public void visitIincInsn(int var, int increment) {}

	/**
	 * Visits a TABLESWITCH instruction.
	 * 
	 * @param min the minimum key value.
	 * @param max the maximum key value.
	 * @param dflt beginning of the default handler block.
	 * @param labels beginnings of the handler blocks. <code>labels[i]</code> is
	 *        the beginning of the handler block for the <code>min + i</code> key.
	 */
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {}

	/**
	 * Visits a LOOKUPSWITCH instruction.
	 * 
	 * @param dflt beginning of the default handler block.
	 * @param keys the values of the keys.
	 * @param labels beginnings of the handler blocks. <code>labels[i]</code> is
	 *        the beginning of the handler block for the <code>keys[i]</code> key.
	 */
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {}

	/**
	 * Visits a MULTIANEWARRAY instruction.
	 * 
	 * @param desc an array type descriptor.
	 * @param dims number of dimensions of the array to allocate.
	 */
	public void visitMultiANewArrayInsn(String desc, int dims) {}

	// -------------------------------------------------------------------------
	// Exceptions table entries, debug information, max stack and max locals
	// -------------------------------------------------------------------------

	/**
	 * Visits a try catch block.
	 * 
	 * @param start beginning of the exception handler's scope (inclusive).
	 * @param end end of the exception handler's scope (exclusive).
	 * @param handler beginning of the exception handler's code.
	 * @param type internal name of the type of exceptions handled by the
	 *        handler, or <code>null</code> to catch any exceptions (for "finally"
	 *        blocks).
	 * @throws IllegalArgumentException if one of the labels has already been
	 *         visited by this visitor (by the {@link #visitLabel visitLabel}
	 *         method).
	 */
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {}

	/**
	 * Visits a local variable declaration.
	 * 
	 * @param name the name of a local variable.
	 * @param desc the type descriptor of this local variable.
	 * @param signature the type signature of this local variable. May be
	 *        <code>null</code> if the local variable type does not use generic
	 *        types.
	 * @param start the first instruction corresponding to the scope of this
	 *        local variable (inclusive).
	 * @param end the last instruction corresponding to the scope of this local
	 *        variable (exclusive).
	 * @param index the local variable's index.
	 * @throws IllegalArgumentException if one of the labels has not already
	 *         been visited by this visitor (by the
	 *         {@link #visitLabel visitLabel} method).
	 */
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {}

	/**
	 * Visits a line number declaration.
	 * 
	 * @param line a line number. This number refers to the source file from
	 *        which the class was compiled.
	 * @param start the first instruction corresponding to this line number.
	 * @throws IllegalArgumentException if <code>start</code> has not already been
	 *         visited by this visitor (by the {@link #visitLabel visitLabel}
	 *         method).
	 */
	public void visitLineNumber(int line, Label start) {}

	/**
	 * Visits the maximum stack size and the maximum number of local variables
	 * of the method.
	 * 
	 * @param maxStack maximum stack size of the method.
	 * @param maxLocals maximum number of local variables for the method.
	 */
	public void visitMaxs(int maxStack, int maxLocals) {}

	/**
	 * Visits the end of the method. This method, which is the last one to be
	 * called, is used to inform the visitor that all the annotations and
	 * attributes of the method have been visited.
	 */
	public void visitEnd() {}
}
