// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package madvoc.girl;

public class Girl {

	private int id;

	private String name;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Override
	public String toString() {
		return "Girl{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
