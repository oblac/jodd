// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.action.DefaultActionSupplement;

public class MyMadvocConfig extends MadvocConfig {

	public MyMadvocConfig() {
//		supplementAction = DefaultActionSupplement.class;		 if this is tuner on, then index.html has to be renamed to index.jsp
		actionPathMappingEnabled = true;
		setRootPackageOf(HelloAction.class);
	}

	protected String myparam;

	public String getMyparam() {
		return myparam;
	}

	public void setMyparam(String myparam) {
		this.myparam = myparam;
	}


	
}
