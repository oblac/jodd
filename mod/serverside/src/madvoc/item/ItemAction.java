// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc.item;

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
public class ItemAction {

	@PetiteInject
	ItemManager itemManager;

	@In
	Item item;

	@Action
	public String add() {
		System.out.println("ItemAction.add");
		System.out.println(itemManager);
		itemManager.add(item);
		prepare();
		return "#list.ok";
	}


	@Out
	List<Item> items;

	@Action
	public String list() {
		System.out.println("ItemAction.list");
		System.out.println(itemManager);
		prepare();
		return "ok";
	}

	private void prepare() {
		items = itemManager.getAllItems();
	}

}
