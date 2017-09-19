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

package jodd.lagarto.form;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormTagTest {

	static FormFieldResolver foo = new FormFieldResolver() {
		public Object value(String name) {
			return '*' + name + '*';
		}
	};
	static FormFieldResolver foo2 = new FormFieldResolver() {
		public Object value(String name) {
			return "*\"" + name + '*';
		}
	};

	static String form(String form) {
		String result = new FormTag().populateForm(form, foo);
		assertEquals(result, form1(form));
		return result;
	}

	static String form1(String form) {
		FormTag ft = new FormTag();
		return ft.populateForm(form, foo);
	}

	static String form2(String form) {
		return new FormTag().populateForm(form, foo2);
	}

	@Test
	public void testSimple() {
		assertEquals("", form(""));
		assertEquals("<form></form>", form("<form></form>"));
	}

	@Test
	public void testVariants() {
		assertEquals("<input type=\"text\"/>", form("<input type='text'/>"));
		assertEquals("<input type=\"text\"></input>", form("<input type='text'></input>"));
		assertEquals("<input type=\"text\"> </input>", form("<input type='text'> </input>"));
		assertEquals("<input type=\"text\"> ", form("<input type='text'> </input"));
		assertEquals("<input type=\"text\">", form("<input type='text'>"));
		assertEquals("<input type=\"\">", form("<input type=''>"));
		assertEquals("<input tYpE=\"text\">", form("<input tYpE='text'>"));
		assertEquals("<input type=\"text\"/>", form("<input  type=\"text\"  />"));
	}

	@Test
	public void testWrongType() {
		assertEquals("<input type=\"textx\" name=\"foo\"/>", form("<input type='textx' name='foo'/>"));
	}

	@Test
	public void testInputText() {
		assertEquals("<input type=\"text\" name=\"foo\" value=\"*foo*\"/>", form("<input type='text' name='foo'/>"));
		assertEquals("<input tYpE=\"text\" nAmE=\"foo\" value=\"*foo*\"/>", form("<input tYpE='text' nAmE='foo'/>"));
		assertEquals("<input type=\"text\" name=\"foo\" value=\"*foo*\"/>", form("<input type  =  'text'  name = 'foo'/>"));
		assertEquals("<input type=\"text\" name=\"&quot;foo\" value=\"*&quot;foo*\"/>", form("<input type  =  'text'  name = '\"foo'/>"));
		assertEquals("<input name=\"foo\" type=\"text\" value=\"*foo*\"/>", form("<input name='foo' type='text' />"));

	}

	@Test
	public void testInputHidden() {
		assertEquals("<input type=\"hidden\" name=\"foo\" value=\"*foo*\"/>", form("<input type='hidden' name='foo'/>"));
	}

	@Test
	public void testInputPassword() {
		assertEquals("<input type=\"password\" name=\"foo\" value=\"*foo*\"/>", form("<input type='password' name='foo'/>"));
	}

	@Test
	public void testInputImage() {
		assertEquals("<input type=\"image\" name=\"foo\" value=\"*foo*\"/>", form("<input type='image' name='foo'/>"));
	}

	@Test
	public void testCheckbox() {
		assertEquals("<input type=\"checkbox\" name=\"foo\" value=\"not\"/>", form("<input type='checkbox' name='foo' value='not'/>"));
		assertEquals("<input type=\"checkbox\" name=\"foo\" value=\"*foo*\" checked/>", form("<input type='checkbox' name='foo' value='*foo*'/>"));
	}

	@Test
	public void testRadio() {
		assertEquals("<input type=\"radio\" name=\"foo\" value=\"not\"/>", form("<input type='radio' name='foo' value='not'/>"));
		assertEquals("<input type=\"radio\" name=\"foo\" value=\"*foo*\" checked/>", form("<input type='radio' name='foo' value='*foo*'/>"));
	}

	@Test
	public void testTextarea() {
		assertEquals("<textarea name=\"foo\">*foo*</textarea>", form("<textarea name='foo'></textarea>"));
		assertEquals("<textarea name=\"foo\">*\"foo*</textarea>", form2("<textarea name='foo'></textarea>"));
	}

	@Test
	public void testSelect() {
		assertEquals("<select name=\"foo\"><option value=\"1\"/><option value=\"2\"></option><option value=\"*foo*\" selected/></select>", form("<select name='foo'><option value='1'/><option value='2'></option><option value='*foo*'/></select>"));
	}

	@Test
	public void testParamLogType() {
		assertEquals(
				"<form><input type=\"hidden\" name=\"logTime>=\" value=\"*logTime>=*\"></form>",
				form("<form><input type='hidden' name='logTime>='></form>"));
	}

	@Test
	public void testMultipleInputs() {
		String form =
			"<input type=\"text\" name=\"cc\" id=\"cc1\"/>\n" +
			"<input type=\"text\" name=\"cc\" id=\"cc2\" />\n" +
			"<input type=\"text\" name=\"cc\" id=\"cc3\" />";

		FormTag ft = new FormTag();

		String populatedForm = ft.populateForm(form, new FormFieldResolver() {
			public Object value(String name) {
				return new String[] {"a@b.c", "c@d.e", "e@f.g"};
			}
		});

		assertEquals(
				"<input type=\"text\" name=\"cc\" id=\"cc1\" value=\"a@b.c\"/>\n" +
				"<input type=\"text\" name=\"cc\" id=\"cc2\" value=\"c@d.e\"/>\n" +
				"<input type=\"text\" name=\"cc\" id=\"cc3\" value=\"e@f.g\"/>",
			populatedForm);
	}

}
