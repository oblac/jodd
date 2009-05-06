// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.data;

public class Cbean {

	private Bbean bbean = new Bbean();

	public void setBbean(Bbean v) {
		bbean = v;
	}

	public Bbean getBbean() {
		return bbean;
	}

}
