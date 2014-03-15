// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeData;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ScopeDataResolver;

import javax.servlet.ServletContext;
import java.util.Enumeration;

/**
 * Servlet context injector.
 */
public class ApplicationScopeInjector extends BaseScopeInjector
		implements Injector, Outjector, ContextInjector<ServletContext> {

	public ApplicationScopeInjector(ScopeDataResolver scopeDataResolver) {
		super(ScopeType.APPLICATION, scopeDataResolver);
	}

	public void inject(ActionRequest actionRequest) {
		Object[] targets = actionRequest.getTargets();

		ScopeData.In[][] injectData = lookupInData(actionRequest);
		if (injectData == null) {
			return;
		}
		ServletContext servletContext = actionRequest.getHttpServletRequest().getSession().getServletContext();

		Enumeration attributeNames = servletContext.getAttributeNames();

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
						Object attrValue = servletContext.getAttribute(attrName);
						setTargetProperty(target, name, attrValue, in.create);
					}
				}
			}
		}
	}

	public void injectContext(Object target, ServletContext servletContext) {
		ScopeData.In[] injectData = resolveInData(target.getClass());
		if (injectData == null) {
			return;
		}

		Enumeration attributeNames = servletContext.getAttributeNames();

		while (attributeNames.hasMoreElements()) {
			String attrName = (String) attributeNames.nextElement();
			for (ScopeData.In in : injectData) {
				String name = getMatchedPropertyName(in, attrName);
				if (name != null) {
					Object attrValue = servletContext.getAttribute(attrName);
					setTargetProperty(target, name, attrValue, in.create);
				}
			}
		}
	}

	public void outject(ActionRequest actionRequest) {
		ScopeData.Out[][] outjectData = lookupOutData(actionRequest);
		if (outjectData == null) {
			return;
		}

		Object[] targets = actionRequest.getTargets();
		ServletContext context = actionRequest.getHttpServletRequest().getSession().getServletContext();

		for (int i = 0; i < targets.length; i++) {
			Object target = targets[i];
			ScopeData.Out[] scopes = outjectData[i];
			if (scopes == null) {
				continue;
			}

			for (ScopeData.Out out : scopes) {
				Object value = getTargetProperty(target, out);
				context.setAttribute(out.name, value);
			}
		}
	}
}