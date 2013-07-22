// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.util.HtmlDecoder;
import jodd.util.HtmlEncoder;
import jodd.util.StringUtil;

import java.io.IOException;

/**
 * Elements attribute.
 */
public class Attribute implements Cloneable {

	protected final String name;
	protected String value;
	protected String[] splits;

	public Attribute(String name, String value, boolean decode) {
		this.name = name;
		this.value = value != null ? (decode ? HtmlDecoder.decode(value) : value) : null;
	}
	
	@Override
	public Attribute clone() {
		return new Attribute(name, value, false);
	}

	/**
	 * Returns attribute name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns attribute value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets attribute value.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns <code>true</code> if attributes name equals to given name.
	 */
	public boolean equalsName(String name) {
		return this.name.equals(name);
	}

	public void toHtml(Appendable appendable) throws IOException {
		appendable.append(name);
		if (value != null) {
			appendable.append('=');
			appendable.append('\"');
			appendable.append(HtmlEncoder.text(value));
			appendable.append('\"');
		}
	}

	// ---------------------------------------------------------------- splits

	/**
	 * Returns true if attribute is including some value.
	 */
	public boolean isIncluding(String include) {
		if (value == null) {
			return false;
		}
		if (splits == null) {
			splits = StringUtil.splitc(value, ' ');
		}

		for (String s: splits) {
			if (s.equals(include)) {
				return true;
			}
		}
		return false;
	}
}
