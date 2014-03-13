// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Servlet session scope injector.
 */
public class SessionScopeInjector extends BaseScopeInjector implements Injector, Outjector {

	public SessionScopeInjector() {
		super(ScopeType.SESSION);
	}

	public void inject(ActionRequest actionRequest) {
		ScopeData.In[] injectData = lookupInData(actionRequest.getActionConfig());
		if (injectData == null) {
			return;
		}

		Object target = actionRequest.getAction();
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpSession session = servletRequest.getSession();

		Enumeration attributeNames = session.getAttributeNames();

		while (attributeNames.hasMoreElements()) {
			String attrName = (String) attributeNames.nextElement();

			for (ScopeData.In in : injectData) {
				String name = getMatchedPropertyName(in, attrName);
				if (name != null) {
					Object attrValue = session.getAttribute(attrName);
					setTargetProperty(target, name, attrValue, in.create);
				}
			}
		}

	}

	public void outject(ActionRequest actionRequest) {
		ScopeData.Out[] outjectData = lookupOutData(actionRequest.getActionConfig());
		if (outjectData == null) {
			return;
		}

		Object target = actionRequest.getAction();
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();

		HttpSession session = servletRequest.getSession();
		for (ScopeData.Out out : outjectData) {
			Object value = getTargetProperty(target, out);
			session.setAttribute(out.name, value);
		}
	}
}