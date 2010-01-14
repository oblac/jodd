// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.format;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Scanf.
 */
public class Scanf {

	protected static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	/**
	 * Scans input console and returns entered string.
	 */
	public static String scanf() {
		try {
			return in.readLine();
		} catch (IOException ioe) {
			return null;
		}
	}
}
