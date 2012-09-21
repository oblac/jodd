// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.tst;

import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;
import jodd.db.oom.meta.DbTable;

@DbTable("BOY")
public class Boy3 {

	@DbId
	public Integer id;

	@DbColumn
	public String name;

	@DbColumn
	public Integer girlId;
}
