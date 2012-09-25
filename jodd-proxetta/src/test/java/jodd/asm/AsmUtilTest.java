// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AsmUtilTest {

	@Test
	public void testTyperef2Name() {
		assertEquals("java.lang.String", AsmUtil.typeref2Name("Ljava/lang/String;"));
	}
}
