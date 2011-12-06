// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.data;

public class XBean {
	private YBean y;
	public YBean getY() {
		return y;
	}
	public void setY(YBean y) {
		this.y = y;
	}

	private YBean[] yy = new YBean[10];
	public YBean[] getYy() {
		return yy;
	}
	void setYy(YBean[] yy) {
		this.yy = yy;
	}

}
