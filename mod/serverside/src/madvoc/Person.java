// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.mutable.MutableInteger;

public class Person {

	private String name;
	private MutableInteger data;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MutableInteger getData() {
		return data;
	}

	public void setData(MutableInteger data) {
		this.data = data;
	}


	@Override
	public String toString() {
		return "Person{" +
				"name='" + name + '\'' +
				", data=" + data +
				'}';
	}
}
