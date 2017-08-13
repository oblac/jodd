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
import jodd.util.Wildcard;

import java.lang.annotation.Annotation;

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
	 * Returns a "clean" signature, that is ready for the comparison.
	 * It does not have any generics information.
	 */
	String getCleanSignature();

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

	// ---------------------------------------------------------------- utils

	/**
	 * Returns {@code true} if method is public.
	 */
	default boolean isPublicMethod() {
		return (getAccessFlags() & AsmUtil.ACC_PUBLIC) != 0;
	}

	/**
	 * Returns {@code true} if method is private.
	 */
	default boolean isPrivateMethod() {
		return (getAccessFlags() & AsmUtil.ACC_PRIVATE) != 0;
	}

	/**
	 * Returns <code>true</code> if method has no argument.
	 */
	default boolean hasNoArguments() {
		return getArgumentsCount() == 0;
	}

	/**
	 * Returns <code>true</code> if method has only one argument.
	 */
	default boolean hasOneArgument() {
		return getArgumentsCount() == 1;
	}

	/**
	 * Returns <code>true</code> if method is declared in <code>Object</code> class (root class).
	 */
	default boolean isRootMethod() {
		return AsmUtil.SIGNATURE_JAVA_LANG_OBJECT.equals(getDeclaredClassName());
	}

	// ---------------------------------------------------------------- return


	/**
	 * Returns <code>true</code> if method's return type is <code>void</code>.
	 */
	default boolean hasNoReturnValue() {
		return getReturnOpcodeType() == AsmUtil.TYPE_VOID;
	}

	/**
	 * Returns <code>true</code> if method has a return type.
	 */
	default boolean hasReturnValue() {
		return getReturnOpcodeType() != AsmUtil.TYPE_VOID;
	}

	// ---------------------------------------------------------------- wildcards

	/**
	 * Match method name to provided {@link jodd.util.Wildcard} pattern.
	 */
	default boolean matchMethodName(String wildcard) {
		return Wildcard.match(getMethodName(), wildcard);
	}

	/**
	 * Match class name to provided {@link jodd.util.Wildcard} pattern.
	 */
	default boolean matchClassName(String wildcard) {
		return Wildcard.match(getClassname(), wildcard);
	}

	// ---------------------------------------------------------------- annotations

	/**
	 * Returns <code>true</code> if method is annotated with one of provided annotation.
	 */
	default boolean hasAnnotation(Class<? extends Annotation>... an) {
		AnnotationInfo[] anns = getAnnotations();
		if (anns == null) {
			return false;
		}

		for (Class<? extends Annotation> annotationClass : an) {
			String anName = annotationClass.getName();
			for (AnnotationInfo ann : anns) {
				if (ann.getAnnotationClassname().equals(anName)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if method is annotated with provided annotation.
	 */
	default AnnotationInfo getAnnotation(Class<? extends Annotation> an) {
		AnnotationInfo[] anns = getAnnotations();
		if (anns == null) {
			return null;
		}
		String anName = an.getName();
		for (AnnotationInfo ann : anns) {
			if (ann.getAnnotationClassname().equals(anName)) {
				return ann;
			}
		}
		return null;
	}


}