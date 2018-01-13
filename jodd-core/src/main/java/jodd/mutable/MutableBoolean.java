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
 * A mutable <code>boolean</code> wrapper.
 */
public final class MutableBoolean implements Comparable<MutableBoolean>, Cloneable {

	public static MutableBoolean of(final boolean value) {
		return new MutableBoolean(value);
	}

	public MutableBoolean() {
	}

	public MutableBoolean(final boolean value) {
		this.value = value;
	}

	public MutableBoolean(final String value) {
		this.value = Boolean.valueOf(value).booleanValue();
	}

	public MutableBoolean(final Boolean value) {
		this.value = value.booleanValue();
	}

	public MutableBoolean(final Number number) {
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
	public boolean get() {
		return value;
	}

	/**
	 * Sets mutable value.
	 */
	public void set(final boolean value) {
		this.value = value;
	}

	/**
	 * Sets mutable value. Throws exception if boolean value is
	 * <code>null</code>.
	 */
	public void set(final Boolean value) {
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
	public boolean equals(final Object obj) {
		if (obj != null) {
			if ( ((Boolean)this.value).getClass() == obj.getClass() ) {
				return value == ((Boolean) obj).booleanValue();
			}
			if (this.getClass() == obj.getClass()) {
				return value == ((MutableBoolean) obj).value;
			}
		}
		return false;
	}
	
	// ---------------------------------------------------------------- compare

	/**
	 * Compares value of two same instances.
	 */
	@Override
	public int compareTo(final MutableBoolean o) {
		return (value == o.value) ? 0 : (!value ? -1 : 1);
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
