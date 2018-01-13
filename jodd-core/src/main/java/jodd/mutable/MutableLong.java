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
 * A mutable <code>long</code> wrapper.
 */
public final class MutableLong extends Number implements Comparable<MutableLong>, Cloneable {

	public static MutableLong of(final long value) {
		return new MutableLong(value);
	}

	public MutableLong() {
	}

	public MutableLong(final long value) {
		this.value = value;
	}

	public MutableLong(final String value) {
		this.value = Long.parseLong(value);
	}

	public MutableLong(final Number number) {
		this.value = number.longValue();
	}

	// ---------------------------------------------------------------- value

	/**
	 * The mutable value.
	 */
	public long value;

	/**
	 * Returns mutable value.
	 */
	public long get() {
		return value;
	}

	/**
	 * Sets mutable value.
	 */
	public void set(final long value) {
		this.value = value;
	}

	/**
	 * Sets mutable value from a Number.
	 */
	public void set(final Number value) {
		this.value = value.longValue();
	}

	// ---------------------------------------------------------------- object

	/**
	 * Stringify the value.
	 */
	@Override
	public String toString() {
		return Long.toString(value);
	}

	/**
	 * Returns a hashcode for this value.
	 */
	@Override
	public int hashCode() {
		return (int) (value ^ (value >>> 32));
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
			if ( ((Long)this.value).getClass() == obj.getClass() ) {
				return value == ((Long) obj).longValue();
			}
			if (this.getClass() == obj.getClass()) {
				return value == ((MutableLong) obj).value;
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
	@Override
	public int compareTo(final MutableLong other) {
		return value < other.value ? -1 : (value == other.value ? 0 : 1);
	}

	// ---------------------------------------------------------------- clone

	/**
	 * Clones object.
	 */
	@Override
	public MutableLong clone() {
		return new MutableLong(value);
	}

}