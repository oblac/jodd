// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.mutable;

/**
 * A mutable <code>double</code> wrapper.
 */
public final class MutableDouble extends Number implements Comparable<MutableDouble>, Cloneable {

	public static MutableDouble of(final double value) {
		return new MutableDouble(value);
	}

	public MutableDouble() {
	}

	public MutableDouble(final double value) {
		this.value = value;
	}

	public MutableDouble(final String value) {
		this.value = Double.parseDouble(value);
	}

	public MutableDouble(final Number number) {
		this.value = number.doubleValue();
	}

	// ---------------------------------------------------------------- value

	/**
	 * The mutable value.
	 */
	public double value;

	/**
	 * Returns mutable value.
	 */
	public double get() {
		return value;
	}

	/**
	 * Sets mutable value.
	 */
	public void set(final double value) {
		this.value = value;
	}

	/**
	 * Sets mutable value from a Number.
	 */
	public void set(final Number value) {
		this.value = value.doubleValue();
	}

	// ---------------------------------------------------------------- object

	/**
	 * Stringify the value.
	 */
	@Override
	public String toString() {
		return Double.toString(value);
	}

	/**
	 * Returns a hashcode for this value.
	 */
	@Override
	public int hashCode() {
		long bits = Double.doubleToLongBits(value);
		return (int) (bits ^ (bits >>> 32));
	}

	/**
	 * Compares this object to the specified object.
	 *
	 * @param obj the object to compare with.
	 * @return <code>true</code> if the objects are the same;
	 *         <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj != null) {
			if ( ((Double)this.value).getClass() == obj.getClass() ) {
				return Double.doubleToLongBits(value) == Double.doubleToLongBits(((Double) obj).doubleValue());
			}
			if (this.getClass() == obj.getClass()) {
				return Double.doubleToLongBits(value) == Double.doubleToLongBits(((MutableDouble) obj).value);
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
		return (int) value;
	}

	/**
	 * Returns the value as a long.
	 */
	@Override
	public long longValue() {
		return (long) value;
	}

	/**
	 * Returns the value as a float..
	 */
	@Override
	public float floatValue() {
		return (float) value;
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
	 * Checks whether the value is the special NaN value.
	 */
	public boolean isNaN() {
		return Double.isNaN(value);
	}

	/**
	 * Checks whether the double value is infinite.
	 */
	public boolean isInfinite() {
		return Double.isInfinite(value);
	}

	/**
	 * Compares value of two same instances.
	 */
	@Override
	public int compareTo(final MutableDouble other) {
		return Double.compare(value, other.value);
	}

	// ---------------------------------------------------------------- clone

	/**
	 * Clones object.
	 */
	@Override
	public MutableDouble clone() {
		return new MutableDouble(value);
	}

}