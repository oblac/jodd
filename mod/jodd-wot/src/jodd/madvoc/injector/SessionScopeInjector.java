// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ScopeDataManager;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Servlet session scope injector.
 */
public class SessionScopeInjector extends ScopeInjector {

	public SessionScopeInjector(ScopeDataManager scopeDataManager) {
		super(scopeDataManager);
	}

	public void inject(Object target, HttpServletRequest servletRequest) {
		ScopeData.In[] injectData = scopeDataManager.lookupInData(target, ScopeType.SESSION);
		if (injectData == null) {
			return;
		}

		HttpSession session = servletRequest.getSession();
		Enumeration attributeNames = session.getAttributeNames();

		while (attributeNames.hasMoreElements()) {
			String attrName = (String) attributeNames.nextElement();
			for (ScopeData.In ii : injectData) {
				String name = getMatchedPropertyName(ii, attrName);
				if (name != null) {
					Object attrValue = session.getAttribute(attrName);
					setTargetProperty(target, name, attrValue, ii.create);
					if (ii.remove) {
						session.removeAttribute(attrName);
					}
				}
			}
		}
	}

	public void outject(Object target, HttpServletRequest servletRequest) {
		ScopeData.Out[] outjectData = scopeDataManager.lookupOutData(target, ScopeType.SESSION);
		if (outjectData == null) {
			return;
		}

		HttpSession session = servletRequest.getSession();
		for (ScopeData.Out oi : outjectData) {
			Object value = getTargetProperty(target, oi);
			session.setAttribute(oi.name, value);
		}
	}
}