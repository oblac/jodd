// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.ScopeType;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.MadvocAction;

@MadvocAction
public class MissingAction {

	public static class Data {
		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	@In(scope = ScopeType.REQUEST)
	final Data data = new Data();

	@Action
	public String view() {
		return "text::" + data.value;
	}
}