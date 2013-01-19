// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.tst;

import jodd.db.oom.meta.DbTable;
import jodd.db.oom.meta.DbColumn;

@DbTable("BOY")
public class Boy2 {

	public Boy2() {
	}

	public Boy2(int id, String name, int girlId) {
		this.id = id;
		this.name = name;
		this.girlId = girlId;
	}

	@DbColumn
	public int id;

	@DbColumn
	public String name;

	@DbColumn
	public int girlId;

	// ---------------------------------------------------------------- special

	public int totalGirls;

	// ---------------------------------------------------------------- joins

	public Girl girl;
	public Girl girlAlt;
}
