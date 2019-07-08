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
 * A mutable <code>float</code> wrapper.
 */
public final class MutableFloat extends Number implements Comparable<MutableFloat>, Cloneable {

	public static MutableFloat of(final float value) {
		return new MutableFloat(value);
	}

	public MutableFloat() {
	}

	public MutableFloat(final float value) {
		this.value = value;
	}

	public MutableFloat(final String value) {
		this.value = Float.parseFloat(value);
	}

	public MutableFloat(final Number number) {
		this.value = number.floatValue();
	}

	// ---------------------------------------------------------------- value

	/**
	 * The mutable value.
	 */
	public float value;

	/**
	 * Returns mutable value.
	 */
	public float get() {
		return value;
	}

	/**
	 * Sets mutable value.
	 */
	public void set(final float value) {
		this.value = value;
	}

	/**
	 * Sets mutable value from a Number.
	 */
	public void set(final Number value) {
		this.value = value.floatValue();
	}

	// ---------------------------------------------------------------- object

	/**
	 * Stringify the value.
	 */
	@Override
	public String toString() {
		return Float.toString(value);
	}

	/**
	 * Returns a hashcode for this value.
	 */
	@Override
	public int hashCode() {
		return  Float.floatToIntBits(value);
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
			if ( ((Float)this.value).getClass() == obj.getClass() ) {
				return Float.floatToIntBits(value) == Float.floatToIntBits(((Float) obj).floatValue());
			}
			if (this.getClass() == obj.getClass()) {
				return Float.floatToIntBits(value) == Float.floatToIntBits(((MutableFloat) obj).value);
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
	 * Checks whether the value is the special NaN value.
	 */
	public boolean isNaN() {
		return Float.isNaN(value);
	}

	/**
	 * Checks whether the float value is infinite.
	 */
	public boolean isInfinite() {
		return Float.isInfinite(value);
	}

	/**
	 * Compares value of two same instances.
	 */
	@Override
	public int compareTo(final MutableFloat other) {
		return Float.compare(value, other.value);
	}

	// ---------------------------------------------------------------- clone

	/**
	 * Clones object.
	 */
	@Override
	public MutableFloat clone() {
		return new MutableFloat(value);
	}

}