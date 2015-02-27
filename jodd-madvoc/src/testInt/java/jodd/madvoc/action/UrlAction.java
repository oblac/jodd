// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;


@MadvocAction("foo/boo")
public class UrlAction {

	/**
	 * Action mapped to '/foo/hello'
	 * Result mapped to '/foo/hello.ok.jsp'
	 */
	@Action(value = "/foo/hello", extension = "notused")
	public String one() {
		return "ok";
	}

	/**
	 * Action mapped to '/foo/boo.zoo/two.exec.html'
	 * Result mapped to '/foo/boo.zoo/two.exec.jsp'
	 */
	@Action("zoo/two.exec")
	public void two() {
	}


}