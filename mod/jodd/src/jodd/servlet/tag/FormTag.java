// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.
package jodd.servlet.tag;

import jodd.servlet.HtmlEncoder;
import jodd.servlet.HtmlTag;
import jodd.servlet.JspValueResolver;
import jodd.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Magic form tag populates a HTML form.
 */
public class FormTag extends BodyTagSupport {

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
		//final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		try {
			JspWriter out = body.getEnclosingWriter();
			String bodytext = populateForm(body.getString(), new ValueResolver() {
				public Object resolveName(String name) {
					if (source != null) {
						name = source + '.' + name;
					}
					return JspValueResolver.resolveProperty(name, pageContext);
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

	public static interface ValueResolver {
	    Object resolveName(String name);
	}

	/**
	 * Populates HTML form.
	 */
	protected String populateForm(String html, ValueResolver resolver) {
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
				if (tagName.equals("select")) {
					currentSelectName = null;
				}
				continue;
			}

			if (tagName.equals("input") == true) {
				// INPUT
				String tagType = tag.getAttribute("type");
				if (tagType == null) {
					continue;
				}
				String name = tag.getAttribute("name");
				if (name == null) {
					continue;
				}
				Object valueObject = resolver.resolveName(name);
				if (valueObject == null) {
					continue;
				}
				String value = valueObject.toString();
				tagType = tagType.toLowerCase();

				if (tagType.equals("text")) {
					tag.setAttribute("value", value);
				} else if (tagType.equals("hidden")) {
					tag.setAttribute("value", value);
				} else if (tagType.equals("image")) {
					tag.setAttribute("value", value);
				} else if (tagType.equals("password")) {
					tag.setAttribute("value", value);
				} else if (tagType.equals("checkbox")) {
					String tagValue = tag.getAttribute("value");
					if (tagValue == null) {
						tagValue = "true";
					}
					if (tagValue.equals(value)) {
						tag.setAttribute("checked");
					}
				} else if (tagType.equals("radio")) {
					String tagValue = tag.getAttribute("value");
					if (tagValue != null) {
						if (tagValue.equals(value)) {
							tag.setAttribute("checked");
						}
					}
				}
			} else if (tagName.equals("textarea")) {
				String name = tag.getAttribute("name");
				Object valueObject = resolver.resolveName(name);
				if (valueObject != null) {
					tag.setSuffixText(HtmlEncoder.text(valueObject.toString()));
				}
			} else if (tagName.equals("select")) {
				currentSelectName = tag.getAttribute("name");
			} else if (tagName.equals("option")) {
				if (currentSelectName == null) {
					continue;
				}
				String tagValue = tag.getAttribute("value");
				if (tagValue != null) {
					Object vals = resolver.resolveName(currentSelectName);
					if (vals == null) {
						continue;
					}
					if (vals.getClass().isArray()) {
						String vs[] = StringUtil.toStringArray(vals);
						for (String vsk : vs) {
							if ((vsk != null) && (vsk.equals(tagValue))) {
								tag.setAttribute("selected");
							}
						}
					} else {
						String value = StringUtil.toString(vals);
						if (value.equals(tagValue)) {
							tag.setAttribute("selected");
						}
					}
				}
			}
		}
		return result.toString();
	}

}
