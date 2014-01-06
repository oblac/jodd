// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.data;

public enum Status {

	IDLE(0),
	STARTED(1),
	TERMINATED(2);

	private final int value;
	private Status(int value) {
		this.value = value;
	}

}