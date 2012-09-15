// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import jodd.servlet.upload.MultipartRequestWrapper;
import jodd.servlet.upload.MultipartRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Populates java bean from {@link MultipartRequestWrapper} objects.
 */
public class MultipartRequestWrapperBeanLoader extends BaseBeanLoader {

	protected final MultipartRequestBeanLoader multipartRequestBeanLoader;
	protected final RequestBeanLoader requestBeanLoader;

	public MultipartRequestWrapperBeanLoader() {
		multipartRequestBeanLoader = new MultipartRequestBeanLoader();
		requestBeanLoader = new RequestBeanLoader();
	}

	public void load(Object bean, Object source) {
		if (source instanceof MultipartRequestWrapper) {
			MultipartRequestWrapper multipartRequestWrapper = (MultipartRequestWrapper) source;

			MultipartRequest multipartRequest = multipartRequestWrapper.getMultipartRequest();

			if (multipartRequest != null) {
				// multipart
				multipartRequestBeanLoader.load(bean, multipartRequest);
			} else {
				// regular request
				HttpServletRequest req = (HttpServletRequest) multipartRequestWrapper.getRequest();
				requestBeanLoader.load(bean, req);
			}
		}
	}
}
