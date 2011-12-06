// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.test;

import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;

public class BadGirlBase {

	@DbColumn("SPECIALITY")
	public String foospeciality;

	@DbId("ID")
	public Integer fooid;

}
