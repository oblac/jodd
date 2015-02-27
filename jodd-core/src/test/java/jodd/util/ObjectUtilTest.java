// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.mutable.MutableInteger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObjectUtilTest {

	@Test
	public void testCloneViaSerialization() throws Exception {

		MutableInteger mu = new MutableInteger(183);

		MutableInteger mu2 = ObjectUtil.cloneViaSerialization(mu);

		assertFalse(mu == mu2);
		assertTrue(mu.equals(mu2));
		assertEquals(mu.intValue(), mu2.intValue());
	}

}
