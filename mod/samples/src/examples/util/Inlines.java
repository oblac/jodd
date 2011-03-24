// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.util;

import jodd.util.StringPool;

public class Inlines {

	protected static final String CONST = "CONST";

	/**
	 * Example of javac inline.
	 * String is put into the constants pool and is
	 * compiled into the bytecode with:
	 * ldc "jodd"
	 * what is equivalent to:
	 * String s = "jodd";
	 */
	public void one() {
		String s = StringPool.JODD;
	}

	/**
	 * javac inline is applied here as well,
	 */
	public void two() {
		String s = CONST;
	}

	/**
	 * Will be compiled as StringBuilder.
	 */
	public void strcat() {
		String s = "foo";
		s = s + StringPool.JODD;
	}
}
