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

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HashCodeTest {

	@Test
	void testhashCode() {
		final boolean randomBoolean = RandomUtils.nextBoolean();
		final boolean[] randomBooleanArray = new boolean[] {randomBoolean, !randomBoolean};
		final String randomString = RandomStringUtils.randomAscii(10);
		final int randomInt = RandomUtils.nextInt();
		final short randomShort = (short) RandomUtils.nextInt(0, Short.MAX_VALUE);
		final byte[] randomByteArray = RandomUtils.nextBytes(5);
		final byte randomByte = randomByteArray[0];
		final long randomLong = RandomUtils.nextLong(1L, 250000L);
		final float randomFloat = RandomUtils.nextFloat(1F, 250000F);
		final double randomDouble = RandomUtils.nextDouble(1F, 250000F);
		final NameValue<String, String> randomNameValue = new NameValue<>(RandomStringUtils.randomAscii(10), RandomStringUtils.randomAscii(10));
		final Object[] randomObjectArray = new Object[] {randomBoolean, randomBooleanArray, randomString, randomInt, randomShort, randomByte, randomLong, randomFloat, randomDouble, randomNameValue, null };
		
		final int hash_1 = HashCode.create()
			// boolean
			.hash(randomBoolean)
			.hash((boolean[]) null)
			.hash(randomBooleanArray)
			// char
			.hash(randomString.charAt(0))
			.hash((char[]) null)
			.hash(new char[]{randomString.charAt(0), randomString.charAt(1)})
			// int
			.hash(randomInt)
			.hash((int[]) null)
			.hash(new int[]{randomInt})
			// short
			.hash(randomShort)
			.hash((short[]) null)
			.hash(new short[]{randomShort})
			// byte
			.hash(randomByte)
			.hash((byte[]) null)
			.hash(new byte[]{randomByte})
			// long
			.hash(randomLong)
			.hash((long[]) null)
			.hash(new long[]{randomLong})
			// float
			.hash(randomFloat)
			.hash((float[]) null)
			.hash(new float[]{randomFloat})
			// double
			.hash(randomDouble)
			.hash((double[]) null)
			.hash(new double[]{randomDouble})
			// Object
			.hash(randomString)
			.hash((Object)null)
			.hash(new Object[] {})
			.hash(randomObjectArray)
			.get();

		final int hash_2 = HashCode.create()
			// boolean
			.hash(randomBoolean)
			.hash((boolean[]) null)
			.hash(randomBooleanArray)
			// char
			.hash(randomString.charAt(0))
			.hash((char[]) null)
			.hash(new char[]{randomString.charAt(0), randomString.charAt(1)})
			// int
			.hash(randomInt)
			.hash((int[]) null)
			.hash(new int[]{randomInt})
			// short
			.hash(randomShort)
			.hash((short[]) null)
			.hash(new short[]{randomShort})
			// byte
			.hash(randomByte)
			.hash((byte[]) null)
			.hash(new byte[]{randomByte})
			// long
			.hash(randomLong)
			.hash((long[]) null)
			.hash(new long[]{randomLong})
			// float
			.hash(randomFloat)
			.hash((float[]) null)
			.hash(new float[]{randomFloat})
			// double
			.hash(randomDouble)
			.hash((double[]) null)
			.hash(new double[]{randomDouble})
			// Object
			.hash(randomString)
			.hash((Object)null)
			.hash(new Object[] {})
			.hash(randomObjectArray)
			.get();

		assertEquals(hash_1, hash_2);
	}
}
