// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import jodd.Jodd;
import junit.framework.TestCase;

import static jodd.asm.AsmUtil.loadClass;

public class AsmUtilTest extends TestCase {

	public void testTypedesc2ClassName() throws ClassNotFoundException {
		assertEquals(int.class, loadClass("I"));
		assertEquals(int[].class, loadClass("[I"));
		assertEquals(long[][].class, loadClass("[[J"));

		assertEquals(Long[][].class, loadClass("[[Ljava/lang/Long;"));

		assertEquals(AsmUtil.class, loadClass("Ljodd/asm/AsmUtil;"));
		assertEquals(AsmUtil[].class, loadClass("[Ljodd/asm/AsmUtil;"));

		assertEquals(Jodd.class, loadClass("Ljodd/Jodd;"));
	}

	public void testTyperef2Name() {
		assertEquals("java.lang.String", AsmUtil.typeref2Name("Ljava/lang/String;"));
	}
}
