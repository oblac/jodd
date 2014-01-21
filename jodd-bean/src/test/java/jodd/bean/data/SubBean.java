// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.data;

public class SubBean extends SupBean {

	public void setV1(String v1) {
		this.v1 = v1;
	}

	public String getV2() {
		return v2 + "sub";
	}

}