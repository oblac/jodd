// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.i18n;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.interceptor.BaseActionInterceptor;
import jodd.proxetta.ProxettaUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * Prepares bundle name for current request.
 */
public class I18nInterceptor extends BaseActionInterceptor {

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

		clazz = ProxettaUtil.getTargetClass(clazz);

		return clazz.getName();
	}

}
