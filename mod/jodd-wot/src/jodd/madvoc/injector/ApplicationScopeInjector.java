// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ScopeType;

import javax.servlet.ServletContext;
import java.util.Enumeration;

/**
 * Servlet context injector.
 */
public class ApplicationScopeInjector extends BaseScopeInjector {

	public ApplicationScopeInjector() {
		super(ScopeType.APPLICATION);
	}

	public void inject(Object target, ServletContext context) {
		ScopeData.In[] injectData = lookupInData(target.getClass());
		if (injectData == null) {
			return;
		}
		Enumeration attributeNames = context.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attrName = (String) attributeNames.nextElement();
			for (ScopeData.In in : injectData) {
				String name = getMatchedPropertyName(in, attrName);
				if (name != null) {
					Object attrValue = context.getAttribute(attrName);
					setTargetProperty(target, name, attrValue, in.create);
					if (in.remove) {
						context.removeAttribute(attrName);
					}
				}
			}
		}
	}

	public void outject(Object target, ServletContext context) {
		ScopeData.Out[] outjectData = lookupOutData(target.getClass());
		if (outjectData == null) {
			return;
		}
		for (ScopeData.Out out : outjectData) {
			Object value = getTargetProperty(target, out);
			context.setAttribute(out.name, value);
		}
	}
}