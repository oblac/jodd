// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.test;

import jodd.db.oom.meta.DbTable;
import jodd.db.oom.meta.DbColumn;

@DbTable("BOY")
public class Boy2 {

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
}
