// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.data;

public class LifeBean {

	public String foo = "foo";
	public String _foo = "_foo";

	public String getFoo() {
		return foo;
	}

	public void setFoo(String foo) {
		this.foo = foo;
	}

	public String bar = "bar";
	public String _bar = "_bar";


	private String www = "http";
	public String getWww() {
		return www;
	}
	public void setWww(String www) {
		this.www = www;
	}
}