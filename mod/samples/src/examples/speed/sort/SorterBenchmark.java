// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.speed.sort;

import java.util.Arrays;

import jodd.util.sort.FastMergeSort;
import jodd.util.sort.Sorter;
import jodd.util.sort.FastQuickSort;
import jodd.datetime.JStopWatch;

public class SorterBenchmark {

	static final int TOTAL_TEST_OBJECTS = 200000;

	private static Foo[] createRandomFooObjects(int total) {
		Foo[] result = new Foo[total];
		for(int i = 0; i < total; i++) {
			result[i] = new Foo();
		}
		return result;
	}

	static JStopWatch swatch = new JStopWatch();

	public static void main(String[] args) {
		System.out.println("\ntest #1");
		test1Java();
		test1JoddMerge();
		test1JoddQuick();
	}

	// ---------------------------------------------------------------- test #1

	static final int loop1 = 20;

	public static void test1Java() {
		Foo[] fooArray = createRandomFooObjects(TOTAL_TEST_OBJECTS);
		swatch.start();
		for (int i = loop1; i > 0; i--) {
			Arrays.sort(fooArray);
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());
	}

	public static void test1JoddMerge() {
		Foo[] fooArray = createRandomFooObjects(TOTAL_TEST_OBJECTS);
		Sorter sorter = new FastMergeSort();
		swatch.start();
		for (int i = loop1; i > 0; i--) {
			sorter.sort(fooArray);
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());
	}

	public static void test1JoddQuick() {
		Foo[] fooArray = createRandomFooObjects(TOTAL_TEST_OBJECTS);
		Sorter sorter = new FastQuickSort();
		swatch.start();
		for (int i = loop1; i > 0; i--) {
			sorter.sort(fooArray);
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());
	}
}