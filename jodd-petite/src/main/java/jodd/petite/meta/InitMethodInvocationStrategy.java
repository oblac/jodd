// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.meta;

/**
 * Invocation strategy for init methods define moment
 * when init methods are invoked.
 */
public enum InitMethodInvocationStrategy {

	/**
	 * Init methods are invoked right after class is created, i.e.
	 * after constructor is called and before bean is wired.
	 */
	POST_CONSTRUCT,

	/**
	 * Init methods are invoked after the wiring and before parameters
	 * are injected.
	 */
	POST_DEFINE,

	/**
	 * Init methods are invoked after bean is completely initialized,
	 * i.e. after wiring and parameters injection. Default strategy.
	 */
	POST_INITIALIZE

}