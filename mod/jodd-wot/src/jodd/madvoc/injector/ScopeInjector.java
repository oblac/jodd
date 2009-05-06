// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.component.ScopeDataManager;
import jodd.bean.BeanUtil;

/**
 * Helper class for HTTP scopes injection.
 */
public abstract class ScopeInjector {

	protected final ScopeDataManager scopeDataManager;

	protected ScopeInjector(ScopeDataManager scopeDataManager) {
		this.scopeDataManager = scopeDataManager;
	}


	/**
	 * Sets target property, optionally creates instance if doesn't exist.
	 */
	protected void setTargetProperty(Object target, String name, Object attrValue, boolean create) {
		if (create == true) {
			BeanUtil.setDeclaredPropertyForcedSilent(target, name, attrValue);
		} else {
			BeanUtil.setDeclaredPropertySilent(target, name, attrValue);
		}
	}

	protected void setTargetProperty(Object target, String name, Object[] paramValues, boolean create) {
		Object value = (paramValues.length == 1 ? paramValues[0] : paramValues);
		if (create == true) {
			BeanUtil.setDeclaredPropertyForcedSilent(target, name, value);
		} else {
			BeanUtil.setDeclaredPropertySilent(target, name, value);
		}
	}

	/**
	 * Reads target ptoperty.
	 */
	protected Object getTargetProperty(Object target, ScopeData.Out out) {
		if (out.target == null) {
			return BeanUtil.getDeclaredProperty(target, out.name);
		} else {
			return BeanUtil.getDeclaredProperty(target, out.target);
		}
	}

	/**
	 * Returns matched property name or <code>null</code> if name is not matched.
	 * <p>
	 * Matches if attribute name matches the required field name. If the match is positive,
	 * injection or outjection is performed on the field.
	 * <p>
	 * Parameter name matches field name if param name starts with field name and has
	 * either '.' or '[' after the field name.
	 * <p>
	 * Returns real property name, once when name is matched.
	 */
	protected String getMatchedPropertyName(ScopeData.In in, String attrName) {
		// match
		if (attrName.startsWith(in.name) == false) {
			return null;
		}
		int requiredLen = in.name.length();
		if (attrName.length() >= requiredLen + 1) {
			if ((attrName.charAt(requiredLen) != '.') && (attrName.charAt(requiredLen) != '[')) {
				return null;
			}
		}

		// get param
		if (in.target == null) {
			return attrName;
		}
		return in.target + attrName.substring(in.name.length());
	}

}
