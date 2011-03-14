// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.i18n;

import jodd.joy.madvoc.action.AppAction;
import jodd.joy.vtor.VtorUtil;
import jodd.proxetta.asm.ProxettaNaming;
import jodd.vtor.Violation;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.interceptor.ActionInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Prepares bundle name for current request.
 * Localizes error messages.
 */
public class I18nInterceptor extends ActionInterceptor {

	public static final String ATTR_NAME_VIOLATIONS = "violations";

	@Override
	public Object intercept(ActionRequest actionRequest) throws Exception {
		HttpServletRequest request = actionRequest.getHttpServletRequest();

		// defines request bundle of this http request
		LocalizationUtil.setRequestBundleName(request, getActionClassName(actionRequest.getAction()));

		Object result = actionRequest.invoke();
		Object action = actionRequest.getAction();
		if (action instanceof AppAction) {
			AppAction appAction = (AppAction) action;
			List<Violation> violations = appAction.violations();
			String data = VtorUtil.createViolationsJsonString(request, violations);
			request.setAttribute(ATTR_NAME_VIOLATIONS, data);
		}
		return result;
	}

	/**
	 * Returns correct action class name.
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
