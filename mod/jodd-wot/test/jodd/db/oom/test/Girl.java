// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.test;

public class Girl {

	public Girl() {
	}

	public Girl(int id, String name, String speciality) {
		this.id = id;
		this.name = name;
		this.speciality = speciality;
	}

	public String speciality;
	public int id;
	public String name;

	// ---------------------------------------------------------------- toString

	@Override
	public String toString() {
		return "Girl{" +
				"id=" + id +
				", name='" + name + '\'' +
				", speciality='" + speciality + '\'' +
				'}';
	}
}
