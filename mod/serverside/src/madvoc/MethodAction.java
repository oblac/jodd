// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import madvoc.girl.Girl;

public class MethodAction {

	void common() {
		System.out.println("common");
	}


	@MadvocAction
	class ReAction {

		@In Girl girl;
		@In("girl") Girl girl2;

		@Action
		public void view() {
			System.out.println("MethodAction.hello");
			System.out.println("girl = " + girl);
			System.out.println("girl2 = " + girl2);
			common();
		}
	}

	@MadvocAction
	class AttrAction {

		@In int id;
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
