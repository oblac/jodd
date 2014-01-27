// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.madvoc.result;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.result.BaseActionResult;
import jodd.util.MimeTypes;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * Generic JSON result.
 */
public class JSONResult extends BaseActionResult<String> {

	public static final String NAME = "json";

	public JSONResult() {
		super(NAME);
	}

	public void render(ActionRequest actionRequest, String resultValue) throws Exception {
		HttpServletResponse response = actionRequest.getHttpServletResponse();

		String encoding = response.getCharacterEncoding();
		response.setContentType(MimeTypes.MIME_APPLICATION_JSON);
		response.setCharacterEncoding(encoding);

		byte[] data = resultValue.getBytes(encoding);

		OutputStream os = response.getOutputStream();
		os.write(data);
		os.flush();
	}

}