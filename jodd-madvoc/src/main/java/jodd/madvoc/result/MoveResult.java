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

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.meta.In;
import jodd.util.URLCoder;
import jodd.servlet.DispatcherUtil;
import jodd.util.RandomString;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Process move results.
 */
public class MoveResult extends BaseActionResult<String> {

	public static final String NAME = "move";

	public MoveResult() {
		super(NAME);
	}

	@In(scope = ScopeType.CONTEXT)
	protected MadvocConfig madvocConfig;

	@In(scope = ScopeType.CONTEXT)
	protected ResultMapper resultMapper;

	/**
	 * Returns unique id, random long value.
	 */
	protected String generateUniqueId() {
		return RandomString.getInstance().randomAlphaNumeric(32);
	}

	/**
	 * Saves action in the session under some id that is added as request parameter.
	 */
	public void render(ActionRequest actionRequest, String resultValue) throws Exception {
		String resultBasePath = actionRequest.getActionConfig().getResultBasePath();

		String resultPath = resultMapper.resolveResultPathString(resultBasePath, resultValue);

		HttpServletRequest httpServletRequest = actionRequest.getHttpServletRequest();
		HttpSession session = httpServletRequest.getSession();

		String id = generateUniqueId();
		session.setAttribute(id, actionRequest);

		String path = resultPath;
		path = URLCoder.build(path).queryParam(madvocConfig.getAttributeMoveId(), id).toString();
		DispatcherUtil.redirect(httpServletRequest, actionRequest.getHttpServletResponse(), path);
	}

}