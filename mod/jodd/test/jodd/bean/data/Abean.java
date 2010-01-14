// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.data;

import java.util.HashMap;

public class Abean extends HashMap {

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

	public void put(String s, Integer o) {
		super.put(s, o);
	}
	public Integer get(String s) {
		return (Integer) super.get(s);
	}
}
