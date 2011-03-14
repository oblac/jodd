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
import jodd.util.CharUtil;
import jodd.vtor.Violation;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;

/**
 * vtor json result.
 */
public class VtorJsonResult extends ActionResult {

	public static final String NAME = "vtor-json";

	public VtorJsonResult() {
		super(NAME);
	}

	@In(scope = ScopeType.CONTEXT)
	protected MadvocConfig madvocConfig;


	@Override
	public void render(ActionRequest actionRequest, Object resultObject, String resultValue, String resultPath) throws Exception {
		HttpServletResponse response = actionRequest.getHttpServletResponse();

		Object action = actionRequest.getAction();
		if (action instanceof AppAction == false) {
			throw new MadvocException("Not app action!");
		}
		AppAction appAction = (AppAction) action;

		List<Violation> list = appAction.violations();

		String result = VtorUtil.createViolationsJsonString(actionRequest.getHttpServletRequest(), list);
		char[] chars = result.toCharArray();
		byte[] data = CharUtil.toByteArray(chars, madvocConfig.getEncoding());

		OutputStream os = response.getOutputStream();
		os.write(data);
		os.flush();
	}

}
