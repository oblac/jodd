// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.dci2;

public class RunMe {

	public static void main(String[] args) {
		UserNonConnected role = FrameworkManager.bind(UserNonConnected.class, new UserImpl());
		System.out.println(role.getLogin());

		role = FrameworkManager.bind(UserNonConnected.class, new UserImpl());
		System.out.println(role.getLogin());
	}
}
