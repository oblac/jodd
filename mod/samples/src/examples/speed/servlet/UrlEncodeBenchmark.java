// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.speed.servlet;

import jodd.servlet.URLCoder;
import jodd.datetime.JStopWatch;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class UrlEncodeBenchmark {

	static JStopWatch swatch = new JStopWatch();

	public static void main(String[] args) throws Exception {
		System.out.println("\ntest #1");
		test1Java();
		test1Jodd();
		test2Java();
		test2Jodd();
		test21Jodd();
	}

	// ---------------------------------------------------------------- test #1

	static final int loop1 = 1000000;

	private static void test1Java() throws UnsupportedEncodingException {
		swatch.start();
		for (int i = 1; i < loop1; i++) {
			URLEncoder.encode("/qwe?name=  dada", "UTF-8");
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());			// 3388
	}

	private static void test1Jodd() {
		swatch.start();
		for (int i = 1; i < loop1; i++) {
			URLCoder.url("/qwe?name=  dada");
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());			// 1570
	}

	// ---------------------------------------------------------------- test #2

	static final int loop2 = 1000000;

	private static void test2Java() throws UnsupportedEncodingException, URISyntaxException {
		swatch.start();
		for (int i = 1; i < loop2; i++) {
			String s = new URI("http", null, "jodd.org", 80, "/f o+o.html", null, null).toString() + "?name=" + URLEncoder.encode("v+al ue", "UTF-8");
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());			// 7482
	}

	private static void test2Jodd() {
		swatch.start();
		for (int i = 1; i < loop2; i++) {
			String s = URLCoder.url("http://jodd.org:80/f o+o.html?name=v+al ue");
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());			// 2720
	}

	private static void test21Jodd() {
		swatch.start();
		for (int i = 1; i < loop2; i++) {
			String s = URLCoder.build("http://jodd.org:80/f o+o.html").param("name", "v+al ue").toString();
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());			// 2340
	}
}
