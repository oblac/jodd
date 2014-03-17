// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.bean.BeanUtil;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocException;
import jodd.madvoc.ScopeData;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ScopeDataResolver;

/**
 * Abstract base class for HTTP scopes injection.
 */
public abstract class BaseScopeInjector {

	private static final Logger log = LoggerFactory.getLogger(BaseScopeInjector.class);

	protected final ScopeDataResolver scopeDataResolver;
	protected final ScopeType scopeType;
	protected final MadvocConfig madvocConfig;

	/**
	 * Creates scope injector for provided {@link jodd.madvoc.ScopeType}.
	 */
	protected BaseScopeInjector(ScopeType scopeType, MadvocConfig madvocConfig, ScopeDataResolver scopeDataResolver) {
		this.scopeType = scopeType;
		this.madvocConfig = madvocConfig;
		this.scopeDataResolver = scopeDataResolver;
	}

	// ---------------------------------------------------------------- beanutil

	/**
	 * Sets target bean property, optionally creates instance if doesn't exist.
	 */
	protected void setTargetProperty(Object target, String name, Object attrValue) {
		if (BeanUtil.hasDeclaredRootProperty(target, name)) {
			try {
				BeanUtil.setDeclaredPropertyForced(target, name, attrValue);
			} catch (Exception ex) {
				if (madvocConfig.isInjectionErrorThrowsException()) {
					throw new MadvocException(ex);
				} else {
					if (log.isWarnEnabled()) {
						log.warn("Injection failed: " + name + ". " + ex.toString());
					}
				}
			}
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
	 * Returns IN data for current scope type.
	 */
	public ScopeData.In[] lookupInData(ScopeData[] scopeData) {
		if (scopeData == null) {
			return null;
		}
		ScopeData sd = scopeData[scopeType.value()];
		if (sd == null) {
			return null;
		}

		return sd.in;
	}

	/**
	 * Lookups scope data for many targets.
	 */
	public ScopeData.In[][] lookupInData(ActionRequest actionRequest) {
		return actionRequest.getActionConfig().ins[scopeType.value()];
	}

	/**
	 * Lookups scope data for many targets.
	 */
	public ScopeData.Out[][] lookupOutData(ActionRequest actionRequest) {
		return actionRequest.getActionConfig().outs[scopeType.value()];
	}

}