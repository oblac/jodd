// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.dci2;

public class UserImpl implements User {

	public String principal() {
		return "jodd";
	}

	public String password() {
		return "secret";
	}
}
