package jodd.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Emulates <code>MethodAdapter</code> from ASM 3.
 */
public class MethodAdapter extends MethodVisitor {

	public MethodAdapter(MethodVisitor mv) {
		super(Opcodes.ASM4, mv);
	}
}
