// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.MadvocAction;

@MadvocAction
public class BookPartialAction {

	@In Book book;

	@Action
	public String hello() {
		return "text:Hi" + book.getIban();
	}
}