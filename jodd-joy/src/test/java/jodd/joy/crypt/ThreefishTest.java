// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.crypt;

import jodd.util.MathUtil;
import jodd.util.RandomStringUtil;
import junit.framework.TestCase;

import java.io.UnsupportedEncodingException;

public class ThreefishTest extends TestCase {

	Threefish threefish;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		threefish = new Threefish(Threefish.BLOCK_SIZE_BITS_1024);
		threefish.init("This is a key message and I feel good", 0x1122334455667788L, 0xFF00FF00AABB9933L);

	}

	private void assertEqualsArray(byte[] expected, byte[] value) {
		assertEquals(expected.length, value.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], value[i]);
		}
	}

	public void testSimple() throws UnsupportedEncodingException {
		String message = "Threefish!";
		byte[] encrypted = threefish.encryptString(message);
		String message2 = threefish.decryptString(encrypted);
		assertEquals(message, message2);

		message = "Jodd was here!Jodd was here!Jodd was here!Jodd was here!Jodd was here!Jodd was here!Jodd was here!Jodd was here!Jodd was here!Jodd was here!Jodd was here!";
		encrypted = threefish.encryptString(message);
		message2 = threefish.decryptString(encrypted);

		assertEquals(message, message2);
	}

	public void testLoop() throws UnsupportedEncodingException {
		
		long reps = 10000;
		while (reps-- > 0) {
			String s = RandomStringUtil.randomAscii(MathUtil.randomInt(1, 1024));
			byte[] encrypted = threefish.encryptString(s);
			String s2 = threefish.decryptString(encrypted);
			assertEquals(s, s2);
		}
		
	}
}
