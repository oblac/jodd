// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.tst4;

public class Foo {
	public String data;

	public Bar bar;

	void init() {
		result = "ctor " + bar + ' ' + data;
	}

	public String result;
}