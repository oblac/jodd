// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.AppendingFilter;
import jodd.madvoc.interceptor.DefaultWebAppInterceptors;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.meta.MadvocAction;

@MadvocAction
@InterceptedBy({AppendingFilter.class, AppendingFilter.class, DefaultWebAppInterceptors.class})
public class FilterAction {

	@Action
	public void view() {
	}
}