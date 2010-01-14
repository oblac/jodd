// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import jodd.servlet.upload.MultipartRequestWrapper;
import jodd.servlet.upload.MultipartRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Populates java bean from {@link MultipartRequestWrapper} objects.
 */
public class MultipartRequestWrapperBeanLoader implements BeanLoader {

	private boolean trim;

	public MultipartRequestWrapperBeanLoader() {
	}

	public MultipartRequestWrapperBeanLoader(boolean trim) {
		this.trim = trim;
	}

	public static void loadBean(Object bean, Object request, boolean trim) {
		if (request instanceof MultipartRequestWrapper) {
			MultipartRequest mrequest = ((MultipartRequestWrapper) request).getMultipartRequest();
			if (mrequest != null) {
				// multipart
				MultipartRequestBeanLoader.loadBean(bean, mrequest, trim);
			} else {
				// regular
				HttpServletRequest req = (HttpServletRequest) ((MultipartRequestWrapper) request).getRequest();
				RequestBeanLoader.loadBean(bean, req, trim);
			}
		}
	}

	public void load(Object bean, Object request) {
		loadBean(bean, request, trim);
	}
}
