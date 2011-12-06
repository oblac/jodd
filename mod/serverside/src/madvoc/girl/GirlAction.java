// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc.girl;

import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.Out;
import jodd.madvoc.meta.InterceptedBy;
import jodd.madvoc.meta.Action;
import jodd.madvoc.interceptor.EchoInterceptor;
import jodd.madvoc.interceptor.DefaultWebAppInterceptors;
import jodd.petite.meta.PetiteInject;

import java.util.List;

@MadvocAction
@InterceptedBy({EchoInterceptor.class, DefaultWebAppInterceptors.class})
public class GirlAction {

	@PetiteInject
	GirlManager girlManager;

	@In
	Girl girl;

	@Action
	public String add() {
		System.out.println("GirlAction.add");
		System.out.println(girlManager);
		girlManager.add(girl);
		prepare();
		return "#list.ok";
	}


	@Out
	List<Girl> girls;

	@Action
	public String list() {
		System.out.println("GirlAction.list");
		System.out.println(girlManager);
		prepare();
		return "ok";
	}

	private void prepare() {
		girls = girlManager.getAllGirls();
	}

}
