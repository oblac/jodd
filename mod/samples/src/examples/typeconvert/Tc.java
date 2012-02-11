// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.typeconvert;

import jodd.typeconverter.Convert;

public class Tc {

	public static void main(String[] args) {

		String[] sa = new String[] {"1", "2"};
		Integer[] i1 = new Integer[] {new Integer(1), new Integer(2)};
		int[] i2 = new int[] {1,2};

		System.out.println(int[].class);
		System.out.println(i2.getClass() == int[].class);

		System.out.println(i1 instanceof Object[]);
		System.out.println(i2 instanceof int[]);

		System.out.println(Convert.toByteArray(sa));
		System.out.println(Convert.toByteArray(i1));
		System.out.println(Convert.toByteArray(i2));

	}
}
