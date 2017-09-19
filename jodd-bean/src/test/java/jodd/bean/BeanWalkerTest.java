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

import jodd.bean.fixtures.FooBean;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BeanWalkerTest {

	@Test
	public void testBeanWalking() {
		final List<String> names = new ArrayList<>();

		BeanWalker beanWalker = BeanWalker.walk(new BeanWalker.BeanWalkerCallback() {
			@Override
			public void visitProperty(String name, Object value) {
				names.add(name);
			}
		});

		beanWalker.bean(new FooBean());

		Collections.sort(names);
		assertEquals("[fooBoolean, fooByte, fooCharacter, fooDouble, fooFloat, " +
			"fooInteger, fooList, fooLong, fooMap, fooString, fooStringA, " +
			"fooboolean, foobyte, foochar, foodouble, foofloat, fooint, " +
			"foolong]", names.toString());

		names.clear();
		beanWalker.excludeAll();
		beanWalker.include("fooByte", "fooBoolean");

		beanWalker.bean(new FooBean());

		Collections.sort(names);
		assertEquals("[fooBoolean, fooByte]", names.toString());
	}

	@Test
	public void testBeanWalkingMap() {
		Map<String, String> map = new HashMap<>();

		map.put("simple", "qwe");
		map.put("com.plex", "asd");

		final List<String> names = new ArrayList<>();

		BeanWalker beanWalker = BeanWalker.walk(new BeanWalker.BeanWalkerCallback() {
			@Override
			public void visitProperty(String name, Object value) {
				names.add(name);
			}
		});

		beanWalker.source(map);

		Collections.sort(names);
		assertEquals("[com.plex, simple]", names.toString());
	}
}
