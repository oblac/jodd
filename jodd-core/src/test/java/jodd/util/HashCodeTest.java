// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HashCodeTest {

	@Test
	public void testhashCode() {
		int hash = HashCode.hash(0, "Hey");
		hash = HashCode.hash(hash, 1);
		hash = HashCode.hash(hash, 1.4);
		hash = HashCode.hash(hash, 9f);
		hash = HashCode.hash(hash, true);
		hash = HashCode.hash(hash, ArraysUtil.ints(1,2,3,4));
		hash = HashCode.hash(hash, new NameValue<String, String>("A", "B"));

		int hash2 = hash;

		hash = HashCode.hash(0, "Hey");
		hash = HashCode.hash(hash, 1);
		hash = HashCode.hash(hash, 1.4);
		hash = HashCode.hash(hash, 9f);
		hash = HashCode.hash(hash, true);
		hash = HashCode.hash(hash, ArraysUtil.ints(1,2,3,4));
		hash = HashCode.hash(hash, new NameValue<String, String>("A", "B"));

		assertEquals(hash, hash2);
	}
}