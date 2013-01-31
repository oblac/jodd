// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.madvoc.result;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.meta.In;
import jodd.madvoc.result.ActionResult;
import jodd.util.MimeTypes;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * Generic JSON result.
 */
public class JSONResult extends ActionResult {

	public static final String NAME = "json";

	public JSONResult() {
		super(NAME);
	}

	@In(scope = ScopeType.CONTEXT)
	protected MadvocConfig madvocConfig;


	@Override
	public void render(ActionRequest actionRequest, Object resultObject, String resultValue, String resultPath) throws Exception {
		HttpServletResponse response = actionRequest.getHttpServletResponse();

		response.setContentType(MimeTypes.MIME_APPLICATION_JSON);

		byte[] data = resultValue.getBytes(madvocConfig.getEncoding());

		OutputStream os = response.getOutputStream();
		os.write(data);
		os.flush();
	}

}
