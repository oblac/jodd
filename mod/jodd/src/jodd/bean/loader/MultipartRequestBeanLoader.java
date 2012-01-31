// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import jodd.servlet.upload.MultipartRequest;
import jodd.servlet.upload.FileUpload;

/**
 * Populates java bean from {@link MultipartRequest} objects.
 */
public class MultipartRequestBeanLoader extends BaseBeanLoader {

	protected boolean trim;

	public MultipartRequestBeanLoader() {
		this.trim = false;
	}

	public MultipartRequestBeanLoader(boolean trim) {
		this.trim = trim;
	}

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
				if (trim == true) {
					for (int i = 0; i < paramValues.length; i++) {
						paramValues[i] = paramValues[i].trim();
					}
				}
				if (paramValues.length == 1) {		// send just String
					beanUtilBean.setPropertyForced(bean, paramName, paramValues[0]);
				} else {							// send String array
					beanUtilBean.setPropertyForced(bean, paramName, paramValues);
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
					beanUtilBean.setPropertyForcedSilent(bean, paramName, uf[0]);
				} else {
					beanUtilBean.setPropertyForcedSilent(bean, paramName, uf);
				}
			}
		}
	}
}