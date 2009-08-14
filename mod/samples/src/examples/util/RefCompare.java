// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.util;

import jodd.datetime.JStopWatch;
import static jodd.util.ref.ReferenceType.SOFT;
import static jodd.util.ref.ReferenceType.STRONG;

import java.util.Map;
import java.util.Random;

public class RefCompare {
	private static final int TOTAL_ELEMENTS = 100000;
	private static final int TOTAL_GETS = TOTAL_ELEMENTS * 1000;

	public static void main(String[] args) {
		Random r = new Random();

//		Map m = new jodd.util.collection.ReferenceMap();
		Map m = new jodd.util.ref.ReferenceMap(STRONG, SOFT);
//		Map m = new ConcurrentHashMap();

		JStopWatch sw = new JStopWatch();
		for (int i = 0; i < TOTAL_ELEMENTS; i++) {
			m.put(Integer.valueOf(r.nextInt()), Double.valueOf(r.nextDouble()));
		}
		for (int i = 0; i < TOTAL_GETS; i++) {
			m.get(Integer.valueOf(r.nextInt()));
		}
		sw.stop();
		System.out.println(sw);
		System.out.println(m.entrySet().size());

	}
}
