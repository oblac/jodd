// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.test;

import jodd.db.oom.meta.DbTable;
import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;

@DbTable("BOY")
public class BadBoy {

	public BadBoy() {}

	public BadBoy(Integer id, String name, Integer girlId) {
		this.ajdi = id;
		this.nejm = name;
		this.girlId = girlId;
	}

	@DbId("ID")
	public Integer ajdi;

	@DbColumn("NAME")
	public String nejm;

	@DbColumn
	public Integer girlId;

	public Girl girl;


	@Override
	public String toString() {
		return "BadBoy{" +
				"ajdi=" + ajdi +
				", nejm='" + nejm + '\'' +
				", girlId=" + girlId +
				'}';
	}
}
