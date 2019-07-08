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

import jodd.bean.BeanUtil;
import jodd.lagarto.LagartoParser;
import jodd.servlet.ServletUtil;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

/**
 * Magic form tag populates HTML form with values.
 */
public class FormTag extends BodyTagSupport {

	/**
	 * Starts the tag.
	 */
	@Override
	public int doStartTag() {
		return EVAL_BODY_AGAIN;
	}

	/**
	 * Performs smart form population.
	 */
	@Override
	public int doAfterBody() throws JspException {
		BodyContent body = getBodyContent();
		JspWriter out = body.getEnclosingWriter();

		String bodytext = populateForm(body.getString(), name -> value(name, pageContext));

		try {
			out.print(bodytext);
		} catch (IOException ioex) {
			throw new JspException(ioex);
		}
		return SKIP_BODY;
	}

	protected Object value(final String name, final PageContext pageContext) {
		String thisRef = BeanUtil.pojo.extractThisReference(name);
		Object value = ServletUtil.value(pageContext, thisRef);
		if (value == null) {
			return ServletUtil.value(pageContext, name);
		}

		if (thisRef.equals(name)) {
			return value;
		}

		String propertyName = name.substring(thisRef.length() + 1);

		return BeanUtil.declaredSilent.getProperty(value, propertyName);
	}


	/**
	 * Ends the tag.
	 */
	@Override
	public int doEndTag() {
		return EVAL_PAGE;
	}

	protected String populateForm(final String formHtml, final FormFieldResolver resolver) {
		LagartoParser lagartoParser = new LagartoParser(formHtml);
		StringBuilder result = new StringBuilder();

		lagartoParser.parse(new FormProcessorVisitor(result, resolver));

		return result.toString();
	}

}