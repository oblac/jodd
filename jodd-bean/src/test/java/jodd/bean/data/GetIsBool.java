// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.data;

public class GetIsBool {

	private boolean flag = true;

	public boolean getFlag() {
		System.out.println("IsGetBool.getFlag");
		return false;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public boolean isFlag() {
		System.out.println("IsGetBool.isFlag");
		return true;
	}
}
