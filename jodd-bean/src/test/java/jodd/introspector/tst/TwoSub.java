// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector.tst;

public class TwoSub extends One {

	@Override
	public int getFtwo() {
		return ftwo;
	}

	public String getFone() {
		return super.getFone();
	}
}