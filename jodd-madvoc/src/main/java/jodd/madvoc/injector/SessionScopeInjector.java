// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeData;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ScopeDataResolver;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Servlet session scope injector.
 */
public class SessionScopeInjector extends BaseScopeInjector implements Injector, Outjector {

	public SessionScopeInjector(ScopeDataResolver scopeDataResolver) {
		super(ScopeType.SESSION, scopeDataResolver);
	}

	public void inject(ActionRequest actionRequest) {
		Object[] targets = actionRequest.getTargets();

		ScopeData.In[][] injectData = lookupInData(actionRequest);
		if (injectData == null) {
			return;
		}

		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpSession session = servletRequest.getSession();

		Enumeration attributeNames = session.getAttributeNames();

		while (attributeNames.hasMoreElements()) {
			String attrName = (String) attributeNames.nextElement();

			for (int i = 0; i < targets.length; i++) {
				Object target = targets[i];
				ScopeData.In[] scopes = injectData[i];
				if (scopes == null) {
					continue;
				}

				for (ScopeData.In in : scopes) {
					String name = getMatchedPropertyName(in, attrName);
					if (name != null) {
						Object attrValue = session.getAttribute(attrName);
						setTargetProperty(target, name, attrValue, in.create);
					}
				}
			}
		}
	}

	public void outject(ActionRequest actionRequest) {
		Object[] targets = actionRequest.getTargets();

		ScopeData.Out[][] outjectData = lookupOutData(actionRequest);
		if (outjectData == null) {
			return;
		}

		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpSession session = servletRequest.getSession();

		for (int i = 0; i < targets.length; i++) {
			Object target = targets[i];
			ScopeData.Out[] scopes = outjectData[i];
			if (scopes == null) {
				continue;
			}

			for (ScopeData.Out out : scopes) {
				Object value = getTargetProperty(target, out);
				session.setAttribute(out.name, value);
			}
		}
	}
}