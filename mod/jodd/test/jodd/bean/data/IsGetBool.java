// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.data;

public class IsGetBool {

	private boolean flag = true;

	public boolean isFlag() {
		System.out.println("IsGetBool.isFlag");
		return true;
	}

	public boolean getFlag() {
		System.out.println("IsGetBool.getFlag");
		return false;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
}
