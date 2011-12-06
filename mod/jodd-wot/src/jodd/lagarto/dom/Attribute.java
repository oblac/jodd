// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.servlet.HtmlDecoder;
import jodd.servlet.HtmlEncoder;

import java.io.IOException;

/**
 * Elements attribute.
 */
public class Attribute {

	protected final String name;
	protected final int nameHash;
	protected String value;

	public Attribute(String name, String value, boolean decode) {
		this.name = name.trim().toLowerCase();
		this.nameHash = name.hashCode();
		this.value = value != null ? (decode ? HtmlDecoder.decode(value) : value) : null;
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
	 * Uses name hash for better performances.
	 */
	public boolean equalsName(String name, int nameHash) {
		if (this.nameHash == nameHash) {
			if (this.name.equals(name)) {
				return true;
			}
		}
		return false;
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
}
