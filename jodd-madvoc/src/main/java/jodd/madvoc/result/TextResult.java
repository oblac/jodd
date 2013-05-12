// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.io.StreamUtil;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.meta.In;
import jodd.util.MimeTypes;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Text result returns a result value, i.e. a string.
 * Useful for JSON responses, when resulting string is built
 * in the action.
 */
public class TextResult extends ActionResult {

	public static final String NAME = "text";

	@In(scope = ScopeType.CONTEXT)
	protected MadvocConfig madvocConfig;

	public TextResult() {
		super(NAME);
	}

	@Override
	public void render(ActionRequest actionRequest, Object resultObject, String resultValue, String resultPath) throws Exception {
		HttpServletResponse response = actionRequest.getHttpServletResponse();

		response.setContentType(MimeTypes.MIME_TEXT_PLAIN);
		response.setCharacterEncoding(madvocConfig.getEncoding());

		byte[] data = resultValue.getBytes(madvocConfig.getEncoding());
		response.setContentLength(data.length);

		OutputStream out = null;
		try {
			out = response.getOutputStream();
			out.write(data);
		} finally {
			StreamUtil.close(out);
		}
	}
}
