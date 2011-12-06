// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.paramo;

import jodd.asm.EmptyMethodVisitor;
import jodd.util.ArraysUtil;
import org.objectweb.asm.Label;

/**
 * Extracts param information from a method.
 */
final class ParamExtractor extends EmptyMethodVisitor {

	private final int paramCount;
	private final int ignoreCount;
	private MethodParameter[] methodParameters;
	private int currentParam;
	boolean debugInfoPresent;

	ParamExtractor(int ignoreCount, int paramCount) {
		this.ignoreCount = ignoreCount;
		this.paramCount = paramCount;
		this.methodParameters = new MethodParameter[paramCount];
		this.currentParam = 0;
		this.debugInfoPresent = paramCount == 0;		// for 0 params, no need for debug info
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		if ((index >= ignoreCount) && (index < (ignoreCount + paramCount))) {
			if (name.equals("arg" + currentParam) == false) {
				debugInfoPresent = true;
			}
			methodParameters[currentParam] = new MethodParameter(name, signature);
			currentParam++;
		}
	}

	@Override
	public void visitEnd() {
		if (methodParameters.length > currentParam) {
			methodParameters = ArraysUtil.subarray(methodParameters, 0, currentParam);
		}
	}

	MethodParameter[] getMethodParameters() {
		return methodParameters;
	}

}
