// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ScopeData;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ScopeDataResolver;
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

		ScopeData.In[] in1 = scopeDataResolver.lookupInData(Action.class, ScopeType.REQUEST);

		ScopeData.In in = in1[0];

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

		ScopeData.In[] in1 = scopeDataResolver.lookupInData(GenAction.class, ScopeType.REQUEST);
		ScopeData.Out[] out1 = scopeDataResolver.lookupOutData(GenAction.class, ScopeType.REQUEST);

		ScopeData.In in = in1[0];
		ScopeData.Out out = out1[0];

		assertEquals("input", in.name);
		assertEquals(String.class, in.type);

		assertEquals("output", out.name);
		assertEquals(Integer.class, out.type);
	}

}