// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.test;

import jodd.db.orm.meta.DbColumn;
import jodd.db.orm.meta.DbId;
import jodd.db.orm.meta.DbTable;

@DbTable("BOY")
public class Boy3 {

	@DbId
	public Integer id;

	@DbColumn
	public String name;

	@DbColumn
	public Integer girlId;
}
