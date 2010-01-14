// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import junit.framework.TestCase;

public class FormTagTest extends TestCase {

	static FormTag.FieldResolver foo = new FormTag.FieldResolver() {
		public Object value(String name) {
			return '*' + name+ '*';
		}
	};
	static FormTag.FieldResolver foo2 = new FormTag.FieldResolver() {
		public Object value(String name) {
			return "*\"" + name + '*';
		}
	};

	static String form(String form) {
		String result = new FormTag().populateForm(form, false, foo);
		assertEquals(result, form1(form));
		return result;
	}
	static String form1(String form) {
		return new FormTag().populateForm(form, true, foo);
	}
	static String form2(String form) {
		return new FormTag().populateForm(form, false, foo2);
	}

	public void testSimple() {
		assertEquals("", form(""));
		assertEquals("<form></form>", form("<form></form>"));
		assertEquals("<form name=\"foo\" id=\"foo\"></form>", form1("<form name=\"foo\"></form>"));
		assertEquals("<form name=\"fo.o\" id=\"fo_o\"></form>", form1("<form name=\"fo.o\"></form>"));
	}
	public void testVariants() {
		assertEquals("<input type='text'/>", form("<input type='text'/>"));
		assertEquals("<input type='text'></input>", form("<input type='text'></input>"));
		assertEquals("<input type='text'> </input>", form("<input type='text'> </input>"));
		assertEquals("<input type='text'> </input", form("<input type='text'> </input"));
		assertEquals("<input type='text'>", form("<input type='text'>"));
		assertEquals("<input type=''>", form("<input type=''>"));
		assertEquals("<input tYpE='text'>", form("<input tYpE='text'>"));
		assertEquals("<input  type=\"text\"  />", form("<input  type=\"text\"  />"));
	}
	public void testWrongType() {
		assertEquals("<input type='textx' name='foo'/>", form("<input type='textx' name='foo'/>"));
	}

	public void testInputText() {
		assertEquals("<input type=\"text\" name=\"foo\" value=\"*foo*\"/>", form("<input type='text' name='foo'/>"));
		assertEquals("<input type=\"text\" name=\"foo\" value=\"*foo*\"/>", form("<input tYpE='text' nAmE='foo'/>"));
		assertEquals("<input type=\"text\" name=\"foo\" value=\"*foo*\"/>", form("<input type  =  'text'  name = 'foo'/>"));
		assertEquals("<input type=\"text\" name=\"\"foo\" value=\"*&quot;foo*\"/>", form("<input type  =  'text'  name = '\"foo'/>"));
		assertEquals("<input name=\"foo\" type=\"text\" value=\"*foo*\"/>", form("<input name='foo' type='text' />"));

	}

	public void testInputHidden() {
		assertEquals("<input type=\"hidden\" name=\"foo\" value=\"*foo*\"/>", form("<input type='hidden' name='foo'/>"));
	}

	public void testInputPassword() {
		assertEquals("<input type=\"password\" name=\"foo\" value=\"*foo*\"/>", form("<input type='password' name='foo'/>"));
	}

	public void testInputImage() {
		assertEquals("<input type=\"image\" name=\"foo\" value=\"*foo*\"/>", form("<input type='image' name='foo'/>"));
	}

	public void testCheckbox() {
		assertEquals("<input type='checkbox' name='foo' value='not'/>", form("<input type='checkbox' name='foo' value='not'/>"));
		assertEquals("<input type=\"checkbox\" name=\"foo\" value=\"*foo*\" checked/>", form("<input type='checkbox' name='foo' value='*foo*'/>"));
	}
	public void testRadio() {
		assertEquals("<input type='radio' name='foo' value='not'/>", form("<input type='radio' name='foo' value='not'/>"));
		assertEquals("<input type=\"radio\" name=\"foo\" value=\"*foo*\" checked/>", form("<input type='radio' name='foo' value='*foo*'/>"));
	}

	public void testTextarea() {
		assertEquals("<textarea name='foo'>*foo*</textarea>", form("<textarea name='foo'></textarea>"));
		assertEquals("<textarea name='foo'>*&quot;foo*</textarea>", form2("<textarea name='foo'></textarea>"));
	}

	public void testSelect() {
		assertEquals("<select name='foo'><option value='1'/><option value='2'></option><option value=\"*foo*\" selected/></select>", form("<select name='foo'><option value='1'/><option value='2'></option><option value='*foo*'/></select>"));
	}

	public void testWithForm() {
		String form = "<form id=\"foo\"><input name=\"in\" type=\"text\"><textarea name=\"te\"><select name=\"se\"></form>";
		String mrof = form1(form);
		assertEquals("<form id=\"foo\"><input name=\"in\" type=\"text\" id=\"foo_in\" value=\"*in*\"><textarea name=\"te\" id=\"foo_te\">*te*<select name=\"se\" id=\"foo_se\"></form>", mrof);
	}
	public void testWithForm2() {
		String form = "<form id=\"fo_o\"><input name=\"i.n\" type=\"text\"><textarea name=\"te\"><select name=\"se\"></form>";
		String mrof = form1(form);
		assertEquals("<form id=\"fo_o\"><input name=\"i.n\" type=\"text\" id=\"fo_o_i_n\" value=\"*i.n*\"><textarea name=\"te\" id=\"fo_o_te\">*te*<select name=\"se\" id=\"fo_o_se\"></form>", mrof);
	}
}
