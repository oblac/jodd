// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector.tst;

public class Abean {

	protected Integer shared;

	private String fooProp = "abean_value";
	public void setFooProp(String v) {
		fooProp = v;
	}
	public String getFooProp() {
		return fooProp;
	}

	public boolean isSomething() {
		return true;
	}
}
