// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.util;

import jodd.datetime.JStopWatch;
import jodd.util.Wildcard;

public class WildcardSpeed {

	public static void main(String[] args) {

		JStopWatch swatch = new JStopWatch();
		for (int i = 0; i < 100000000; i++) {
			Wildcard.match("1234567", "*");       // 1.812
			//Wildcard.equalsOrMatch("1234567", "1234567");       // 4.985 - 18.7666
			//"1234567".equals("1234567");        // 01.375
		}
		swatch.stop();
		System.out.println(swatch);
	}
}
