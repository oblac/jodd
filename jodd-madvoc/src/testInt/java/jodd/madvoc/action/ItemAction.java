// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.ScopeType;
import jodd.madvoc.beans.GlobalService;
import jodd.madvoc.beans.SessionBean;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.MadvocAction;
import jodd.petite.meta.PetiteInject;

import javax.servlet.http.HttpSession;

@MadvocAction
public class ItemAction {

	@PetiteInject
	SessionBean sessionBean;

	@PetiteInject
	GlobalService globalService;

	@In(scope = ScopeType.SERVLET)
	HttpSession httpSession;

	@Action
	public String view() {
		return "text:" + sessionBean.toString() + " sid:" + httpSession.getId();
	}

	@Action
	public String global() {
		return "text:" + globalService.toString() + " " + globalService.getSessionBean().toString() + " sid:" + httpSession.getId();
	}
}