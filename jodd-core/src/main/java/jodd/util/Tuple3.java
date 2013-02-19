// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Simple tuple of three elements. A tuple is a set of values that relate
 * to each other in some way.
 */
public class Tuple3<T1, T2, T3> {

	/**
	 * Shorthand creation.
	 */
	public static <T1, T2, T3> Tuple3<T1, T2, T3> tuple(T1 v1, T2 v2, T3 v3) {
		return new Tuple3<T1, T2, T3>(v1, v2, v3);
	}

	public final T1 v1;
	public final T2 v2;
	public final T3 v3;

	/**
	 * Creates new tuple of three elements.
	 */
	public Tuple3(T1 v1, T2 v2, T3 v3) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
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

	/**
	 * Returns third value.
	 */
	public T3 v3() {
		return v3;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Tuple3 tuple3 = (Tuple3) o;

		if (v1 != null ? !v1.equals(tuple3.v1) : tuple3.v1 != null) {
			return false;
		}
		if (v2 != null ? !v2.equals(tuple3.v2) : tuple3.v2 != null) {
			return false;
		}
		if (v3 != null ? !v3.equals(tuple3.v3) : tuple3.v3 != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = v1 != null ? v1.hashCode() : 0;
		result = 31 * result + (v2 != null ? v2.hashCode() : 0);
		result = 31 * result + (v3 != null ? v3.hashCode() : 0);
		return result;
	}

	public String toString() {
		return "Tuple{" + "v1=" + v1 + ", v2=" + v2 + ", v3=" + v3 + '}';
	}

}