// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import jodd.bean.BeanUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Populates object from servlet request parameters.
 * <p>
 * By java servlet specifications, parameter values are always String arrays
 * (String[]).  That is how they are sent to BeanUtil.setProperty(),
 * except in case when this array contains just one String element. In that
 * case it is sent as a single String.
 *
 */
public class RequestParamBeanLoader implements BeanLoader {

	private boolean trim;

	public RequestParamBeanLoader() {
	}

	public RequestParamBeanLoader(boolean trim) {
		this.trim = trim;
	}


	public static void loadBean(Object bean, Object request, boolean trim) {
		if (request instanceof HttpServletRequest) {
			Enumeration paramNames = ((HttpServletRequest)request).getParameterNames();
			while (paramNames.hasMoreElements()) {
				String paramName = (String) paramNames.nextElement();
				String[] paramValues = ((HttpServletRequest)request).getParameterValues(paramName);
				if (paramValues == null) {
					continue;
				}
				if (paramValues.length == 0) {
					continue;
				}
				if (trim == true) {
					for (int i = 0; i < paramValues.length; i++) {
						paramValues[i] = paramValues[i].trim();
					}
				}
				try {
					if (paramValues.length == 1) {	// use just String
						BeanUtil.setPropertyForcedSilent(bean, paramName, paramValues[0]);
					} else {	// use String array
						BeanUtil.setPropertyForcedSilent(bean, paramName, paramValues);
					}
				} catch (Exception ex) {
					// ignore exception
				}
			}
		}
	}

	public void load(Object bean, Object request) {
		loadBean(bean, request, trim);
	}

}
