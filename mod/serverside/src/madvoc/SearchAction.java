// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.InOut;

@MadvocAction
public class SearchAction {

	@InOut
	String query;

	@Action(extension = Action.NO_EXTENSION)
	public void execute() {
		System.out.println("SearchAction.execute");
		System.out.println("query = " + query);
	}

}
