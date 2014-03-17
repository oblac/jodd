// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;

import java.util.List;

@MadvocAction
public class TypesAction {

	@In
	List<Integer> listA;

	@Out
	String result;

	@Action
	public void one() {
		result = listA.toString();
	}

}