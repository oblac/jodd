// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.madvoc.result;

import jodd.joy.madvoc.action.AppAction;
import jodd.joy.vtor.VtorUtil;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocException;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.meta.In;
import jodd.madvoc.result.ActionResult;
import jodd.servlet.ServletUtil;
import jodd.util.CharUtil;
import jodd.util.MimeTypes;
import jodd.vtor.Violation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;

/**
 * vtor json result.
 */
public class VtorJsonResult extends ActionResult {

	public static final String NAME = "vtor-json";

	/**
	 * Defines response content type of returned json string.
	 * By default it is set to application/json.
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


	@Override
	public void render(ActionRequest actionRequest, Object resultObject, String resultValue, String resultPath) throws Exception {
		HttpServletRequest request = actionRequest.getHttpServletRequest();
		HttpServletResponse response = actionRequest.getHttpServletResponse();

		Object action = actionRequest.getAction();
		if (action instanceof AppAction == false) {
			throw new MadvocException("Not an app action!");
		}
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
