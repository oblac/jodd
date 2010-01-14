// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.test;

import jodd.db.orm.meta.DbTable;
import jodd.db.orm.meta.DbColumn;
import jodd.db.orm.meta.DbId;

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
