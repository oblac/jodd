// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.tst4;

public class Foo2 {

	public String data;

	public Bar bar;

	void init1() {
		result = "1 " + bar + ' ' + data;
	}
	void init2() {
		result += " 2 " + bar + ' ' + data;
	}
	void init3() {
		result += " 3 " + bar + ' ' + data;
	}

	public String result;

}