// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.tst3;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;

@MadvocAction("/my-[package]/[class]")
public class JimAction {

	@Action("my-[method]")
	public void hello() {}

}