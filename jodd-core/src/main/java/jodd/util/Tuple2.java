// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Simple tuple of two elements. A tuple is a set of values that relate
 * to each other in some way.
 * @see NameValue
 */
public class Tuple2<T1, T2> {

	/**
	 * Shorthand creation.
	 */
	public static <T1, T2> Tuple2<T1, T2> tuple(T1 v1, T2 v2) {
		return new Tuple2<T1, T2>(v1, v2);
	}

	public final T1 v1;
	public final T2 v2;

	/**
	 * Creates new tuple of two elements.
	 */
	public Tuple2(T1 v1, T2 v2) {
		this.v1 = v1;
		this.v2 = v2;
	}

	/**
	 * Returns first value.
	 */
	public T1 v1() {
		return v1;
	}

	/**
	 * Returns second value.
	 */
	public T2 v2() {
		return v2;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Tuple2 tuple2 = (Tuple2) o;

		if (v1 != null ? !v1.equals(tuple2.v1) : tuple2.v1 != null) {
			return false;
		}
		if (v2 != null ? !v2.equals(tuple2.v2) : tuple2.v2 != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = v1 != null ? v1.hashCode() : 0;
		result = 31 * result + (v2 != null ? v2.hashCode() : 0);
		return result;
	}

	public String toString() {
		return "Tuple{" + "v1=" + v1 + ", v2=" + v2 + '}';
	}

}