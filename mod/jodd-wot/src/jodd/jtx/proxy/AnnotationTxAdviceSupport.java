// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx.proxy;

/**
 * Support class for {@link jodd.jtx.proxy.AnnotationTxAdvice}.
 * It represent a connection point from the advice's code and the rest of the application.
 */
public class AnnotationTxAdviceSupport {

	/**
	 * Manager that will be used by advice.
	 */
	public static AnnotationTxAdviceManager manager;

}