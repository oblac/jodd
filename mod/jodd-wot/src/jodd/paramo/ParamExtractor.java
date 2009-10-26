// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.paramo;

import jodd.asm.EmptyMethodVisitor;
import jodd.util.StringPool;
import org.objectweb.asm.Label;

/**
 * Extracts param information from a method.
 */
final class ParamExtractor extends EmptyMethodVisitor {

	final int paramCount;
	final int ignoreCount;
	final StringBuilder result;
	int currentParam;
	boolean debugInfoPresent;

	ParamExtractor(int ignoreCount, int paramCount) {
		this.ignoreCount = ignoreCount;
		this.paramCount = paramCount;
		this.result = new StringBuilder();
		this.currentParam = 0;
		this.debugInfoPresent = paramCount == 0;		// for 0 params, no need for debug info
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		if ((index >= ignoreCount) && (index < (ignoreCount + paramCount))) {
			if (name.equals("arg" + currentParam) == false) {
				debugInfoPresent = true;
			}
			result.append(',');
			result.append(name);
			currentParam++;
		}
	}

	String getResult() {
		return result.length() != 0 ? result.substring(1) : StringPool.EMPTY;
	}

}
