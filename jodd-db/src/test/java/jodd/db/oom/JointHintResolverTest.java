// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.oom.tst.BadBoy;
import jodd.db.oom.tst.Girl;
import org.junit.Test;

import static org.junit.Assert.*;

public class JointHintResolverTest {

	@Test
	public void testHints() {
		Girl girl = new Girl();
		BadBoy badBoy = new BadBoy();
		Object[] data = new Object[]{girl, badBoy};

		JoinHintResolver jhr = new JoinHintResolver();
		Object[] result = jhr.join(data, "girl, boy");
		assertEquals(2, result.length);
		assertTrue(result[0] instanceof Girl);
		assertTrue(result[1] instanceof BadBoy);
		badBoy = (BadBoy) result[1];
		assertNull(badBoy.girl);

		jhr = new JoinHintResolver();
		result = jhr.join(data, "boy.girl, boy");
		assertEquals(1, result.length);
		assertTrue(result[0] instanceof BadBoy);
		badBoy = (BadBoy) result[0];
		assertEquals(girl, badBoy.girl);

		girl = new Girl();
		badBoy = new BadBoy();
		data = new Object[]{girl, badBoy, Integer.valueOf(7)};
		jhr = new JoinHintResolver();
		result = jhr.join(data, "boy.girl, boy, boy.girlId");
		assertEquals(1, result.length);
		assertTrue(result[0] instanceof BadBoy);
		badBoy = (BadBoy) result[0];
		assertEquals(girl, badBoy.girl);
		assertEquals(7, badBoy.girlId.intValue());

		girl = new Girl();
		badBoy = new BadBoy();
		data = new Object[]{girl, badBoy, Integer.valueOf(7)};
		jhr = new JoinHintResolver();
		result = jhr.join(data, "boy.girl, boy, girlId");
		assertEquals(2, result.length);
		assertTrue(result[0] instanceof BadBoy);
		badBoy = (BadBoy) result[0];
		assertEquals(girl, badBoy.girl);
		assertNull(badBoy.girlId);
		assertTrue(result[1] instanceof Integer);
	}
}
