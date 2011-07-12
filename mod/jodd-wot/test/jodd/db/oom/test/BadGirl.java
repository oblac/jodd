// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.test;

import jodd.db.oom.meta.DbTable;
import jodd.db.oom.meta.DbColumn;

import java.util.List;

@DbTable("GIRL")
public class BadGirl extends BadGirlBase {

	public BadGirl() {
	}

	public BadGirl(Integer id, String name, String speciality) {
		this.fooid = id;
		this.fooname = name;
		this.foospeciality = speciality;
	}

	@DbColumn("NAME")
	public String fooname;

	public List<BadBoy> boys;

	// ---------------------------------------------------------------- toString

	@Override
	public String toString() {
		return "Girl{" +
				"id=" + fooid +
				", name='" + fooname + '\'' +
				", speciality='" + foospeciality + '\'' +
				'}';
	}
}
