// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BitsTest {

	@Test
	public void testBitsPutGet() {
		byte[] bytes = new byte[10];

		Bits.putBoolean(bytes, 0, true);
		assertTrue(Bits.getBoolean(bytes, 0));

		Bits.putChar(bytes, 0, 'A');
		assertEquals('A', Bits.getChar(bytes, 0));

		Bits.putShort(bytes, 0, (short) 73);
		assertEquals(73, Bits.getShort(bytes, 0));

		Bits.putInt(bytes, 0, 3373);
		assertEquals(3373, Bits.getInt(bytes, 0));

		Bits.putLong(bytes, 0, 3453454364564564L);
		assertEquals(3453454364564564L, Bits.getLong(bytes, 0));

		Bits.putFloat(bytes, 0, (float) 34.66);
		assertEquals(34.66, Bits.getFloat(bytes, 0), 0.001);

		Bits.putDouble(bytes, 0, 34.66);
		assertEquals(34.66, Bits.getDouble(bytes, 0), 0.001);
	}
}