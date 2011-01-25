// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.component.ActionPathRewriter;
import jodd.util.StringUtil;

import javax.servlet.http.HttpServletRequest;

public class MyRewriter extends ActionPathRewriter {

	@Override
	public String rewrite(String actionPath, HttpServletRequest servletRequest, String httpMethod) {
		return StringUtil.remove(actionPath, '_');
	}

}
