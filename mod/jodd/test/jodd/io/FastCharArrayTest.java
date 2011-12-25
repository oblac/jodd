// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;

public class FastCharArrayTest extends TestCase {

	public void testFcat() throws IOException {
		FastCharArrayWriter fcaw = new FastCharArrayWriter();

		fcaw.write(65);
		fcaw.write(new char[] {'a', 'z', 'r'});
		fcaw.write(new char[] {'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l'}, 4, 3);

		char[] result = fcaw.toCharArray();
		char[] expected = new char[] {'A', 'a', 'z', 'r', 'g', 'h', 'j'};

		assertTrue(Arrays.equals(expected, result));
	}

	public void testFcatSingle() throws IOException {
		FastCharArrayWriter fcaw = new FastCharArrayWriter();

		fcaw.write(73);
		fcaw.write(74);
		fcaw.write(75);
		fcaw.write(76);
		fcaw.write(77);

		char[] result = fcaw.toCharArray();
		char[] expected = new char[] {73, 74, 75, 76, 77};

		assertTrue(Arrays.equals(expected, result));
	}


}
