// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public class PrettyStringBuilderTest extends TestCase {

	public void testList() {
		PrettyStringBuilder psb = new PrettyStringBuilder();
		List l = new ArrayList();
		l.add("One");
		l.add("Two");
		assertEquals("(One,Two)", psb.toString(l));
	}

	public void testMap() {
		PrettyStringBuilder psb = new PrettyStringBuilder();
		Map m = new LinkedHashMap();
		m.put(1, "One");
		m.put(2, "Two");
		assertEquals("{1:One,2:Two}", psb.toString(m));
	}

	public void testArray() {
		PrettyStringBuilder psb = new PrettyStringBuilder();
		int[] arr = new int[] {1,2,3};
		assertEquals("[1,2,3]", psb.toString(arr));
	}

	public void testMax() {
		PrettyStringBuilder psb = new PrettyStringBuilder();
		psb.setMaxItemsToShow(3);
		int[] arr = new int[] {1,2,3};
		assertEquals("[1,2,3]", psb.toString(arr));
		arr = new int[] {1,2,3,4};
		assertEquals("[1,2,3,...]", psb.toString(arr));
	}

	public void testDeep() {
		PrettyStringBuilder psb = new PrettyStringBuilder();
		List l = new ArrayList();
		l.add("One");
		l.add(new int[] {1,2,});
		assertEquals("(One,[1,2])", psb.toString(l));
	}
}
