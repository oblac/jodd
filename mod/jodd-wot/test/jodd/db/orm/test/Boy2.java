// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.test;

import jodd.db.orm.meta.DbTable;
import jodd.db.orm.meta.DbColumn;

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
