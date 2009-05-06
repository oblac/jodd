// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.sort;

import java.util.Comparator;

//import jodd.util.sort.FastQuickSort;
import jodd.util.sort.FastMergeSort;
import jodd.util.sort.Sorter;
import jodd.util.sort.DefaultSort;
import jodd.util.sort.FastQuickSort;
//import jodd.util.sort.DefaultSort;

public class fastsort {

	public static final int TOTAL_TEST_OBJECTS = 100000;

	private static Foo[] createRandomFooObjects(int total) {
		Foo[] result = new Foo[total];
		for(int i = 0; i < total; i++) {
			result[i] = new Foo();
		}
		return result;
	}
	
	static class Acomparator implements Comparator {
		public int compare(Object arg0, Object arg1) {
			return ((Foo) arg0).getName().compareTo(((Foo) arg1).getName());
		}
	}
	public static void main(String[] arg) throws Exception {
		System.out.println("create array of random objects " + TOTAL_TEST_OBJECTS);
		Foo[] fooArray = createRandomFooObjects(TOTAL_TEST_OBJECTS);
		for (int i = 0; i < 10; i++) {
			System.out.println("> " + fooArray[i]);
		}

		/* CHOOSE SORTING STRATEGY */
//		Sorter sorter = new FastQuickSort();      //266      250ms
		Sorter sorter = new FastMergeSort();	  //141      125ms
		//Sorter sorter = new DefaultSort();	  //219ms    188ms
		
		fastsort.Acomparator acomparator = new fastsort.Acomparator();
		
		System.out.println("\n\nsorting...");

		long t1 = System.currentTimeMillis();
//		sorter.sort(fooArray, acomparator);
//		sorter.sort(fooArray);
		FastMergeSort.doSort(fooArray);
		long t2 = System.currentTimeMillis();

		System.out.println("\n\nend of sort.");

		for (int i = 0; i < 10; i++) {
			System.out.println("> " + fooArray[i]);
		}
		System.out.println("\n\ntotal sort time: " + (t2 - t1) + "ms");
	}
}