// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector.test;

public class Abean {

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
