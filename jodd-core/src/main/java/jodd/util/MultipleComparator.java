// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.util.Comparator;
import java.util.List;

/**
 * Multiple comparators compares using list of comparators.
 */
public class MultipleComparator<T> implements Comparator<T> {

	protected final List<Comparator<T>> comparators;

	public MultipleComparator(List<Comparator<T>> comparators) {
		this.comparators = comparators;
	}

	/**
	 * Compares two objects starting with first comparator; if they are equals
	 * proceeds to the next comparator and so on.
	 */
	public int compare(T o1, T o2) {
		int result = 0;
		int comparatorsSize = comparators.size();
		for (int i = 0; i < comparatorsSize; i++) {
			Comparator<T> comparator = comparators.get(i);
			result = comparator.compare(o1, o2);
			if (result != 0) {
				break;
			}
		}
		return result;
	}
}
