// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.pointcuts;

import jodd.proxetta.ProxyPointcut;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.AnnotationData;
import jodd.proxetta.AnnotationInfo;
import jodd.proxetta.ClassInfo;
import jodd.util.Wildcard;

import java.util.List;
import java.lang.annotation.Annotation;

/**
 * {@link jodd.proxetta.ProxyPointcut} support methods.
 */
public abstract class ProxyPointcutSupport implements ProxyPointcut {

	protected static final String SIGNATURE_OBJECT_CLASS = "java/lang/Object";

	/**
	 * Returns <code>true</code> if method is public.
	 */
	public boolean isPublic(MethodInfo msign) {
		return (msign.getAccessFlags() & MethodInfo.ACC_PUBLIC) != 0;
	}

	/**
	 * Returns <code>true</code> if method is annotated with provided annotation.
	 */
	public boolean hasAnnotation(MethodInfo msign, String annotationName) {
		List<AnnotationData> anns = msign.getAnnotations();
		for (AnnotationData a : anns) {
			if (annotationName.equals(a.declaration)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Locates annotation in class info.
	 */
	public AnnotationInfo lookupAnnotation(ClassInfo ci, Class<? extends Annotation> an) {
		AnnotationInfo[] anns = ci.getAnnotations();
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
	 * Returns <code>true</code> if method has no arguments.
	 */
	public boolean hasNoArguments(MethodInfo msign) {
		return msign.getArgumentsCount() == 0;
	}

	/**
	 * Returns <code>true</code> if method has only one argument.
	 */
	public boolean hasOneArgument(MethodInfo msign) {
		return msign.getArgumentsCount() == 1;
	}

	// ---------------------------------------------------------------- methods level

	/**
	 * Returns <code>true</code> if method is a top-level method.
	 */
	public boolean isTopLevelMethod(MethodInfo msign) {
		return msign.isTopLevelMethod();
	}

	/**
	 * Returns <code>true</code> if method is declared in <code>Object</code> class (root class).
	 */
	public boolean isRootMethod(MethodInfo msign) {
		return SIGNATURE_OBJECT_CLASS.equals(msign.getDeclaredClassName());
	}


	// ---------------------------------------------------------------- wildcards

	/**
	 * Match method name to provided {@link jodd.util.Wildcard} pattern.
	 */
	public boolean matchMethodName(MethodInfo msing, String wildcard) {
		return Wildcard.match(msing.getMethodName(), wildcard);
	}

	/**
	 * Match class name to provided {@link jodd.util.Wildcard} pattern.
	 */
	public boolean matchClassName(MethodInfo msing, String wildcard) {
		return Wildcard.match(msing.getClassname(), wildcard);
	}


	// ---------------------------------------------------------------- return


	/**
	 * Returns <code>true</code> if method's return type is <code>void</code>.
	 */
	public boolean hasNoReturnValue(MethodInfo msign) {
		return msign.getReturnOpcodeType() == MethodInfo.TYPE_VOID;
	}

	/**
	 * Returns <code>true</code> if method has a return type.
	 */
	public boolean hasReturnValue(MethodInfo msign) {
		return msign.getReturnOpcodeType() != MethodInfo.TYPE_VOID;
	}



	// ---------------------------------------------------------------- logical joins

	/**
	 * Returns <code>true</code> if both pointcuts can be applied on the method..
	 */
	public boolean and(MethodInfo msign, ProxyPointcut p1, ProxyPointcut p2) {
		return p1.apply(msign) && p2.apply(msign);
	}

	/**
	 * Returns <code>true</code> if at least one pointcuts can be applied on the method..
	 */
	public boolean or(MethodInfo msign, ProxyPointcut p1, ProxyPointcut p2) {
		return p1.apply(msign) || p2.apply(msign);
	}

}
