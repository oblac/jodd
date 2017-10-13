// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.paramo;

import jodd.asm.EmptyMethodVisitor;
import jodd.util.ArraysUtil;
import jodd.asm5.Label;

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
			if (!name.equals("arg" + currentParam)) {
				debugInfoPresent = true;
			}
			if (signature == null) {
				signature = desc;
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