// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mutable;

/**
 * A mutable <code>short</code> wrapper.
 */
public final class MutableShort extends Number implements Comparable<MutableShort>, Cloneable {

	public MutableShort() {
	}

	public MutableShort(short value) {
		this.value = value;
	}

	public MutableShort(String value) {
		this.value = Short.parseShort(value);
	}

	public MutableShort(Number number) {
		this.value = number.shortValue();
	}

	// ---------------------------------------------------------------- value

	/**
	 * The mutable value.
	 */
	public short value;

	/**
	 * Returns mutable value.
	 */
	public short getValue() {
		return value;
	}

	/**
	 * Sets mutable value.
	 */
	public void setValue(short value) {
		this.value = value;
	}

	/**
	 * Sets mutable value from a Number.
	 */
	public void setValue(Number value) {
		this.value = value.shortValue();
	}

	// ---------------------------------------------------------------- object

	/**
	 * Stringify the value.
	 */
	@Override
	public String toString() {
		return Integer.toString(value);
	}

	/**
	 * Returns a hashcode for this value.
	 */
	@Override
	public int hashCode() {
		return value;
	}

	/**
	 * Compares this object to the specified object.
	 *
	 * @param obj the object to compare with.
	 * @return <code>true</code> if the objects are the same;
	 *         <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			if (obj instanceof Short) {
				return value == ((Short) obj).shortValue();
			}
			if (obj instanceof MutableShort) {
				return value == ((MutableShort) obj).value;
			}
		}
		return false;
	}

	// ---------------------------------------------------------------- number

	/**
	 * Returns the value as a int.
	 */
	@Override
	public int intValue() {
		return value;
	}

	/**
	 * Returns the value as a long.
	 */
	@Override
	public long longValue() {
		return value;
	}

	/**
	 * Returns the value as a float.
	 */
	@Override
	public float floatValue() {
		return value;
	}

	/**
	 * Returns the value as a double.
	 */
	@Override
	public double doubleValue() {
		return value;
	}

	// ---------------------------------------------------------------- compare

	/**
	 * Compares value of two same instances.
	 */
	public int compareTo(MutableShort other) {
		return value < other.value ? -1 : (value == other.value ? 0 : 1);
	}

	// ---------------------------------------------------------------- clone

	/**
	 * Clones object.
	 */
	@Override
	public MutableShort clone() {
		return new MutableShort(value);
	}

}