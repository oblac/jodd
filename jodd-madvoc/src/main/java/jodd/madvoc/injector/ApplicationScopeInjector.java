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
		ScopeData.In[] injectData = lookupInData(actionRequest.getActionConfig());
		if (injectData == null) {
			return;
		}

		Object target = actionRequest.getAction();
		ServletContext servletContext = actionRequest.getHttpServletRequest().getSession().getServletContext();

		inject(target, servletContext, injectData);
	}

	public void injectContext(Object target, ServletContext servletContext) {
		ScopeData.In[] injectData = lookupInData(target.getClass());
		if (injectData == null) {
			return;
		}
		inject(target, servletContext, injectData);
	}

	protected void inject(Object target, ServletContext servletContext, ScopeData.In[] injectData) {
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
		ScopeData.Out[] outjectData = lookupOutData(actionRequest.getActionConfig());
		if (outjectData == null) {
			return;
		}

		Object target = actionRequest.getAction();
		ServletContext context = actionRequest.getHttpServletRequest().getSession().getServletContext();

		for (ScopeData.Out out : outjectData) {
			Object value = getTargetProperty(target, out);
			context.setAttribute(out.name, value);
		}
	}
}