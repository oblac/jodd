// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.In;

import java.util.List;
import java.util.Map;

@MadvocAction
public class DefaultAction {

	@In("ppp")
	List<Person> plist;

	@In("ppp")
	Person[] parray;

	@In("ppp")
	Map<String, Person> pmap;
	

	@Action
	public String view() {
		System.out.println("default action name");
		if (plist == null) {
			System.out.println("-");
		} else {
			for (int i = 0; i < plist.size(); i++) {
				System.out.println(i + " " + plist.get(i));
			}
		}

		if (parray == null) {
			System.out.println("-");
		} else {
			for (int i = 0; i < parray.length; i++) {
				System.out.println(i + " " + parray[i]);
			}
		}

		if (pmap == null) {
			System.out.println("-");
		} else {
			System.out.println(pmap);
		}

		return "ok";
	}

}
