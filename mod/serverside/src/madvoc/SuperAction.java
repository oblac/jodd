// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.InOut;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.Action;

@MadvocAction
public class SuperAction {

	@InOut
	int id;

	void common() {
		System.out.println("common " + id);
		id++;
	}


	public static class SuperSimpleAction extends SuperAction {
	
		@In("id") int ajdi;

		@Action
		public void simple() {
			System.out.println("MethodAction.hello");
			System.out.println("id = " + id);
			System.out.println("ajdi = " + ajdi);
			common();
		}

	}


}
