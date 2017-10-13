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

package jodd.proxetta;

import jodd.asm.AsmUtil;
import jodd.mutable.MutableInteger;

import java.util.ArrayList;
import java.util.List;

/**
 * Information about invocation.
 */
public class InvokeInfo {

	private final String owner;
	private final String className;
	private final String classShortName;
	private final String methodName;
	private final String description;
	private final String returnType;
	private final String signature;
	private final String[] arguments;

	public InvokeInfo(String owner, String methodName, String description) {
		this.owner = owner;
		this.className = owner.replace('/', '.');
		this.methodName = methodName;
		this.description = description;

		// short name
		int ndx = className.lastIndexOf('.');
		classShortName = ndx == -1 ? className : className.substring(ndx + 1);

		// arguments
		List<String> args = new ArrayList<>();
		MutableInteger from = new MutableInteger(1);
		if (description.length() != 0) {
			while (description.charAt(from.value) != ')') {
				String a = AsmUtil.typedescToSignature(description, from);
				args.add(a);
			}
		}

		arguments = new String[args.size()];
		args.toArray(arguments);

		from.value++;
		returnType = description.length() > 0 ?
				AsmUtil.typedescToSignature(description, from) :
				className;

		StringBuilder s = new StringBuilder();
		s.append(returnType).append(' ').append(methodName).append('(');
		for (int i = 0; i < arguments.length; i++) {
			if (i != 0) {
				s.append(',').append(' ');
			}
			String argument = arguments[i];
			s.append(argument);
		}
		s.append(')');

		signature = s.toString();
	}

	// ---------------------------------------------------------------- getters

	/**
	 * Returns bytecode-like class that is method owner.
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * Returns java-like class name.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Returns class short name, without a package.
	 */
	public String getClassShortName() {
		return classShortName;
	}

	/**
	 * Returns method name.
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Returns bytecode-like method description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns java-like return type.
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * Returns java-like method signature.
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * Return arguments count.
	 */
	public int getArgumentsCount() {
		return arguments.length;
	}

	/**
	 * Return java-like argument types.
	 */
	public String[] getArguments() {
		return arguments;
	}

	@Override
	public String toString() {
		return "InvokeInfo: " + signature;
	}
}
