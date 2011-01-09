// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.sort;

import java.util.Comparator;

public interface Sorter {
	
	void sort(Object a[], Comparator comparator);
	
	void sort(Comparable a[]);

}
