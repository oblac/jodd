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

package jodd.jerry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavascriptTest {

	private ScriptEngine scriptEngine;

	@BeforeEach
	public void setUp() throws ScriptException {
		ScriptEngineManager factory = new ScriptEngineManager();

		scriptEngine = factory.getEngineByName("javascript");

		scriptEngine.eval("$ = function(f) { return doc.$(f); }");
	}

	protected String run(String html, String query) throws ScriptException {
		Jerry doc = Jerry.jerry(html);

		scriptEngine.put("doc", doc);

		scriptEngine.eval(query);

		return doc.html();
	}

	@Test
	public void testEmbedded() throws ScriptException {

		String result = run(
			"<div id='pizza'></div>",
			"$('#pizza').text('PIZZA!')");

		assertEquals("<div id=\"pizza\">PIZZA!</div>", result);
	}

	@Test
	public void testEmbedded_each() throws ScriptException {

		String result = run(
			"<div id='pizza'><span class='p'></span><span class='p'></span><span class='p'></span></div>",
			"$('#pizza .p').each(function($this, i) { $this.text(i); })");

		assertEquals("<div id=\"pizza\"><span class=\"p\">0</span><span class=\"p\">1</span><span class=\"p\">2</span></div>", result);
	}

}
