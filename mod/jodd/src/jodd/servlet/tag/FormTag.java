// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.
package jodd.servlet.tag;

import jodd.servlet.HtmlEncoder;
import jodd.servlet.HtmlFormUtil;
import jodd.servlet.HtmlTag;
import jodd.servlet.JspValueResolver;
import jodd.util.StringUtil;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

/**
 * Magic form tag populates a HTML form.
 */
public class FormTag extends BodyTagSupport {

	private static final String FORM = "form";
	private static final String ID = "id";
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

	// ---------------------------------------------------------------- options

	private boolean addIds;

	/**
	 * Specify if field IDs should be added based on form id.
	 */
	public void setAddIds(boolean addIds) {
		this.addIds = addIds;
	}

	// ---------------------------------------------------------------- interface

	/**
	 * Resolve form fields.
	 */
	public interface FieldResolver {
		/**
		 * Resolves form field value.
		 */
		Object value(String name);
	}

	// ---------------------------------------------------------------- tag

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
		String bodytext = populateForm(body.getString(), addIds, new FieldResolver() {
			public Object value(String name) {
				return JspValueResolver.resolveProperty(name, pageContext);
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

	// ---------------------------------------------------------------- populate

	/**
	 * Builds tag id if it is missing.
	 */
	private void makeId(HtmlTag tag, String formId, String name) {
		if (formId == null) {
			return;
		}
		if (name == null) {
			return;
		}
		String id = tag.getAttribute(ID);
		if (id != null) {
			return;
		}
		tag.setAttribute(ID, formId + HtmlFormUtil.name2id(name));
	}


	/**
	 * Populates HTML form.
	 */
	protected String populateForm(String html, boolean addIds, FieldResolver resolver) {
		int s = 0;
		StringBuilder result = new StringBuilder((int) (html.length() * 1.2));
		String currentSelectName = null;
		HtmlTag tag = null;
		String formId = null;
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
				} else if (tagName.equals(FORM)) {
					formId = null;
				}
				continue;
			}

			// find form id
			if (addIds == true) {
				if (tagName.equals(FORM) && formId == null) {
					formId = tag.getAttribute(ID);
					if (formId != null) {
						 formId += '_';
					}
					continue;
				}
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
				makeId(tag, formId, name);
				Object valueObject = resolver.value(name);
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
				Object valueObject = resolver.value(name);
				if (valueObject != null) {
					tag.setSuffixText(HtmlEncoder.text(valueObject.toString()));
					makeId(tag, formId, name);
				}
			} else if (tagName.equals(SELECT)) {
				currentSelectName = tag.getAttribute(NAME);
				makeId(tag, formId, currentSelectName);
			} else if (tagName.equals(OPTION)) {
				if (currentSelectName == null) {
					continue;
				}
				String tagValue = tag.getAttribute(VALUE);
				if (tagValue != null) {
					Object vals = resolver.value(currentSelectName);
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
