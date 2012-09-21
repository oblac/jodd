// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.tst;

public enum FooWeight {
	light(1),
	medium(10),
	heavy(77);

	final int value;
	FooWeight(int value) {
		this.value = value;
	}

	public static FooWeight valueOf(int value) {
		switch (value) {
			case 1: return light;
			case 10: return medium;
			case 77: return heavy;
		}
		throw new IllegalArgumentException("Invalid enum value: " + value);
	}

	public int getValue() {
		return value;
	}
}

