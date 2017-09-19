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

package jodd.introspector;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntrospectorPropertyGenericsTest {

	static class BaseAction<A, B> {
		A input;
		B output;

		public A getLina() {
			return null;
		}
	}

	static class GenAction extends BaseAction<String, Integer> {
	}

	static class Normal {
		String input;
		Integer output;
	}

	@Test
	public void testGenAction() {
		ClassDescriptor cd = ClassIntrospector.lookup(GenAction.class);

		FieldDescriptor fd = cd.getFieldDescriptor("input", true);

		assertEquals(Object.class, fd.getField().getType());
		assertEquals(String.class, fd.getRawType());

		fd = cd.getFieldDescriptor("output", true);

		assertEquals(Object.class, fd.getField().getType());
		assertEquals(Integer.class, fd.getRawType());

		PropertyDescriptor pd = cd.getPropertyDescriptor("input", true);
		assertEquals(String.class, pd.getType());

		pd = cd.getPropertyDescriptor("output", true);
		assertEquals(Integer.class, pd.getType());

		pd = cd.getPropertyDescriptor("lina", true);
		assertEquals(String.class, pd.getType());
	}

	@Test
	public void testNormal() {
		ClassDescriptor cd = ClassIntrospector.lookup(Normal.class);

		FieldDescriptor fd = cd.getFieldDescriptor("input", true);

		assertEquals(String.class, fd.getField().getType());
		assertEquals(String.class, fd.getRawType());

		fd = cd.getFieldDescriptor("output", true);

		assertEquals(Integer.class, fd.getField().getType());
		assertEquals(Integer.class, fd.getRawType());

		PropertyDescriptor pd = cd.getPropertyDescriptor("input", true);
		assertEquals(String.class, pd.getType());

		pd = cd.getPropertyDescriptor("output", true);
		assertEquals(Integer.class, pd.getType());
	}

}
