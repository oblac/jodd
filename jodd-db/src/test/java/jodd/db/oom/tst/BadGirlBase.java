// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.tst;

import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;

public class BadGirlBase {

	@DbColumn("SPECIALITY")
	public String foospeciality;

	@DbId("ID")
	public Integer fooid;

}
