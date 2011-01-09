// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.test;

import jodd.db.orm.meta.DbTable;
import jodd.db.orm.meta.DbColumn;

@DbTable("GIRL")
public class IdName {

	@DbColumn
	public int id;

	@DbColumn
	public String name;
}
