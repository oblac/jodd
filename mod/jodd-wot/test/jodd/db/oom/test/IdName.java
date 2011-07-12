// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.test;

import jodd.db.oom.meta.DbTable;
import jodd.db.oom.meta.DbColumn;

@DbTable("GIRL")
public class IdName {

	@DbColumn
	public int id;

	@DbColumn
	public String name;
}
