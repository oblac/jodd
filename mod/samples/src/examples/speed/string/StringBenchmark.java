// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package examples.speed.string;

import jodd.datetime.JStopWatch;
import jodd.util.StringBand;

public class StringBenchmark {

	static JStopWatch swatch = new JStopWatch();

	public static void main(String[] args) throws Exception {

		loop1 = 10000;
		for (int i = 0; i < 5; i++) {
			System.out.println("===" + loop1);
			test1String();
			test1StringBuilder();
			test1StringJodd();
			loop1 *= 10;
		}

		System.out.println("\n\n\n");

		loop2_adds = 100;
		for (int i = 0; i < 4; i++) {
			System.out.println("===" + loop2_adds);
			//test2String();
			test2StringBuilder();
			test2StringJodd();
			test2StringJodd2();
			loop2_adds *= 10;
		}
	}

	// ---------------------------------------------------------------- test #1

	static int loop1 = 10000;

	private static void test1String() {
		swatch.start();
		for (int i = loop1; i > 0; i--) {
			String s = "123456" + "78901234" +"567890";
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());
	}

	private static void test1StringBuilder() {
		swatch.start();
		for (int i = loop1; i > 0; i--) {
			String s = new StringBuilder().append("123456").append("78901234").append("567890").toString();
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());
	}

	private static void test1StringJodd() {
		swatch.start();
		for (int i = loop1; i > 0; i--) {
			String s = new StringBand().append("123456").append("78901234").append("567890").toString();
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());
	}

	// ---------------------------------------------------------------- test #2

	static int loop2 = 1000;
	static int loop2_adds = 100;

	private static void test2String() {
		System.out.println("---elapsed: endless");
	}

	private static void test2StringBuilder() {
		String toAdd = "1234567890";
		swatch.start();
		for (int l = loop2; l > 0; l--) {
			StringBuilder s = new StringBuilder();
			for (int i = loop2_adds; i > 0; i--) {
				s.append(toAdd);
			}
			s.toString();
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());
	}

	private static void test2StringJodd() {
		String toAdd = "1234567890";
		swatch.start();
		for (int l = loop2; l > 0; l--) {
			StringBand s = new StringBand();
			for (int i = loop2_adds; i > 0; i--) {
				s.append(toAdd);
			}
			s.toString();
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());
	}

	private static void test2StringJodd2() {
		String toAdd = "1234567890";
		swatch.start();
		for (int l = loop2; l > 0; l--) {
			StringBand s = new StringBand(loop2_adds);
			for (int i = loop2_adds; i > 0; i--) {
				s.append(toAdd);
			}
			s.toString();
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());
	}

}
