// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.test2;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;

@MadvocAction("/re/")
public class ReAction {

	@Action
	public void hello() {
	}

	@Action("user/${id}/[method]")
	public void macro() {
	}

	@Action("user/image/${id}/${fmt}/[method]")
	public void macro2() {
	}

	@Action(value = "users/${id}/[method]", extension = Action.IGNORE, method = "POST")
	public void macro3() {
	}


	@Action("wild${id}cat")
	public void wild1() {
	}
	@Action(value = "wild${id}dog", method = "POST")
	public void wild2() {
	}



	@Action(value = "duplo/${id:^[0-9]+}", extension = Action.IGNORE)
//	@Action(value = "duplo/${id}", extension = Action.NO_EXTENSION)
	public void duplo2() {
	}

	@Action(value = "duplo/${sid}", extension = Action.IGNORE)
	public void duplo1() {
	}

}