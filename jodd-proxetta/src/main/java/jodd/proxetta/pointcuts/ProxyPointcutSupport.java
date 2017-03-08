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

package jodd.proxetta.pointcuts;

import jodd.asm.AsmUtil;
import jodd.proxetta.ProxyPointcut;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.AnnotationInfo;
import jodd.proxetta.ClassInfo;
import jodd.proxetta.asm.ProxettaAsmUtil;
import jodd.util.Wildcard;

import java.lang.annotation.Annotation;

/**
 * {@link jodd.proxetta.ProxyPointcut} support methods.
 */
public abstract class ProxyPointcutSupport implements ProxyPointcut {

	/**
	 * Returns <code>true</code> if method is public.
	 */
	public boolean isPublic(MethodInfo methodInfo) {
		return (methodInfo.getAccessFlags() & AsmUtil.ACC_PUBLIC) != 0;
	}

	/**
	 * Returns <code>true</code> if method is annotated with provided annotation.
	 */
	public AnnotationInfo getAnnotation(MethodInfo methodInfo, Class<? extends Annotation> mi) {
		AnnotationInfo[] anns = methodInfo.getAnnotations();
		if (anns == null) {
			return null;
		}
		String anName = mi.getName();
		for (AnnotationInfo ann : anns) {
			if (ann.getAnnotationClassname().equals(anName)) {
				return ann;
			}
		}
		return null;
	}

	/**
	 * Returns <code>true</code> if method is annotated with one of provided annotation.
	 */
	public boolean hasAnnotation(MethodInfo methodInfo, Class<? extends Annotation>... an) {
		AnnotationInfo[] anns = methodInfo.getAnnotations();
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
	 * Finds annotation in class info. Returns <code>null</code> if annotation doesn't exist.
	 */
	public AnnotationInfo getAnnotation(ClassInfo classInfo, Class<? extends Annotation> an) {
		AnnotationInfo[] anns = classInfo.getAnnotations();
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

	/**
	 * Returns <code>true</code> if class is annotated with one of provided annotation.
	 */
	public boolean hasAnnotation(ClassInfo classInfo, Class<? extends Annotation>... an) {
		AnnotationInfo[] anns = classInfo.getAnnotations();
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
	 * Returns <code>true</code> if method has no arguments.
	 */
	public boolean hasNoArguments(MethodInfo methodInfo) {
		return methodInfo.getArgumentsCount() == 0;
	}

	/**
	 * Returns <code>true</code> if method has only one argument.
	 */
	public boolean hasOneArgument(MethodInfo methodInfo) {
		return methodInfo.getArgumentsCount() == 1;
	}

	// ---------------------------------------------------------------- methods level

	/**
	 * Returns <code>true</code> if method is a top-level method.
	 */
	public boolean isTopLevelMethod(MethodInfo methodInfo) {
		return methodInfo.isTopLevelMethod();
	}

	/**
	 * Returns <code>true</code> if method is declared in <code>Object</code> class (root class).
	 */
	public boolean isRootMethod(MethodInfo methodInfo) {
		return AsmUtil.SIGNATURE_JAVA_LANG_OBJECT.equals(methodInfo.getDeclaredClassName());
	}

	/**
	 * Returns <code>true</code> if method is constructor or static block.
	 */
	public boolean isSpecialMethod(MethodInfo methodInfo) {
		return
				methodInfo.getMethodName().equals(ProxettaAsmUtil.INIT) ||
				methodInfo.getMethodName().equals(ProxettaAsmUtil.CLINIT);
	}


	// ---------------------------------------------------------------- wildcards

	/**
	 * Match method name to provided {@link jodd.util.Wildcard} pattern.
	 */
	public boolean matchMethodName(MethodInfo methodInfo, String wildcard) {
		return Wildcard.match(methodInfo.getMethodName(), wildcard);
	}

	/**
	 * Match class name to provided {@link jodd.util.Wildcard} pattern.
	 */
	public boolean matchClassName(MethodInfo methodInfo, String wildcard) {
		return Wildcard.match(methodInfo.getClassname(), wildcard);
	}


	// ---------------------------------------------------------------- return


	/**
	 * Returns <code>true</code> if method's return type is <code>void</code>.
	 */
	public boolean hasNoReturnValue(MethodInfo methodInfo) {
		return methodInfo.getReturnOpcodeType() == AsmUtil.TYPE_VOID;
	}

	/**
	 * Returns <code>true</code> if method has a return type.
	 */
	public boolean hasReturnValue(MethodInfo methodInfo) {
		return methodInfo.getReturnOpcodeType() != AsmUtil.TYPE_VOID;
	}



	// ---------------------------------------------------------------- logical joins

	/**
	 * Returns <code>true</code> if both pointcuts can be applied on the method..
	 */
	public boolean and(MethodInfo methodInfo, ProxyPointcut p1, ProxyPointcut p2) {
		return p1.apply(methodInfo) && p2.apply(methodInfo);
	}

	/**
	 * Returns <code>true</code> if at least one pointcuts can be applied on the method..
	 */
	public boolean or(MethodInfo methodInfo, ProxyPointcut p1, ProxyPointcut p2) {
		return p1.apply(methodInfo) || p2.apply(methodInfo);
	}

}