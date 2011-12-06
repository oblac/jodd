// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

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
	static String form3(String form) {
		FormTag ft = new FormTag();
		return ft.populateForm(form, foo);
	}

	public void testSimple() {
		assertEquals("", form(""));
		assertEquals("<form></form>", form("<form></form>"));
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
}
