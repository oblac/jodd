// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mutable;

/**
 * Generic mutable value holder for holding objects.
 */
public interface ValueHolder<T> extends ValueProvider<T> {

	/**
	 * Returns value.
	 */
	public T getValue();

	/**
	 * Sets new value.
	 */
	public void setValue(T value);

}