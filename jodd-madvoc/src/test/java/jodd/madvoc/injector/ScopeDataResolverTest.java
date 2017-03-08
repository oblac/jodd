// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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

		ScopeData[] scopeData = scopeDataResolver.resolveScopeData(Action.class);

		ScopeData.In[] in1 = scopeData[ScopeType.REQUEST.value()].in;

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

		ScopeData[] scopeData = scopeDataResolver.resolveScopeData(GenAction.class);

		ScopeData.In[] in1 = scopeData[ScopeType.REQUEST.value()].in;
		ScopeData.Out[] out1 = scopeData[ScopeType.REQUEST.value()].out;

		ScopeData.In in = in1[0];
		ScopeData.Out out = out1[0];

		assertEquals("input", in.name);
		assertEquals(String.class, in.type);

		assertEquals("output", out.name);
		assertEquals(Integer.class, out.type);
	}

}