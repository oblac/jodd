// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.BaseActionInterceptor;
import jodd.servlet.wrapper.BufferResponseWrapper;
import jodd.util.ArraysUtil;

import javax.servlet.http.HttpServletResponse;

public class AppendingFilter extends BaseActionInterceptor implements ActionFilter {

	/*
	@Override
	public Object intercept(ActionRequest actionRequest) throws Exception {
		HttpServletResponse httpResponse = actionRequest.getHttpServletResponse();

		BufferResponseWrapper wrapper = new BufferResponseWrapper(httpResponse);
		actionRequest.setHttpServletResponse(wrapper);

		Object result = actionRequest.invoke();

		char[] chars = wrapper.getBufferContentAsChars();
		chars = ArraysUtil.join(chars, "peep!".toCharArray());
		wrapper.writeContentToResponse(chars);

		actionRequest.setHttpServletResponse(httpResponse);

		return result;
	}*/

	public Object intercept(ActionRequest actionRequest) throws Exception {
		HttpServletResponse httpResponse = actionRequest.getHttpServletResponse();

		BufferResponseWrapper wrapper = new BufferResponseWrapper(httpResponse);
		actionRequest.setHttpServletResponse(wrapper);

		Object result = actionRequest.invoke();

		wrapper.print("peep!");
		wrapper.writeContentToResponse();

		actionRequest.setHttpServletResponse(httpResponse);

		return result;
	}

}