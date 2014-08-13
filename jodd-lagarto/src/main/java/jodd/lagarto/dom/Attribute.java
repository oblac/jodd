// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.util.StringUtil;

/**
 * Elements attribute.
 */
public class Attribute implements Cloneable {

	protected final String rawName;
	protected final String name;
	protected String value;
	protected String[] splits;

	public Attribute(String rawName, String name, String value) {
		this.rawName = rawName;
		this.name = name;
		this.value = value;
	}
	
	@Override
	public Attribute clone() {
		return new Attribute(rawName, name, value);
	}

	/**
	 * Returns attributes raw name.
	 */
	public String getRawName() {
		return rawName;
	}

	/**
	 * Returns attributes name.
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

	// ---------------------------------------------------------------- splits

	/**
	 * Returns true if attribute is containing some value.
	 */
	public boolean isContaining(String include) {
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
