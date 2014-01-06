// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.tst2;

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

	@Action(value = "users/${id}/[method]", extension = Action.NONE, method = "POST")
	public void macro3() {
	}


	@Action("wild${id}cat")
	public void wild1() {
	}
	@Action(value = "wild${id}dog", method = "POST")
	public void wild2() {
	}



	@Action(value = "duplo/${id:^[0-9]+}", extension = Action.NONE)
//	@Action(value = "duplo/${id}", extension = Action.NO_EXTENSION)
	public void duplo2() {
	}

	@Action(value = "duplo/${sid}", extension = Action.NONE)
	public void duplo1() {
	}


	// ---------------------------------------------------------------- zqq #30

	String entityName;

	@Action(value = "/${entityName}/dba.delete", extension = "do")
	public void zqq1() {}

	@Action(value = "/${entityName}/dba.delete_multi", extension = "do")
	public void zqq2() {}

}