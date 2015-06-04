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
 * int result = HashCodeUtil.SEED;
 * result = HashCodeUtil.hash(result, value1);
 * result = HashCodeUtil.hash(result, value2);
 * ...
 * return result;
 * </pre>
 */
public class HashCode {

	private static final int C1 = 0xcc9e2d51;
	private static final int C2 = 0x1b873593;

	/**
	 * Smear hash code.
	 */
	public static int smear(int hashCode) {
		return C2 * Integer.rotateLeft(hashCode * C1, 15);
	}

	/**
	 * An initial hash code value to which is added contributions from fields.
	 * Using a non-zero value decreases collisions of hash code values.
	 */
	public static final int SEED = 173;

	public static final int PRIME = 37;

	// ---------------------------------------------------------------- boolean

	/**
	 * Calculates hash code for booleans.
	 */
	public static int hash(int seed, boolean aBoolean) {
		return (PRIME * seed) + (aBoolean ? 1231 : 1237);
	}

	/**
	 * Calculates hash code for boolean array.
	 */
	public static int hash(int seed, boolean[] booleanArray) {
		if (booleanArray == null) {
			return 0;
		}
		for (boolean aBoolean : booleanArray) {
			seed = hash(seed, aBoolean);
		}
		return seed;
	}

	/**
	 * Calculates hash code for boolean array.
	 */
	public static int hashBooleanArray(int seed, boolean... booleanArray) {
		return hash(seed, booleanArray);
	}

	// ---------------------------------------------------------------- char

	/**
	 * Calculates hash code for chars.
	 */
	public static int hash(int seed, char aChar) {
		return (PRIME * seed) + (int) aChar;
	}

	/**
	 * Calculates hash code for char array.
	 */
	public static int hash(int seed, char[] charArray) {
		if (charArray == null) {
			return 0;
		}
		for (char aChar : charArray) {
			seed = hash(seed, aChar);
		}
		return seed;
	}

	/**
	 * Calculates hash code for char array.
	 */
	public static int hashCharArray(int seed, char... charArray) {
		return hash(seed, charArray);
	}

	// ---------------------------------------------------------------- ints

	/**
	 * Calculates hash code for ints.
	 */
	public static int hash(int seed, int anInt) {
		return (PRIME * seed) + anInt;
	}

	/**
	 * Calculates hash code for int array.
	 */
	public static int hash(int seed, int[] intArray) {
		if (intArray == null) {
			return 0;
		}
		for (int anInt : intArray) {
			seed = hash(seed, anInt);
		}
		return seed;
	}

	/**
	 * Calculates hash code for int array.
	 */
	public static int hashIntArray(int seed, int... intArray) {
	    return hash(seed, intArray);
	}


	/**
	 * Calculates hash code for short array.
	 */
	public static int hash(int seed, short[] shortArray) {
		if (shortArray == null) {
			return 0;
		}
		for (short aShort : shortArray) {
			seed = hash(seed, aShort);
		}
		return seed;
	}

	/**
	 * Calculates hash code for short array.
	 */
	public static int hashShortArray(int seed, short... shortArray) {
		return hash(seed, shortArray);
	}

	/**
	 * Calculates hash code for byte array.
	 */
	public static int hash(int seed, byte[] byteArray) {
		if (byteArray == null) {
			return 0;
		}
		for (byte aByte : byteArray) {
			seed = hash(seed, aByte);
		}
		return seed;
	}

	/**
	 * Calculates hash code for byte array.
	 */
	public static int hashByteArray(int seed, byte... byteArray) {
		return hash(seed, byteArray);
	}


	// ---------------------------------------------------------------- long

	/**
	 * Calculates hash code for longs.
	 */
	public static int hash(int seed, long aLong) {
		return (PRIME * seed) + (int) (aLong ^ (aLong >>> 32));
	}

	/**
	 * Calculates hash code for long array.
	 */
	public static int hash(int seed, long[] longArray) {
		if (longArray == null) {
			return 0;
		}
		for (long aLong : longArray) {
			seed = hash(seed, aLong);
		}
		return seed;
	}

	/**
	 * Calculates hash code for long array.
	 */
	public static int hashLongArray(int seed, long... longArray) {
		return hash(seed, longArray);
	}

	// ---------------------------------------------------------------- float

	/**
	 * Calculates hash code for floats.
	 */
	public static int hash(int seed, float aFloat) {
		return hash(seed, Float.floatToIntBits(aFloat));
	}

	/**
	 * Calculates hash code for float array.
	 */
	public static int hash(int seed, float[] floatArray) {
		if (floatArray == null) {
			return 0;
		}
		for (float aFloat : floatArray) {
			seed = hash(seed, aFloat);
		}
		return seed;
	}


	/**
	 * Calculates hash code for float array.
	 */
	public static int hashFloatArray(int seed, float... floatArray) {
		return hash(seed, floatArray);
	}

	// ---------------------------------------------------------------- double

	/**
	 * Calculates hash code for doubles.
	 */
	public static int hash(int seed, double aDouble) {
		return hash(seed, Double.doubleToLongBits(aDouble));
	}

	/**
	 * Calculates hash code for double array.
	 */
	public static int hash(int seed, double[] doubleArray) {
		if (doubleArray == null) {
			return 0;
		}
		for (double aDouble : doubleArray) {
			seed = hash(seed, aDouble);
		}
		return seed;
	}

	/**
	 * Calculates hash code for double array.
	 */
	public static int hashDoubleArray(int seed, double... doubleArray) {
	    return hash(seed, doubleArray);
	}

	// ---------------------------------------------------------------- object

	/**
	 * Calculates hash code for Objects. Object is a possibly-null object field, and possibly an array.
	 * <p>
	 * If <code>aObject</code> is an array, then each element may be a primitive
	 * or a possibly-null object.
	 */
	public static int hash(int seed, Object aObject) {
		int result = seed;
		if (aObject == null) {
			result = hash(result, 0);
		} else if (aObject.getClass().isArray() == false) {
			result = hash(result, aObject.hashCode());
		} else {
			Object[] objects = (Object[]) aObject;
			int length = objects.length;
			for (int idx = 0; idx < length; ++idx) {
				result = hash(result, objects[idx]);
			}
		}
		return result;
	}

}