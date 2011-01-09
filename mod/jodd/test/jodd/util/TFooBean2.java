// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

public class TFooBean2 extends TFooBean implements Comparable {

	private static final long serialVersionUID = 3688789150522291763L;

	public int compareTo(Object o) {
		//TFooBean2 sees also:
		//this.getDefault();
		//this.getProtected();
		//this.getPublic();
		return 0;
	}
}
