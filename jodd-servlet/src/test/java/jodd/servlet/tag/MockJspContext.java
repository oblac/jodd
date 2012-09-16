// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MockJspContext extends JspContext {
	Map<String, Object> map = new HashMap<String, Object>();

	@Override
	public void setAttribute(String s, Object o) {
		map.put(s, o);
	}

	@Override
	public void setAttribute(String s, Object o, int i) {
		map.put(i + "___" + s, o);
	}

	@Override
	public Object getAttribute(String s) {
		return map.get(s);
	}

	@Override
	public Object getAttribute(String s, int i) {
		return map.get(i + "___" + s);
	}

	@Override
	public Object findAttribute(String s) {
		return map.get(s);
	}

	@Override
	public void removeAttribute(String s) {
		map.remove(s);
	}

	@Override
	public void removeAttribute(String s, int i) {
		map.remove(i + "___" + s);
	}

	@Override
	public int getAttributesScope(String s) {
		return 0;
	}

	@Override
	public Enumeration getAttributeNamesInScope(int i) {
		return null;
	}

	@Override
	public JspWriter getOut() {
		return null;
	}

	@Override
	public ExpressionEvaluator getExpressionEvaluator() {
		return null;
	}

	@Override
	public VariableResolver getVariableResolver() {
		return null;
	}

}
