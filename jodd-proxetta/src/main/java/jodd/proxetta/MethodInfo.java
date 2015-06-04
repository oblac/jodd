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

/**
 * Method info provides various information about a method.
 */
public interface MethodInfo {

	/**
	 * Returns full java-like method arguments declaration.
	 * @see #getSignature()
	 */
	String getDeclaration();

	/**
	 * Returns java-like return type.
	 */
	String getReturnType();

	/**
	 * Returns type name for return type.
	 */
	String getReturnTypeName();

	/**
	 * Returns list of exceptions.
	 */
	String getExceptions();

	/**
	 * Returns java-like method signature of @{link #getDescription description}.
	 * Does not contain any generic information.
	 */
	String getSignature();

	/**
	 * Returns raw bytecode signature or <code>null</code> if not present.
	 * @see #getDescription()
	 */
	public String getRawSignature();

	/**
	 * Returns method name.
	 */
	String getMethodName();

	/**
	 * Returns number of method arguments.
	 */
	int getArgumentsCount();

	char getArgumentOpcodeType(int index);

	/**
	 * Returns type name of given argument.
	 */
	String getArgumentTypeName(int index);

	/**
	 * Returns offset of an argument in local variables.
	 */
	int getArgumentOffset(int index);

	/**
	 * Returns annotations for given argument.
	 */
	AnnotationInfo[] getArgumentAnnotations(int index);

	/**
	 * Returns size of all arguments on stack.
	 * It is not equal to argument count, as some types
	 * takes 2 places, like <code>long</code>.
	 */
	int getAllArgumentsSize();

	/**
	 * Returns return type opcode.
	 * For example, returns 'V' for void etc.
	 */
	char getReturnOpcodeType();

	/**
	 * Returns method access flags.
	 */
	int getAccessFlags();

	/**
	 * Returns bytecode-like class name.
	 */
	String getClassname();

	/**
	 * Returns bytecode-like method description.
	 * @see #getSignature()
	 * @see #getRawSignature()
	 */
	String getDescription();

	/**
	 * Returns annotation infos, if there is any.
	 */
	AnnotationInfo[] getAnnotations();

	/**
	 * Returns declared class name for inner methods or {@link #getClassname() classname} for top-level methods.
	 */
	String getDeclaredClassName();

	/**
	 * Returns <code>true</code> if method is declared in top-level class.
	 */
	boolean isTopLevelMethod();

	/**
	 * Returns target {@link jodd.proxetta.ClassInfo class informations}.
	 */
	ClassInfo getClassInfo();

}