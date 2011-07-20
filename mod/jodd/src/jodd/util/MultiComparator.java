// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.io.Serializable;

/**
 * Compare objects using several comparators at once.
 */
public class MultiComparator implements Comparator, Serializable {

	private ArrayList<Comparator> comparators = new ArrayList<Comparator>();

	public MultiComparator(Comparator... comparators) {
		for (Comparator c : comparators) {
			this.comparators.add(c);
		}
	}
	
	public void add(Comparator c) {
		comparators.add(c);
	}
	
	public int compare(Object arg0, Object arg1) {
		for (Comparator c : comparators) {
			int result = c.compare(arg0, arg1);
			if (result != 0) {
				return result;
			}
		}
		return 0;
	}
}
