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

package jodd.bean;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SupplierTest {

	private static class Foo {
		public int bbb = 13;
		public Supplier<Foo> aaa = () -> this;
	}

	@Test
	void testSupplier_inMap() {
		Map map1 = new HashMap();
		Supplier<Map> mapSupplier = () -> map1;

		map1.put("qwe", "123");
		map1.put("asd", mapSupplier);

		assertEquals("123", BeanUtil.pojo.getProperty(map1, "qwe"));
		assertEquals("123", BeanUtil.pojo.getProperty(map1, "asd.qwe"));
	}

	@Test
	void testSupplier_last() {
		Map map1 = new HashMap();
		Supplier<Map> mapSupplier = () -> map1;

		map1.put("qwe", "123");
		map1.put("asd", mapSupplier);

		assertEquals("123", BeanUtil.pojo.getProperty(map1, "qwe"));
		assertEquals(mapSupplier, BeanUtil.pojo.getProperty(map1, "asd"));
	}


	@Test
	void testSupplier_inBean() {
		Foo foo = new Foo();

		assertEquals(Integer.valueOf(13), BeanUtil.pojo.getProperty(foo, "bbb"));
		assertEquals(Integer.valueOf(13), BeanUtil.pojo.getProperty(foo, "aaa.bbb"));
	}
}
