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

package jodd.madvoc.result;

import jodd.bean.BeanTemplateParser;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.Scope;
import jodd.servlet.DispatcherUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simply sends permanent redirection to an external location.
 */
public class ServletPermanentRedirectActionResult implements ActionResult<PermanentRedirect> {

	protected final BeanTemplateParser beanTemplateParser = new BeanTemplateParser();

	public ServletPermanentRedirectActionResult() {
		beanTemplateParser.setMacroPrefix(null);
		beanTemplateParser.setMacroStart("{");
		beanTemplateParser.setMacroEnd("}");
	}

	@In
	@Scope(ScopeType.CONTEXT)
	protected ResultMapper resultMapper;

	/**
	 * Redirects to the given location. Provided path is parsed, action is used as a value context.
	 */
	@Override
	public void render(ActionRequest actionRequest, PermanentRedirect redirectResult) {
		String resultBasePath = actionRequest.getActionRuntime().resultBasePath();

		String resultPath;
		final String resultValue = redirectResult.path();

		if (resultValue.startsWith("http://") || resultValue.startsWith("https://")) {
			resultPath = resultValue;
		}
		else {
			resultPath = resultMapper.resolveResultPathString(resultBasePath, resultValue);
		}

		HttpServletRequest request = actionRequest.getHttpServletRequest();
		HttpServletResponse response = actionRequest.getHttpServletResponse();

		String path = resultPath;
		path = beanTemplateParser.parseWithBean(path, actionRequest.getAction());

		redirect(request, response, path);
	}

	protected void redirect(HttpServletRequest request, HttpServletResponse response, String path) {
		DispatcherUtil.redirectPermanent(request, response, path);
	}
}