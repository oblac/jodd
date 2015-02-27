// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.jspp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsppTest {

	@Test
	public void testJspp() {
		String content = "<h1>hello</h1><pp:go /><pp:go name=\'jpp\'/>";

		Jspp jspp = new Jspp() {
			@Override
			protected String loadMacro(String macroName) {
				return "Hello ${name}";
			}
		};

		String result = jspp.process(content);

		assertEquals("<h1>hello</h1>Hello ${name}Hello jpp", result);
	}
}