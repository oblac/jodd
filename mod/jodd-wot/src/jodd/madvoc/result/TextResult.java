// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.io.StreamUtil;
import jodd.madvoc.ActionRequest;
import jodd.util.MimeTypes;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Text result returns a result value, i.e. a string.
 * Useful for JSON responses, when resulting string is built
 * in the action.
 */
public class TextResult extends ActionResult {

	public static final String NAME = "text";

	public TextResult() {
		super(NAME);
	}

	@Override
	public void render(ActionRequest actionRequest, Object resultObject, String resultValue, String resultPath) throws Exception {
		HttpServletResponse response = actionRequest.getHttpServletResponse();
		response.setContentType(MimeTypes.MIME_TEXT_PLAIN);
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.println(resultValue);
		} finally {
			StreamUtil.close(writer);
		}
	}
}
