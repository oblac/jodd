// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.model;

public class LoopClassOne {

	private LoopClassTwo loopClassTwo;

	public LoopClassTwo getLoopClassTwo() {
		return loopClassTwo;
	}

	public void setLoopClassTwo(LoopClassTwo loopClassTwo) {
		this.loopClassTwo = loopClassTwo;
	}
}
