// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.tst;

import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;
import jodd.db.oom.meta.DbTable;

@DbTable
public class User {
	@DbId
	public long userId;

	@DbColumn
	public String name;

	public int exp;
}