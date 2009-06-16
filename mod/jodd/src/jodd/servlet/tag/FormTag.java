// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.
package jodd.servlet.tag;

import jodd.servlet.HtmlEncoder;
import jodd.servlet.HtmlTag;
import jodd.servlet.JspValueResolver;
import jodd.util.StringUtil;
import jodd.util.Closure;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Magic form tag populates a HTML form.
 */
public class FormTag extends BodyTagSupport {

	private static final String INPUT = "input";
	private static final String TYPE = "type";
	private static final String VALUE = "value";
	private static final String TEXT = "text";
	private static final String SELECT = "select";
	private static final String HIDDEN = "hidden";
	private static final String IMAGE = "image";
	private static final String PASSWORD = "password";
	private static final String CHECKBOX = "checkbox";
	private static final String TRUE = "true";
	private static final String CHECKED = "checked";
	private static final String RADIO = "radio";
	private static final String TEXTAREA = "textarea";
	private static final String NAME = "name";
	private static final String OPTION = "option";
	private static final String SELECTED = "selected";

	// ---------------------------------------------------------------- tag

	protected String source;

	public void setSource(String source) {
		this.source = source;
	}

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
	public int doAfterBody() {
		BodyContent body = getBodyContent();
		try {
			JspWriter out = body.getEnclosingWriter();
			String bodytext = populateForm(body.getString(), new Closure<String, Object>() {
				public Object execute(String input) {
					if (source != null) {
						input = source + '.' + input;
					}
					return JspValueResolver.resolveProperty(input, pageContext);
				}
			});
			out.print(bodytext);
		} catch (Exception ex) {
			ex.printStackTrace();
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

	// ---------------------------------------------------------------- populate

	/**
	 * Populates HTML form.
	 */
	protected String populateForm(String html, Closure<String, Object> resolver) {
		int s = 0;
		StringBuilder result = new StringBuilder((int) (html.length() * 1.2));
		String currentSelectName = null;
		HtmlTag tag = null;
		while (true) {
			if (tag != null) {
				result.append(tag);
			}
			tag = HtmlTag.locateNextTag(html, s);
			if (tag == null) {
				result.append(html.substring(s));
				break;
			}
			result.append(html.substring(s, tag.getStartIndex()));
			s = tag.getNextIndex();

			String tagName = tag.getTagName();
			if (tag.isEndTag()) {
				if (tagName.equals(SELECT)) {
					currentSelectName = null;
				}
				continue;
			}

			if (tagName.equals(INPUT) == true) {
				// INPUT
				String tagType = tag.getAttribute(TYPE);
				if (tagType == null) {
					continue;
				}
				String name = tag.getAttribute(NAME);
				if (name == null) {
					continue;
				}
				Object valueObject = resolver.execute(name);
				if (valueObject == null) {
					continue;
				}
				String value = valueObject.toString();
				tagType = tagType.toLowerCase();

				if (tagType.equals(TEXT)) {
					tag.setAttribute(VALUE, value);
				} else if (tagType.equals(HIDDEN)) {
					tag.setAttribute(VALUE, value);
				} else if (tagType.equals(IMAGE)) {
					tag.setAttribute(VALUE, value);
				} else if (tagType.equals(PASSWORD)) {
					tag.setAttribute(VALUE, value);
				} else if (tagType.equals(CHECKBOX)) {
					String tagValue = tag.getAttribute(VALUE);
					if (tagValue == null) {
						tagValue = TRUE;
					}
					if (tagValue.equals(value)) {
						tag.setAttribute(CHECKED);
					}
				} else if (tagType.equals(RADIO)) {
					String tagValue = tag.getAttribute(VALUE);
					if (tagValue != null) {
						if (tagValue.equals(value)) {
							tag.setAttribute(CHECKED);
						}
					}
				}
			} else if (tagName.equals(TEXTAREA)) {
				String name = tag.getAttribute(NAME);
				Object valueObject = resolver.execute(name);
				if (valueObject != null) {
					tag.setSuffixText(HtmlEncoder.text(valueObject.toString()));
				}
			} else if (tagName.equals(SELECT)) {
				currentSelectName = tag.getAttribute(NAME);
			} else if (tagName.equals(OPTION)) {
				if (currentSelectName == null) {
					continue;
				}
				String tagValue = tag.getAttribute(VALUE);
				if (tagValue != null) {
					Object vals = resolver.execute(currentSelectName);
					if (vals == null) {
						continue;
					}
					if (vals.getClass().isArray()) {
						String vs[] = StringUtil.toStringArray(vals);
						for (String vsk : vs) {
							if ((vsk != null) && (vsk.equals(tagValue))) {
								tag.setAttribute(SELECTED);
							}
						}
					} else {
						String value = StringUtil.toString(vals);
						if (value.equals(tagValue)) {
							tag.setAttribute(SELECTED);
						}
					}
				}
			}
		}
		return result.toString();
	}

}
