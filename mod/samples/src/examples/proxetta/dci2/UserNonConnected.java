// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.dci2;

@Role(User.class)
public class UserNonConnected {

	public String getLogin() {
		return Self.<User>get().principal();
	}
}
