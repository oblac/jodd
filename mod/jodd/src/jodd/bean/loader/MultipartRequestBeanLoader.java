// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import jodd.bean.BeanUtil;
import jodd.servlet.upload.MultipartRequest;
import jodd.servlet.upload.FileUpload;

/**
 * Populates java bean from {@link MultipartRequest} objects.
 */
public class MultipartRequestBeanLoader implements BeanLoader {

	private boolean trim;

	public MultipartRequestBeanLoader() {
	}

	public MultipartRequestBeanLoader(boolean trim) {
		this.trim = trim;
	}

	public static void loadBean(Object bean, Object request, boolean trim) {
		if (request instanceof MultipartRequest) {
			MultipartRequest mrequest = (MultipartRequest) request;
			for (Object o : mrequest.getParameterNames()) {
				String paramName = (String) o;
				String[] paramValues = mrequest.getParameterValues(paramName);
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
					if (paramValues.length == 1) {
						// send just String
						BeanUtil.setPropertyForced(bean, paramName, paramValues[0]);
					} else {
						// send String array
						BeanUtil.setPropertyForced(bean, paramName, paramValues);
					}
				} catch (Exception bex) {
					// ignore exception
				}
			}

			for (Object o1 : mrequest.getFileParameterNames()) {
				String paramName = (String) o1;
				FileUpload[] uf = mrequest.getFiles(paramName);
				if (uf == null) {
					continue;
				}
				if (uf.length == 0) {
					continue;
				}
				try {
					if (uf.length == 1) {
						BeanUtil.setPropertyForcedSilent(bean, paramName, uf[0]);
					} else {
						BeanUtil.setPropertyForcedSilent(bean, paramName, uf);
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
