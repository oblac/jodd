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

package jodd.joy.madvoc.result;

import jodd.joy.madvoc.action.AppAction;
import jodd.joy.vtor.VtorUtil;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.meta.In;
import jodd.madvoc.result.BaseActionResult;
import jodd.util.CharUtil;
import jodd.util.MimeTypes;
import jodd.vtor.Violation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;

/**
 * VTor validation result as JSON string.
 */
public class VtorJsonResult extends BaseActionResult<String> {

	public static final String NAME = "vtor-json";

	/**
	 * Defines response content type of returned json string.
	 * By default it is set to <code>application/json</code>.
	 * If set to <code>null</code> response will be not set.
	 * <p>
	 * Some form plugins (as jquery form plugin) submits multipart form
	 * using hidden iframe, not using ajax. Therefore, Accept header
	 * does not contains application/json (since it is regular post and ajax).
	 * In this case change response content type to "text/html". Or disable
	 * iframe posting if possible.
	 */
	public static String jsonResponseContentType = MimeTypes.MIME_APPLICATION_JSON;

	public VtorJsonResult() {
		super(NAME);
	}

	@In(scope = ScopeType.CONTEXT)
	protected MadvocConfig madvocConfig;


	public void render(ActionRequest actionRequest, String resultValue) throws Exception {
		HttpServletRequest request = actionRequest.getHttpServletRequest();
		HttpServletResponse response = actionRequest.getHttpServletResponse();

		Object action = actionRequest.getAction();
		AppAction appAction = (AppAction) action;

		List<Violation> list = appAction.violations();

		String result = VtorUtil.createViolationsJsonString(request, list);

		if (jsonResponseContentType != null) {
			response.setContentType(jsonResponseContentType);
		}

		char[] chars = result.toCharArray();
		byte[] data = CharUtil.toByteArray(chars, madvocConfig.getEncoding());

		OutputStream os = response.getOutputStream();
		os.write(data);
		os.flush();
	}

}