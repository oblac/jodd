// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mutable;

/**
 * Generic mutable value holder for holding objects.
 * $Id$
 */
public class ValueHolder<T> {

	protected T value;

	public ValueHolder() {
	}

	public ValueHolder(T value) {
		this.value = value;
	}

	/**
	 * Returns value.
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Sets new value.
	 */
	public void setValue(T value) {
		this.value = value;
	}

	/**
	 * Returns <code>true</code> if value is <code>null</code>.
	 */
	public boolean isNull() {
		return value == null;
	}

	/**
	 * Simple to-string representation.
	 */
	@Override
	public String toString() {
		if (value == null) {
			return "{" + null + '}';
		}
		return '{' + value.toString() + '}';
	}

}
