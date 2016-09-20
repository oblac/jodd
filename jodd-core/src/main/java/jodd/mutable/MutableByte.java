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
 * A mutable <code>byte</code> wrapper.
 */
public final class MutableByte extends Number implements Comparable<MutableByte>, Cloneable {

	public MutableByte() {
	}

	public MutableByte(byte value) {
		this.value = value;
	}

	public MutableByte(String value) {
		this.value = Byte.parseByte(value);
	}

	public MutableByte(Number number) {
		this.value = number.byteValue();
	}

	// ---------------------------------------------------------------- value

	/**
	 * The mutable value.
	 */
	public byte value;

	/**
	 * Returns mutable value.
	 */
	public byte get() {
		return value;
	}

	/**
	 * Sets mutable value.
	 */
	public void set(byte value) {
		this.value = value;
	}

	/**
	 * Sets mutable value from a Number.
	 */
	public void set(Number value) {
		this.value = value.byteValue();
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
			if ( ((Byte)this.value).getClass() == obj.getClass() ) {
				return value == ((Byte) obj).byteValue();
			}
			if (this.getClass() == obj.getClass()) {
				return value == ((MutableByte) obj).value;
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
	public int compareTo(MutableByte other) {
		return value < other.value ? -1 : (value == other.value ? 0 : 1);
	}

	// ---------------------------------------------------------------- clone

	/**
	 * Clones object.
	 */
	@Override
	public MutableByte clone() {
		return new MutableByte(value);
	}

}