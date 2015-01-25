// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mutable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ValueHolderTest {

	@Test
	public void testValueHolder() {
		ValueHolder<String> str = ValueHolderWrapper.create();
		str.setValue("123");

		assertEquals("123", str.getValue());

		str = ValueHolderWrapper.wrap("123");
		str.setValue("123");

		assertEquals("123", str.getValue());
	}
}