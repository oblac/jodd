// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.buffer;

import jodd.util.RandomStringUtil;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class FastCharBufferTest {

	@Test
	public void testAppendString() {
		FastCharBuffer fcb = new FastCharBuffer(10);

		fcb.append("12345678");
		fcb.append("ABCDEFGH");

		assertEquals("12345678ABCDEFGH", fcb.toString());
	}

	@Test
	public void testRandomAppends() {
		StringBuilder sb = new StringBuilder(10);
		FastCharBuffer fcb = new FastCharBuffer(10);

		Random rnd = new Random();

		int loop = 100;
		while (loop-- > 0) {
			String s = RandomStringUtil.randomAlphaNumeric(rnd.nextInt(20));

			sb.append(s);
			fcb.append(s);
		}

		assertEquals(sb.toString(), fcb.toString());
	}

}