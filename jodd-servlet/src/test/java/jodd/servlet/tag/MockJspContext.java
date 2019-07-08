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

package jodd.servlet.tag;

import javax.el.ELContext;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MockJspContext extends JspContext {
	Map<String, Object> map = new HashMap<>();

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
	public Enumeration<String> getAttributeNamesInScope(int i) {
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

	@Override
	public ELContext getELContext() {
		return null;
	}

}
