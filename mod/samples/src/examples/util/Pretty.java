// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.util;

import jodd.util.PrettyStringBuilder;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Pretty {

	public static void main(String[] args) {
		PrettyStringBuilder psb = new PrettyStringBuilder();
		List l = new ArrayList();
		l.add("One");
		l.add("Two");
		System.out.println(psb.toString(l));

		Map m = new HashMap();
		m.put(1, "One");
		m.put(2, "Two");
		System.out.println(psb.toString(m));
	}
}
