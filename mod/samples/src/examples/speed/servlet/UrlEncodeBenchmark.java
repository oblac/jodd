// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.speed.servlet;

import jodd.servlet.UrlEncoder;
import jodd.datetime.JStopWatch;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlEncodeBenchmark {

	static JStopWatch swatch = new JStopWatch();

	public static void main(String[] args) throws Exception {
		System.out.println("\ntest #1");
		test1Java();
		test1Jodd();
	}

	// ---------------------------------------------------------------- test #1

	static final int loop1 = 1000000;

	private static void test1Java() throws UnsupportedEncodingException {
		swatch.start();
		for (int i = 1; i < loop1; i++) {
			URLEncoder.encode("/qwe?name=  dada", "UTF-8");
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());			// 4922
	}

	private static void test1Jodd() {
		swatch.start();
		for (int i = 1; i < loop1; i++) {
			UrlEncoder.url("/qwe?name=  dada");
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());			// 2344
	}
}
