// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.paramo.data;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Generic {

	public void one(Map<String, Long> foo, Long aLong) {
	}

	public void two(Map<String, Bar<Long>> zzz) {
	}

	public void three(Comparable<?> comparable, Iterator<? extends CharSequence> iterator, List<? super Integer> list) {
	}

}
