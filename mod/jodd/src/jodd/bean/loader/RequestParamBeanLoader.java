// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

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
public class RequestParamBeanLoader extends BaseBeanLoader {

	public void load(Object bean, Object source) {
		if (source instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) source;

			Enumeration paramNames = httpServletRequest.getParameterNames();
			while (paramNames.hasMoreElements()) {
				String paramName = (String) paramNames.nextElement();
				String[] paramValues = httpServletRequest.getParameterValues(paramName);
				if (paramValues == null) {
					continue;
				}
				if (paramValues.length == 0) {
					continue;
				}

				if (paramValues.length == 1) {	// use just String
					setProperty(bean, paramName, paramValues[0]);
				} else {						// use String array
					setProperty(bean, paramName, paramValues);
				}

			}
		}
	}

}