// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ScopeType;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Servlet session scope injector.
 */
public class SessionScopeInjector extends BaseScopeInjector {

	public SessionScopeInjector() {
		super(ScopeType.SESSION);
	}

	public void inject(Object target, HttpServletRequest servletRequest) {
		ScopeData.In[] injectData = lookupInData(target.getClass());
		if (injectData == null) {
			return;
		}
		HttpSession session = servletRequest.getSession();
		Enumeration attributeNames = session.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attrName = (String) attributeNames.nextElement();
			for (ScopeData.In in : injectData) {
				String name = getMatchedPropertyName(in, attrName);
				if (name != null) {
					Object attrValue = session.getAttribute(attrName);
					setTargetProperty(target, name, attrValue, in.create);
					if (in.remove) {
						session.removeAttribute(attrName);
					}
				}
			}
		}

	}

	public void outject(Object target, HttpServletRequest servletRequest) {
		ScopeData.Out[] outjectData = lookupOutData(target.getClass());
		if (outjectData == null) {
			return;
		}
		HttpSession session = servletRequest.getSession();
		for (ScopeData.Out out : outjectData) {
			Object value = getTargetProperty(target, out);
			session.setAttribute(out.name, value);
		}
	}
}