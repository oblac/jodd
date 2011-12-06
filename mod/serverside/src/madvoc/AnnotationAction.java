// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;
import madvoc.meta.CustomUserAction;

@MadvocAction
public class AnnotationAction {

	@Action
	public void action() {
	}

	@CustomUserAction
	public void custom() {
	}
}
