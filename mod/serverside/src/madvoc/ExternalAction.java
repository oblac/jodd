package madvoc;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;

@MadvocAction
public class ExternalAction {

	@Action
	public String execute() {
		System.out.println("redirect to external link!");
		return "url:http://jodd.org";
	}

}
