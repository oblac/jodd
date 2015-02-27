// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.form;

import jodd.lagarto.LagartoParser;
import jodd.servlet.JspResolver;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
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

		String bodytext = populateForm(body.getString(), new FormFieldResolver() {
			public Object value(String name) {
				return JspResolver.value(name, pageContext);
			}
		});

		try {
			out.print(bodytext);
		} catch (IOException ioex) {
			throw new JspException(ioex);
		}
		return SKIP_BODY;
	}

	/**
	 * Ends the tag.
	 */
	@Override
	public int doEndTag() {
		return EVAL_PAGE;
	}

	protected String populateForm(String formHtml, FormFieldResolver resolver) {
		LagartoParser lagartoParser = new LagartoParser(formHtml, true);
		StringBuilder result = new StringBuilder();

		lagartoParser.parse(new FormProcessorVisitor(result, resolver));

		return result.toString();
	}

}