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

package jodd.decora.parser;

import static org.junit.Assert.fail;
import static org.powermock.reflect.Whitebox.invokeMethod;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.Before;
import org.junit.Test;

import jodd.decora.DecoraException;

public class DecoratorTagVisitorTest {

	private DecoratorTagVisitor decoraTagVisitor;

	@Before
	public void setUp() {
		decoraTagVisitor = new DecoratorTagVisitor();
	}

	@Test(expected = DecoraException.class)
	public final void testCheckNestedDecoraTagsDecoraTagNameNotNull() throws Exception {
		// setup
		setInternalState(decoraTagVisitor, "decoraTagName", "TEST");

		// when
		invokeMethod(decoraTagVisitor, "checkNestedDecoraTags");

		// then
		fail("A DecoraException must have occured because decoraTagName is not null.");
	}

	@Test
	public final void testCheckNestedDecoraTagsDecoraTagNameNull() throws Exception {
		// setup
		String nullString = null;
		setInternalState(decoraTagVisitor, "decoraTagName", nullString);

		// when
		invokeMethod(decoraTagVisitor, "checkNestedDecoraTags");

		// then
		// DecoraException not expected
	}

}
