// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.test;

import jodd.db.orm.meta.DbColumn;
import jodd.db.orm.meta.DbId;

public class BadGirlBase {

	@DbColumn("SPECIALITY")
	public String foospeciality;

	@DbId("ID")
	public Integer fooid;

}
