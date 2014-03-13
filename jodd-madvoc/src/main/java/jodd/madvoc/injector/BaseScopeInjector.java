// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.bean.BeanUtil;
import jodd.madvoc.ActionConfig;
import jodd.madvoc.ScopeType;

/**
 * Abstract base class for HTTP scopes injection.
 */
public abstract class BaseScopeInjector {

	protected static final ScopeDataResolver scopeDataResolver = new ScopeDataResolver();

	protected final ScopeType scopeType;

	/**
	 * Creates scope injector for provided {@link jodd.madvoc.ScopeType}.
	 */
	protected BaseScopeInjector(ScopeType scopeType) {
		this.scopeType = scopeType;
	}

	// ---------------------------------------------------------------- beanutil

	/**
	 * Sets target bean property, optionally creates instance if doesn't exist.
	 */
	protected void setTargetProperty(Object target, String name, Object attrValue, boolean create) {
		if (create == true) {
			BeanUtil.setDeclaredPropertyForcedSilent(target, name, attrValue);
		} else {
			BeanUtil.setDeclaredPropertySilent(target, name, attrValue);
		}
	}

	/**
	 * Reads target property.
	 */
	protected Object getTargetProperty(Object target, ScopeData.Out out) {
		if (out.target == null) {
			return BeanUtil.getDeclaredProperty(target, out.name);
		} else {
			return BeanUtil.getDeclaredProperty(target, out.target);
		}
	}

	// ---------------------------------------------------------------- matched property

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
			char c = attrName.charAt(requiredLen);
			if ((c != '.') && (c != '[')) {
				return null;
			}
		}

		// get param
		if (in.target == null) {
			return attrName;
		}
		return in.target + attrName.substring(in.name.length());
	}


	// ---------------------------------------------------------------- delegates

	/**
	 * Delegates to {@link jodd.madvoc.injector.ScopeDataResolver#lookupInData(jodd.madvoc.ActionConfig, jodd.madvoc.ScopeType)}.
	 */
	public ScopeData.In[] lookupInData(ActionConfig actionConfig) {
		return scopeDataResolver.lookupInData(actionConfig, scopeType);
	}

	/**
	 * Delegates to {@link jodd.madvoc.injector.ScopeDataResolver#lookupInData(Class, jodd.madvoc.ScopeType)}.
	 */
	public ScopeData.In[] lookupInData(Class type) {
		return scopeDataResolver.lookupInData(type, scopeType);
	}

	/**
	 * Delegates to {@link jodd.madvoc.injector.ScopeDataResolver#lookupOutData(jodd.madvoc.ActionConfig, jodd.madvoc.ScopeType)}.
	 */
	public ScopeData.Out[] lookupOutData(ActionConfig actionConfig) {
		return scopeDataResolver.lookupOutData(actionConfig, scopeType);
	}

	/**
	 * Delegates to {@link jodd.madvoc.injector.ScopeDataResolver#lookupOutData(Class, jodd.madvoc.ScopeType)}.
	 */
	public ScopeData.Out[] lookupOutData(Class type) {
		return scopeDataResolver.lookupOutData(type, scopeType);
	}

}