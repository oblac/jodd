// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mutable;

/**
 * Creates {@link jodd.mutable.ValueHolder} instances.
 */
public class ValueHolderWrapper {

	/**
	 * Creates new empty {@link jodd.mutable.ValueHolder}.
	 */
	public static <T> ValueHolder<T> create() {
		return new ValueHolderImpl<T>(null);
	}

	/**
	 * Wraps existing instance to {@link jodd.mutable.ValueHolder}.
	 */
	public static <T> ValueHolder<T> wrap(final T value) {
		return new ValueHolderImpl<T>(value);
	}

	static class ValueHolderImpl<T> implements ValueHolder<T> {

		T value;

		ValueHolderImpl(T v) {
			this.value = v;
		}

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}

		/**
		 * Simple to-string representation.
		 */
		@Override
		public String toString() {
			if (value == null) {
				return "{null}";
			}
			return '{' + value.toString() + '}';
		}

	}
}