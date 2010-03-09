// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.madvoc.ActionRequest;
import jodd.util.StringUtil;

import javax.servlet.http.HttpServletResponse;

/**
 * Sets HTTP status or error code.
 */
public class HttpStatusResult extends ActionResult {

	public static final String NAME = "http";

	public HttpStatusResult() {
		super(NAME);
	}

	@Override
	public void render(ActionRequest actionRequest, Object resultObject, String resultValue, String resultPath) throws Exception {
		HttpServletResponse response = actionRequest.getHttpServletResponse();
		boolean isError = false;
		if (StringUtil.endsWithChar(resultValue, '!')) {
			isError = true;
			resultValue = resultValue.substring(0, resultValue.length() - 1);
		}
		if (isError) {
			response.sendError(Integer.parseInt(resultValue));
		} else {
			response.setStatus(Integer.parseInt(resultValue));
		}

	}
}
