// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.io.Serializable;

public class MultiComparator implements Comparator, Serializable {

	private ArrayList comparators = new ArrayList();
	private Comparator[] comparatorsArray;
	
	public MultiComparator(Comparator c) {
		add(c);
	}
	
	public void add(Comparator c) {
		comparators.add(c);
		makeArray();
	}
	
	private void makeArray() {
		comparatorsArray = new Comparator[comparators.size()];
		for (int i = 0; i < comparators.size(); i++) {
			comparatorsArray[i] = (Comparator) comparators.get(i);
		}
	}

	public int compare(Object arg0, Object arg1) {
		for (Comparator c : comparatorsArray) {
			int result = c.compare(arg0, arg1);
			if (result != 0) {
				return result;
			}
		}
		return 0;
	}
}
