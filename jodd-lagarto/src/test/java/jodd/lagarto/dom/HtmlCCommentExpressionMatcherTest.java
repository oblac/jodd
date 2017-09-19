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

package jodd.lagarto.dom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HtmlCCommentExpressionMatcherTest {

	@Test
	public void testSingleExpressions() {
		HtmlCCommentExpressionMatcher m = new HtmlCCommentExpressionMatcher();

		assertFalse(m.match(5, "if IE 6"));
		assertTrue(m.match(6, "if IE 6.0"));

		assertTrue(m.match(5, "if !IE 6"));
		assertFalse(m.match(6, "if !IE 6.0"));

		assertTrue(m.match(5, "if lt IE 6"));
		assertFalse(m.match(6, "if lt IE 6.0"));
		assertFalse(m.match(7, "if lt IE 6"));

		assertTrue(m.match(5, "if lte IE 6"));
		assertTrue(m.match(6, "if lte IE 6.0"));
		assertFalse(m.match(7, "if lte IE 6.0"));

		assertFalse(m.match(5, "if gt IE 6.0"));
		assertFalse(m.match(6, "if gt IE 6"));
		assertTrue(m.match(7, "if gt IE 6"));

		assertFalse(m.match(5, "if gte IE 6"));
		assertTrue(m.match(6, "if gte IE 6"));
		assertTrue(m.match(7, "if gte IE 6.0"));

		assertFalse(m.match(5.5f, "if gte IE 6"));

		assertTrue(m.match(5.4f, "if IE 5"));
		assertTrue(m.match(5.6f, "if IE 5"));
		assertFalse(m.match(5.5f, "if IE 5.6"));

		assertFalse(m.match(9.1f, "if !IE 9"));
		assertFalse(m.match(9.1f, "if !IE 9.0"));
		assertTrue(m.match(9.1f, "if !IE 9.2"));

		assertFalse(m.match(9.1f, "if gt IE 9"));
		assertFalse(m.match(9.1f, "if gt IE 9.0"));
		assertTrue(m.match(9.1f, "if gt IE 9.01"));

		assertFalse(m.match(9.1f, "if lt IE 9"));
		assertFalse(m.match(9.1f, "if lt IE 9.0"));
		assertTrue(m.match(9.1f, "if lt IE 9.2"));

		assertTrue(m.match(9.1f, "if gte IE 9"));
		assertTrue(m.match(9.1f, "if gte IE 9.0"));
		assertFalse(m.match(9.1f, "if gte IE 9.2"));

		assertTrue(m.match(9.1f, "if lte IE 9"));
		assertTrue(m.match(9.1f, "if lte IE 9.0"));
		assertFalse(m.match(9.1f, "if lte IE 9.01"));
	}

	@Test
	public void testTwoExpressions() {
		HtmlCCommentExpressionMatcher m = new HtmlCCommentExpressionMatcher();

		assertTrue(m.match(5, "if (lt IE 6)&(lt IE 7)"));
		assertFalse(m.match(6, "if (lt IE 6)&(lt IE 7)"));
		assertFalse(m.match(7, "if (lt IE 6)&(lt IE 7)"));

		assertTrue(m.match(5, "if (lt IE 6)|(lt IE 7)"));
		assertTrue(m.match(6, "if (lt IE 6)|(lt IE 7)"));
		assertFalse(m.match(7, "if (lt IE 6)|(lt IE 7)"));
		assertTrue(m.match(7, "if (lt IE 6)|(lte IE 7)"));

		assertTrue(m.match(6.5f, "if (IE 6)|(IE 7)"));
	}
}
