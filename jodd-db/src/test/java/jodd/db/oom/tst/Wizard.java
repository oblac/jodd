// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.tst;

import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;
import jodd.db.oom.meta.DbMapTo;
import jodd.db.oom.meta.DbTable;

@DbTable
public class Wizard {

	@DbId
	public long wizardId;
	@DbColumn
	public int level;

	// ---------------------------------------------------------------- user

	public User user;

	public String getName() {
		return user.name;
	}

}