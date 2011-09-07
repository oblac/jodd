// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.meta.In;
import jodd.madvoc.component.MadvocConfig;
import jodd.servlet.ServletUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Raw results directly writes byte context to the output.
 * Output is closed after writing.
 */
public class RawResult extends ActionResult {

	public static final String NAME = "raw";

	public RawResult() {
		super(NAME);
	}

	@In(scope = ScopeType.CONTEXT)
	protected MadvocConfig madvocConfig;

	@Override
	public void render(ActionRequest actionRequest, Object resultObject, String resultValue, String resultPath) throws IOException {
		if (resultObject == null) {
			return;
		}
		if (resultObject instanceof RawResultData != true) {
			resultObject = new RawData(resultValue.getBytes(madvocConfig.getEncoding()));
		}

		RawResultData result = (RawResultData) resultObject;

		byte[] data = result.getBytes();

		HttpServletResponse response = actionRequest.getHttpServletResponse();

		ServletUtil.prepareDownload(response, result.getDownloadFileName(), result.getMimeType(), data.length);

		OutputStream os = response.getOutputStream();
		os.write(data);
		os.flush();
	}
}
