// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import jodd.servlet.upload.MultipartRequest;
import jodd.servlet.upload.FileUpload;

/**
 * Populates java bean from {@link MultipartRequest} objects.
 */
public class MultipartRequestBeanLoader extends BaseBeanLoader {

	public void load(Object bean, Object source) {
		if (source instanceof MultipartRequest) {

			MultipartRequest multipartRequest = (MultipartRequest) source;

			for (Object o : multipartRequest.getParameterNames()) {
				String paramName = (String) o;
				String[] paramValues = multipartRequest.getParameterValues(paramName);
				if (paramValues == null) {
					continue;
				}
				if (paramValues.length == 0) {
					continue;
				}
				if (paramValues.length == 1) {		// send just String
					setProperty(bean, paramName, paramValues[0]);
				} else {							// send String array
					setProperty(bean, paramName, paramValues);
				}
			}

			for (Object o1 : multipartRequest.getFileParameterNames()) {
				String paramName = (String) o1;
				FileUpload[] uf = multipartRequest.getFiles(paramName);
				if (uf == null) {
					continue;
				}
				if (uf.length == 0) {
					continue;
				}

				if (uf.length == 1) {
					setProperty(bean, paramName, uf[0]);
				} else {
					setProperty(bean, paramName, uf);
				}
			}
		}
	}
}