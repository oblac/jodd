// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.AppendingFilter;
import jodd.madvoc.interceptor.DefaultWebAppInterceptors;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.FilteredBy;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.meta.MadvocAction;

@MadvocAction
@FilteredBy({AppendingFilter.class, AppendingFilter.class})
@InterceptedBy(DefaultWebAppInterceptors.class)
public class FilterAction {

	@Action
	public void view() {
	}
}