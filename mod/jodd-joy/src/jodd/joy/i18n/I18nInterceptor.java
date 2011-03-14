// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.i18n;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.proxetta.asm.ProxettaNaming;

import javax.servlet.http.HttpServletRequest;

/**
 * Prepares bundle name for current request.
 */
public class I18nInterceptor extends ActionInterceptor {

	@Override
	public Object intercept(ActionRequest actionRequest) throws Exception {
		HttpServletRequest request = actionRequest.getHttpServletRequest();

		// defines request bundle of this http request
		LocalizationUtil.setRequestBundleName(request, getActionClassName(actionRequest.getAction()));

		return actionRequest.invoke();
	}

	/**
	 * Returns correct action class name. Detects Proxetta classes.
	 */
	protected String getActionClassName(Object action) {
		Class clazz = action.getClass();
		String name = clazz.getName();
		if (name.endsWith(ProxettaNaming.PROXY_CLASS_NAME_SUFFIX)) {
			clazz = clazz.getSuperclass();
			name = clazz.getName();
		}
		return name;
	}

}
