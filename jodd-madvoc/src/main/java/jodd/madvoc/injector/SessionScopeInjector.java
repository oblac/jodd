// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

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
		silent = true;
	}

	public void inject(ActionRequest actionRequest) {
		ScopeData[] injectData = lookupScopeData(actionRequest);
		if (injectData == null) {
			return;
		}

		Target[] targets = actionRequest.getTargets();
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpSession session = servletRequest.getSession();

		Enumeration attributeNames = session.getAttributeNames();

		while (attributeNames.hasMoreElements()) {
			String attrName = (String) attributeNames.nextElement();

			for (int i = 0; i < targets.length; i++) {
				Target target = targets[i];
				if (injectData[i] == null) {
					continue;
				}
				ScopeData.In[] scopes = injectData[i].in;
				if (scopes == null) {
					continue;
				}

				for (ScopeData.In in : scopes) {
					String name = getMatchedPropertyName(in, attrName);
					if (name != null) {
						Object attrValue = session.getAttribute(attrName);
						setTargetProperty(target, name, attrValue);
					}
				}
			}
		}
	}

	public void outject(ActionRequest actionRequest) {
		ScopeData[] outjectData = lookupScopeData(actionRequest);
		if (outjectData == null) {
			return;
		}

		Target[] targets = actionRequest.getTargets();
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpSession session = servletRequest.getSession();

		for (int i = 0; i < targets.length; i++) {
			Target target = targets[i];
			if (outjectData[i] == null) {
				continue;
			}
			ScopeData.Out[] scopes = outjectData[i].out;
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