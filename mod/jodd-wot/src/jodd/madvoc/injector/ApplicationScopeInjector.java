// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ScopeDataManager;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Servlet context injector.
 */
public class ApplicationScopeInjector extends ScopeInjector {

	public ApplicationScopeInjector(ScopeDataManager scopeDataManager) {
		super(scopeDataManager);
	}

	public void inject(Object target, ServletContext context) {
		ScopeData.In[] injectData = scopeDataManager.lookupInData(target, ScopeType.APPLICATION);
		if (injectData == null) {
			return;
		}

		Enumeration attributeNames = context.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attrName = (String) attributeNames.nextElement();
			for (ScopeData.In ii : injectData) {
				String name = getMatchedPropertyName(ii, attrName);
				if (name != null) {
					Object attrValue = context.getAttribute(attrName);
					setTargetProperty(target, name, attrValue, ii.create);
					if (ii.remove) {
						context.removeAttribute(attrName);
					}
				}
			}
		}
	}

	public void outject(Object target, ServletContext context) {
		ScopeData.Out[] outjectData = scopeDataManager.lookupOutData(target, ScopeType.APPLICATION);
		if (outjectData == null) {
			return;
		}

		for (ScopeData.Out oi : outjectData) {
			Object value = getTargetProperty(target, oi);
			context.setAttribute(oi.name, value);
		}
	}
}