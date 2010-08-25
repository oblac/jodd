package jodd.joy.i18n;

import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import jodd.servlet.HtmlEncoder;
import jodd.util.StringUtil;
import static jodd.joy.i18n.LocalizationUtil.findMessage;
import static jodd.joy.i18n.LocalizationUtil.findDefaultMessage;

/**
 * Renders text output. Text is key in the resource bundle. Tag supports variables.
 * If variable name starts with 'key' then variable represents another key that will
 * be lookup from the resources.
 */
public class TextTag extends SimpleTagSupport implements DynamicAttributes {

	private static final String UNKNOWN_PREFIX = "???";
	private static final String UNKNOWN_SUFFIX = "\u00BF\u00BF\u00BF";
	private static final String KEY_ATTR_NAME = "key";

	protected String key;
	public void setKey(String key) {
		this.key = key;
	}

	protected boolean defaultOnly;

	/**
	 * Sets only default resource bundles.
	 */
	public void setDefaultOnly(String defaultOnly) {
		this.defaultOnly = Boolean.parseBoolean(defaultOnly);
	}

	private List<String[]> params = new ArrayList<String[]>();

	public void setDynamicAttribute(String uri, String localName, Object value) {
		params.add(new String[] {localName, StringUtil.toSafeString(value)});
	}

	@Override
	public void doTag() throws JspException {
		PageContext pageContext = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

		key = key.trim();
		String message;
		if (StringUtil.isEmpty(key)) {
			return;
		}

		message = defaultOnly ? findDefaultMessage(request, key) : findMessage(request, key);
		if (message == null) {
			message = UNKNOWN_PREFIX + key + UNKNOWN_SUFFIX;
		} else {
			for (String[] param : params) {
				String paramName = param[0];
				String paramValue = param[1];
				String value = paramValue;
				if (paramName.startsWith(KEY_ATTR_NAME)) {
					value = defaultOnly ? findDefaultMessage(request, paramValue) : findMessage(request, paramValue);
					if (value == null) {
						value = UNKNOWN_PREFIX + paramValue + UNKNOWN_SUFFIX;
					}
				}
				message = StringUtil.replace(message, '{' + paramName + '}', value);
			}
		}

		JspWriter out = pageContext.getOut();
		try {
			out.print(HtmlEncoder.text(message));
		} catch (IOException ioex) {
			// ignore
		}
	}
}
