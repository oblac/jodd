// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.data;

public class YBean {

	private String foo;

	public String getFoo() {
		return foo;
	}

	public void setFoo(String foo) {
		this.foo = foo;
	}

	public String getPpublic() {
		return "public";
	}

	protected String getPprotected() {
		return "protected";
	}

	String getPpackage() {
		return "package";
	}

	private String getPprivate() {
		return "private";
	}
}
