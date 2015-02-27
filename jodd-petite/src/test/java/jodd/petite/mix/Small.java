// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.mix;

public class Small {

	public static int instanceCounter;

	private final int id;

	public Small() {
		id = ++instanceCounter;
	}

	public String name() {
		return "small " + id;
	}

}
