// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

/**
 * Base action wrapper stack.
 */
public abstract class BaseActionWrapperStack<T extends ActionWrapper> extends BaseActionWrapper {

	protected Class<? extends T>[] wrappers;

	/**
	 * Constructs an empty wrapper stack that will be configured later,
	 * using setter.
	 */
	protected BaseActionWrapperStack() {
	}

	/**
	 * Constructs an wrapper stack with the given wrappers.
	 */
	protected BaseActionWrapperStack(Class<? extends T>... wrapperClasses) {
		this.wrappers = wrapperClasses;
	}

	/**
	 * Returns an array of wrappers.
	 */
	public Class<? extends T>[] getWrappers() {
		return wrappers;
	}

	/**
	 * Throws an exception, as stack can not be invoked.
	 */
	public Object invoke(ActionRequest actionRequest) throws Exception {
		throw new MadvocException("Wrapper stack can not be invoked.");
	}

}