// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ScopeType;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.Out;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScopeDataResolverTest {

	static class Action {
		@In String input;
	}

	@Test
	public void testInAnnotations() {
		ScopeDataResolver scopeDataResolver = new ScopeDataResolver();

		ScopeData[] scopeDatas = scopeDataResolver.inspectAllScopeData(Action.class);
		assertEquals(ScopeType.values().length, scopeDatas.length);

		ScopeData in1 = scopeDatas[ScopeType.REQUEST.value()];
		assertEquals(1, in1.in.length);

		ScopeData.In in = in1.in[0];

		assertEquals("input", in.name);
		assertEquals(String.class, in.type);
	}

	// ----------------------------------------------------------------

	static class BaseAction<A, B> {
		@In A input;
		@Out B output;
	}

	static class GenAction extends BaseAction<String, Integer> {
	}

	@Test
	public void testGenericAction() {
		ScopeDataResolver scopeDataResolver = new ScopeDataResolver();

		ScopeData[] scopeDatas = scopeDataResolver.inspectAllScopeData(GenAction.class);
		assertEquals(ScopeType.values().length, scopeDatas.length);

		ScopeData in1 = scopeDatas[ScopeType.REQUEST.value()];
		assertEquals(1, in1.in.length);

		ScopeData.In in = in1.in[0];
		ScopeData.Out out = in1.out[0];

		assertEquals("input", in.name);
		assertEquals(String.class, in.type);

		assertEquals("output", out.name);
		assertEquals(Integer.class, out.type);

	}

}