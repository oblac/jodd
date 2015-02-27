// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector.tst;

public class Bbean extends Abean {

	public static final long serialVersionUID = 42L;

	private Long boo;

	Long getBoo() {
		return boo;
	}

	void setBoo(Long boo) {
		this.boo = boo;
	}
}
