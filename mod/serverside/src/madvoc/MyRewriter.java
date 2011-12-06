// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.component.ActionPathRewriter;
import jodd.util.StringUtil;

import javax.servlet.http.HttpServletRequest;

public class MyRewriter extends ActionPathRewriter {

	@Override
	public String rewrite(HttpServletRequest servletRequest, String actionPath, String httpMethod) {
		return StringUtil.remove(actionPath, '_');
	}

}
