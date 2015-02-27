// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;

@MadvocAction
public class OneRedirectAction {

	String value;

	@Action
	public String execute() {
		value = "333";
		return "redirect:/<two>?value=${value}";
	}

	@Action
	public String perm() {
		value = "444";
		return "url:/<two>?value=${value}";
	}

}