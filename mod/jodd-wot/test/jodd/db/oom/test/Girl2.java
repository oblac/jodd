// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.test;

import jodd.db.oom.meta.DbTable;
import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;

import java.sql.Timestamp;

@DbTable("GIRL")
public class Girl2 {

	public Girl2() {
	}

	public Girl2(int id, String name, String speciality) {
		this.id = Integer.valueOf(id);
		this.name = name;
		this.speciality = speciality;
	}

	public Girl2(String name) {
		this.name = name;
	}

	@DbId
	public Integer id;

	@DbColumn
	public String speciality;

	@DbColumn
	public String name;

	@DbColumn
	public Timestamp time;

}
