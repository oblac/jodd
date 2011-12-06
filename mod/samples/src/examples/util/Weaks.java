// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.util;

import static jodd.util.ref.ReferenceType.*;
import jodd.util.ref.ReferenceMap;
import jodd.util.ref.ReferenceSet;
import jodd.util.ThreadUtil;

public class Weaks {

	public static void main(String[] args) {
		valueExample();
		System.out.println("------------");
		keyExample();
		System.out.println("------------");
		setExample();
	}

	static void valueExample() {
		//ReferenceMap rm = new ReferenceMap(STRONG, STRONG);   // false
		//ReferenceMap rm = new ReferenceMap(STRONG, SOFT);   // false
		ReferenceMap rm = new ReferenceMap(STRONG, WEAK);   // true
		String value = new String("value");
		rm.put("key", value);
		System.out.println(rm.isEmpty());

		value = null;
		System.gc();
		System.gc();
		ThreadUtil.sleep(5000);
		System.out.println(rm.isEmpty());
	}

	static void keyExample() {
		ReferenceMap<String, String> rm = new ReferenceMap<String, String>(WEAK, STRONG);   // true
		//ReferenceMap rm = new ReferenceMap(STRONG, STRONG);   // false
		String key = new String("key");
		rm.put(key, "value");
		System.out.println(rm.isEmpty());

		key = null;
		System.gc();
		System.gc();
		ThreadUtil.sleep(5000);
		System.out.println(rm.isEmpty());
	}

	static void setExample() {
		ReferenceSet<String> rs = new ReferenceSet<String>(WEAK);   // true
		String value = new String("value");
		rs.add(value);
		System.out.println(rs.isEmpty());

		value = null;
		System.gc();
		System.gc();
		ThreadUtil.sleep(5000);
		System.out.println(rs.isEmpty());
	}

}
