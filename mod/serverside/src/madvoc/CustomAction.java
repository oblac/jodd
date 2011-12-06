// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.InterceptedBy;

/**
 * Custom paths.
 */
@MadvocAction("foo/boo")
@InterceptedBy(MyInterceptorStack.class)
public class CustomAction {

	/**
	 * Action mapped to '/foo/hello'
	 * Result mapped to '/foo/hello.ok.jsp'
	 */
	@Action(value = "/foo/hello", extension = "xxxxxxxxx")
	public String execute() {
		return "ok";
	}

	/**
	 * Action mapped to '/foo/boo.zoo/again.exec.html'
	 * Result mapped to '/foo/boo.zoo/again.exec.jsp'
	 */
	@Action("zoo/again.exec")
	public void again() {
		System.out.println("CustomAction.again");
	}

	/**
	 * Action mapped to '/foo/boo-aa/temp.bb'
	 * Result mapped to '/foo/boo-aa/temp.bb.jsp'
	 */
	@Action("/foo/boo-aa/temp.bb")
	public void temp() {
	}

}
