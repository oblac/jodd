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
import jodd.util.StringUtil;
import jodd.util.Wildcard;

import java.lang.annotation.Annotation;

/**
 * Method info provides various information about a method. There are two types
 * of information:
 * <ul>
 *     <li>java-like, user-readable, that matches the java code, where, for example,
 *     	   packages in class names are separated with a dot;
 *     </li>
 *     <li>
 *         bytecode-related, where information is more suitable for bytecodes,
 *         where, for example, packages in class names are separated with a slash.
 *     </li>
 * </ul>
 */
public interface MethodInfo {

	/**
	 * Returns full java-like declaration of method's arguments and return type.
	 * For example: {@code (long, java.lang.Integer)int}.
	 * @see #getSignature()
	 */
	String getDeclaration();

	/**
	 * Returns return {@link TypeInfo type information}.
	 */
	TypeInfo getReturnType();

	/**
	 * Returns exceptions as a comma-seaprated string.
	 * @see #getExceptions()
	 */
	default String getExceptionsAsString() {
		if (getExceptions() == null) {
			return null;
		}

		return StringUtil.join(getExceptions(),',');
	}

	/**
	 * Returns array of exceptions or {@code null} if no exception is declared.
	 */
	String[] getExceptions();

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
	 * Returns method name.
	 */
	String getMethodName();

	/**
	 * Returns the number of methods arguments.
	 */
	int getArgumentsCount();

	/**
	 * Returns bytecode offset of an argument in local variables.
	 */
	int getArgumentOffset(int index);

	/**
	 * Returns methods argument (1-indexed).
	 */
	TypeInfo getArgument(int index);

	/**
	 * Returns the size of all arguments on stack.
	 * It is not equal to argument count, because some types
	 * takes 2 places, like <code>long</code>.
	 */
	int getAllArgumentsSize();

	/**
	 * Returns methods access flags.
	 */
	int getAccessFlags();

	/**
	 * Returns bytecode-like class name, where packages are separated by a slash.
	 * For example: {@code org/jodd/Jodd}
	 */
	String getClassname();

	/**
	 * Returns bytecode-like method description.
	 * @see #getSignature()
	 */
	String getDescription();

	/**
	 * Returns annotation information, if there is any.
	 */
	AnnotationInfo[] getAnnotations();

	/**
	 * Returns declared class name for inner methods or
	 * {@link #getClassname() classname} for top-level methods.
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
		return getReturnType().getOpcode() == AsmUtil.TYPE_VOID;
	}

	/**
	 * Returns <code>true</code> if method has a return type.
	 */
	default boolean hasReturnValue() {
		return getReturnType().getOpcode() != AsmUtil.TYPE_VOID;
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