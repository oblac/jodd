// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector.test;

public class Bbean extends Abean {
	private Long boo;

	Long getBoo() {
		return boo;
	}

	void setBoo(Long boo) {
		this.boo = boo;
	}
}
