// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.action.ListMadvocConfig;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;

@MadvocAction
public class MadvocInfoAction extends ListMadvocConfig {

	@Action
	public void view() {
		super.collectActionConfigs();
		super.collectActionInterceptors();
		super.collectActionResults();
	}
}
