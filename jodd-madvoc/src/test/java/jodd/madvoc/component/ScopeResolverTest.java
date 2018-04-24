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

package jodd.madvoc.component;

import jodd.madvoc.MadvocConfig;
import jodd.madvoc.scope.MadvocScope;
import jodd.petite.PetiteContainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScopeResolverTest {

	private static class MyScope implements MadvocScope {
	}

	@Test
	void testScopeRegistration() {
		ScopeResolver scopeResolver = new ScopeResolver();
		scopeResolver.madpc = new PetiteContainer();
		scopeResolver.madpc.addBean("madvocConfig", new MadvocConfig());

		MadvocScope requestScope = scopeResolver.defaultOrScopeType(MadvocScope.REQUEST);
		MadvocScope requestScope2 = scopeResolver.defaultOrScopeType(MadvocScope.REQUEST);

		assertEquals(requestScope2, requestScope);

		scopeResolver.registerScope(MadvocScope.REQUEST, MyScope.class);

		requestScope = scopeResolver.defaultOrScopeType(MadvocScope.REQUEST);
		assertNotEquals(requestScope2, requestScope);

		assertTrue(requestScope instanceof MyScope);

		assertEquals(1, scopeResolver.allScopes.size());
	}

	@Test
	void testScopeClassLookup() {
		ScopeResolver scopeResolver = new ScopeResolver();
		scopeResolver.madpc = new PetiteContainer();
		scopeResolver.madpc.addBean("madvocConfig", new MadvocConfig());

		MadvocScope madvocScope = scopeResolver.defaultOrScopeType(MyScope.class.getName());
		assertNotNull(madvocScope);
		assertTrue(madvocScope instanceof MyScope);
		MadvocScope madvocScope2 = scopeResolver.defaultOrScopeType(MyScope.class.getName());

		assertEquals(madvocScope2, madvocScope);
	}
}
