package jodd.madvoc.action;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;

@MadvocAction
public class AsyncAction {

	@Out
	String task;

	@Action(async = true)
	public void view() {
		task = "TASK";
	}

}