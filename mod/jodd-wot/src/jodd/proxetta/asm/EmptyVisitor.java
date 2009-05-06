// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * Empty field, method and class visitor.
 */
public class EmptyVisitor implements MethodVisitor, ClassVisitor, FieldVisitor {

	// ---------------------------------------------------------------- class visitor add-ons

	public void visit(int i, int i1, String string, String string1, String string2, String[] strings) {
	}

	public void visitSource(String string, String string1) {
	}

	public void visitOuterClass(String string, String string1, String string2) {

	}

	public void visitInnerClass(String string, String string1, String string2, int i) {
	}

	public FieldVisitor visitField(int i, String string, String string1, String string2, Object object) {
		return null;
	}

	public MethodVisitor visitMethod(int i, String string, String string1, String string2, String[] strings) {
		return null;
	}

	// ---------------------------------------------------------------- method visitor only

	public AnnotationVisitor visitAnnotationDefault() {
		return null;
	}

	public AnnotationVisitor visitAnnotation(String string, boolean b) {
		return null;
	}

	public AnnotationVisitor visitParameterAnnotation(int i, String string, boolean b) {
		return null;
	}

	public void visitAttribute(Attribute attribute) {
	}

	public void visitCode() {
	}

	public void visitFrame(int i, int i1, Object[] objects, int i2, Object[] objects1) {
	}

	public void visitInsn(int i) {
	}

	public void visitIntInsn(int i, int i1) {
	}

	public void visitVarInsn(int i, int i1) {
	}

	public void visitTypeInsn(int i, String string) {
	}

	public void visitFieldInsn(int i, String string, String string1, String string2) {
	}

	public void visitMethodInsn(int i, String string, String string1, String string2) {
	}

	public void visitJumpInsn(int i, Label label) {
	}

	public void visitLabel(Label label) {
	}

	public void visitLdcInsn(Object object) {
	}

	public void visitIincInsn(int i, int i1) {
	}

	public void visitTableSwitchInsn(int i, int i1, Label label, Label[] labels) {
	}

	public void visitLookupSwitchInsn(Label label, int[] ints, Label[] labels) {
	}

	public void visitMultiANewArrayInsn(String string, int i) {
	}

	public void visitTryCatchBlock(Label label, Label label1, Label label2, String string) {
	}

	public void visitLocalVariable(String string, String string1, String string2, Label label, Label label1, int i) {
	}

	public void visitLineNumber(int i, Label label) {
	}

	public void visitMaxs(int i, int i1) {
	}

	public void visitEnd() {
	}
}
