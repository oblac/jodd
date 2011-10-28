// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

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
		List<String> args = new ArrayList<String>();
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
