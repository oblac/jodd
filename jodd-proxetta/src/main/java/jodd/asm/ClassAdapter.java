package jodd.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Emulates <code>ClassAdapter</code> from ASM 3.
 */
public class ClassAdapter extends ClassVisitor {

	public ClassAdapter(ClassVisitor cv) {
		super(Opcodes.ASM4, cv);
	}
}
