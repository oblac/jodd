// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.servlet.DispatcherUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Simply sends permanent redirection to an external location.
 */
public class ServletUrlRedirectResult extends ServletRedirectResult {

	public static final String NAME = "url";

	public ServletUrlRedirectResult() {
		super(NAME);
	}

	@Override
	protected void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
		DispatcherUtil.redirectPermanent(request, response, path);
	}
}