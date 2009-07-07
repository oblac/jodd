// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mutable;

/**
 * A mutable <code>boolean</code> wrapper.
 */
public final class MutableBoolean implements Comparable<MutableBoolean>, Cloneable {

	public MutableBoolean() {
	}

	public MutableBoolean(boolean value) {
		this.value = value;
	}

	public MutableBoolean(String value) {
		this.value = Boolean.valueOf(value).booleanValue();
	}

	public MutableBoolean(Boolean value) {
		this.value = value.booleanValue();
	}

	public MutableBoolean(Number number) {
		this.value = number.intValue() != 0;
	}


	// ---------------------------------------------------------------- value

	/**
	 * The mutable value.
	 */
	public boolean value;

	/**
	 * Returns mutable value.
	 */
	public boolean getValue() {
		return value;
	}

	/**
	 * Sets mutable value.
	 */
	public void setValue(boolean value) {
		this.value = value;
	}

	public void setValue(Boolean value) {
		this.value = value.booleanValue();
	}


	// ---------------------------------------------------------------- object

	/**
	 * Stringify the value.
	 */
	@Override
	public String toString() {
		return Boolean.toString(value);
	}

	/**
	 * Returns a hashcode for this value.
	 */
	@Override
	public int hashCode() {
		return value ? 1231 : 1237;
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
			if (obj instanceof Boolean) {
				return value == ((Boolean) obj).booleanValue();
			}
			if (obj instanceof MutableBoolean) {
				return value == ((MutableBoolean) obj).value;
			}
		}
		return false;
	}
	
	// ---------------------------------------------------------------- compare

	/**
	 * Compares value of two same instances.
	 */
	public int compareTo(MutableBoolean o) {
		return (value == o.value) ? 0 : (value == false ? -1 : 1);
	}

	// ---------------------------------------------------------------- clone

	/**
	 * Clones object.
	 */
	@Override
	public MutableBoolean clone() {
		return new MutableBoolean(value);
	}
}
