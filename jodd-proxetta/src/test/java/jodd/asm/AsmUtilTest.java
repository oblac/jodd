// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import junit.framework.TestCase;

public class AsmUtilTest extends TestCase {

	public void testTyperef2Name() {
		assertEquals("java.lang.String", AsmUtil.typeref2Name("Ljava/lang/String;"));
	}
}
