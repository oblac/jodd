// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.madvoc;

import jodd.madvoc.filter.BaseActionFilter;
import jodd.servlet.wrapper.BufferResponseWrapper;

import javax.servlet.http.HttpServletResponse;

public class AppendingFilter extends BaseActionFilter {

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

	public Object filter(ActionRequest actionRequest) throws Exception {
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