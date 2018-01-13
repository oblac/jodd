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

package jodd.util;

/**
 * Collected methods which allow easy implementation of <code>hashCode()</code>.
 * Based on items #7 and #8 from "Effective Java" book.
 * <p>
 * Usage scenario:<br>
 * <pre>
 * return HashCode.create()
 *      .hash(value1)
 *      .hash(value2)
 *      ...
 *      .get();
 * </pre>
 */
public class HashCode {

	/**
	 * Smears hash code.
	 */
	public static int smear(final int hashCode) {
		return 0x1b873593 * Integer.rotateLeft(hashCode * 0xcc9e2d51, 15);
	}

	/**
	 * An initial hash code value to which is added contributions from fields.
	 * Using a non-zero value decreases collisions of hash code values.
	 */
	private final int prime;
	private int hashcode;

	/**
	 * Creates new HashCode calculator.
	 */
	public static HashCode create() {
		return create(173, 37);
	}

	/**
	 * Creates new HashCode calculator with custom seed and prime number.
	 */
	public static HashCode create(final int seed, final int prime) {
		return new HashCode(seed, prime);
	}

	public HashCode(final int seed, final int prime) {
		this.prime = prime;
		this.hashcode = seed;
	}

	/**
	 * Returns the calculated hashcode value.
	 */
	public int get() {
		return hashcode;
	}

	// ---------------------------------------------------------------- boolean

	/**
	 * Calculates hash code for booleans.
	 */
	public HashCode hash(final boolean aBoolean) {
		hashcode = (prime * hashcode) + (aBoolean ? 1231 : 1237);
		return this;
	}

	/**
	 * Calculates hash code for boolean array.
	 */
	public HashCode hash(final boolean[] booleanArray) {
		if (booleanArray == null) {
			hashcode = hashNull(hashcode);
			return this;
		}
		for (boolean aBoolean : booleanArray) {
			hash(aBoolean);
		}
		return this;
	}

	// ---------------------------------------------------------------- char

	/**
	 * Calculates hash code for chars.
	 */
	public HashCode hash(final char aChar) {
		hashcode = (prime * hashcode) + (int) aChar;
		return this;
	}

	/**
	 * Calculates hash code for char array.
	 */
	public HashCode hash(final char[] charArray) {
		if (charArray == null) {
			hashcode = hashNull(hashcode);
			return this;
		}
		for (char aChar : charArray) {
			hash(aChar);
		}
		return this;
	}

	// ---------------------------------------------------------------- ints

	/**
	 * Calculates hash code for ints.
	 */
	public HashCode hash(final int anInt) {
		hashcode = (prime * hashcode) + anInt;
		return this;
	}

	/**
	 * Calculates hash code for int array.
	 */
	public HashCode hash(final int[] intArray) {
		if (intArray == null) {
			hashcode = hashNull(hashcode);
			return this;
		}
		for (int anInt : intArray) {
			hash(anInt);
		}
		return this;
	}

	/**
	 * Calculates hash code for short array.
	 */
	public HashCode hash(final short[] shortArray) {
		if (shortArray == null) {
			hashcode = hashNull(hashcode);
			return this;
		}
		for (short aShort : shortArray) {
			hash(aShort);
		}
		return this;
	}

	/**
	 * Calculates hash code for byte array.
	 */
	public HashCode hash(final byte[] byteArray) {
		if (byteArray == null) {
			hashcode = hashNull(hashcode);
			return this;
		}
		for (byte aByte : byteArray) {
			hash(aByte);
		}
		return this;
	}

	// ---------------------------------------------------------------- long

	/**
	 * Calculates hash code for longs.
	 */
	public HashCode hash(final long aLong) {
		hashcode = (prime * hashcode) + (int) (aLong ^ (aLong >>> 32));
		return this;
	}

	/**
	 * Calculates hash code for long array.
	 */
	public HashCode hash(final long[] longArray) {
		if (longArray != null) {
			for (long aLong : longArray) {
				hash(aLong);
			}
		}
		return this;
	}

	// ---------------------------------------------------------------- float

	/**
	 * Calculates hash code for floats.
	 */
	public HashCode hash(final float aFloat) {
		hash(Float.floatToIntBits(aFloat));
		return this;
	}

	/**
	 * Calculates hash code for float array.
	 */
	public HashCode hash(final float[] floatArray) {
		if (floatArray == null) {
			hashcode = hashNull(hashcode);
			return this;
		}
		for (float aFloat : floatArray) {
			hash(aFloat);
		}
		return this;
	}

	// ---------------------------------------------------------------- double

	/**
	 * Calculates hash code for doubles.
	 */
	public HashCode hash(final double aDouble) {
		hash(Double.doubleToLongBits(aDouble));
		return this;
	}

	/**
	 * Calculates hash code for double array.
	 */
	public HashCode hash(final double[] doubleArray) {
		if (doubleArray == null) {
			hashcode = hashNull(hashcode);
			return this;
		}
		for (double aDouble : doubleArray) {
			hash(aDouble);
		}
		return this;
	}

	// ---------------------------------------------------------------- object

	/**
	 * Calculates hash code for Objects. Object is a possibly-null object field, and possibly an array.
	 * <p>
	 * If <code>aObject</code> is an array, then each element may be a primitive
	 * or a possibly-null object.
	 */
	public HashCode hash(final Object aObject) {
		hashcode = hashValue(hashcode, aObject);
		return this;
	}

	// ---------------------------------------------------------------- calculate

	/**
	 * Returns hash for null value.
	 */
	private int hashNull(final int seed) {
		return prime * seed;
	}

	/**
	 * Creates hash of a value.
	 */
	private int hashValue(final int seed, final Object object) {
		if (object == null) {
			return hashNull(seed);
		}

		if (object.getClass().isArray()) {
			Object[] objects = (Object[]) object;
			int result = seed;

			for (Object element : objects) {
				if (element != null) {
					result = (prime * result) + element.hashCode();
				}
				else {
					result = hashNull(result);
				}
			}

			return result;
		}

		return (prime * seed) + object.hashCode();
	}

}