// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import jodd.bean.BeanTemplate;
import jodd.bean.BeanTemplateResolver;
import jodd.util.StringPool;

/**
 * Holds original props value and generated one.
 */
public class PropsValue {

	PropsValue(String value) {
		this.value = value;
	}

	/**
	 * Original value.
	 */
	protected String value;

	/**
	 * Value with all macros resolved. May be <code>null</code> when
	 * value doesn't contain anything to resolve.
	 */
	protected String resolved;

	/**
	 * Returns either resolved or real value.
	 */
	public String getValue() {
		return resolved != null ? resolved : value;
	}

	/**
	 * Resolve value for macros. Returns <code>true</code> if
	 * value is resolved.
	 */
	public boolean resolveValue(BeanTemplateResolver resolver) {
		String newValue = BeanTemplate.parse(value, resolver, StringPool.EMPTY);
		if (newValue.equals(value) == false) {
			resolved = newValue;
			return true;
		}
		resolved = null;
		return false;
	}


	@Override
	public String toString() {
		return "PropsValue{" + value + (resolved == null ? "" : "}{" + resolved) + '}';
	}
}
