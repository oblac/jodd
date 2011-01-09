// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.speed.mutable;

import jodd.datetime.JStopWatch;
import jodd.mutable.MutableInteger;

public class MutableBenchmark {

	static JStopWatch swatch = new JStopWatch();

	public static void main(String[] args) throws Exception {
		test1Integer();
		test1Jodd();
	}

	// ---------------------------------------------------------------- test #1

	static final int loop1 = 1000000000;

	private static void test1Integer() {
		Integer integer = new Integer(173);
		swatch.start();
		for (int i = loop1; i > 0; i--) {
			integer = Integer.valueOf(integer.intValue() + 2);
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());
	}

	private static void test1Jodd() {
		MutableInteger mi = new MutableInteger(173);
		swatch.start();
		for (int i = loop1; i > 0; i--) {
			mi.value += 2;
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());
	}


}
