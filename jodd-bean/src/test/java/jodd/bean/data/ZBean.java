// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.data;

public class ZBean extends YBean {

	public String getTpublic() {
		return "public";
	}

	protected String getTprotected() {
		return "protected";
	}

	String getTpackage() {
		return "package";
	}

	private String getTprivate() {
		return "private";
	}

	
}
