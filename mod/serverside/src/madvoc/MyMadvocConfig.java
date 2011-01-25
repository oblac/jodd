// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.action.DefaultActionSupplement;
import jodd.madvoc.meta.Action;
import madvoc.meta.CustomUserAction;

import java.lang.annotation.Annotation;

public class MyMadvocConfig extends MadvocConfig {

	@SuppressWarnings( {"unchecked"})
	public MyMadvocConfig() {
		supplementAction = DefaultActionSupplement.class;
		actionPathMappingEnabled = true;
		setRootPackageOf(HelloAction.class);
		this.actionAnnotations = new Class[] {Action.class, CustomUserAction.class};
	}

	protected String myparam;

	public String getMyparam() {
		return myparam;
	}

	public void setMyparam(String myparam) {
		this.myparam = myparam;
	}


	
}
