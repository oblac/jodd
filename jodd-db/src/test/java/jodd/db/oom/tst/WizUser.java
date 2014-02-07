// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.tst;

import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;
import jodd.db.oom.meta.DbMapTo;
import jodd.db.oom.meta.DbTable;

@DbTable
@DbMapTo({Wizard.class, User.class})
public class WizUser {

	@DbColumn
	public long userId;

//	@DbColumn
//	public long wizardId;

	@DbColumn
	public int level;

	@DbColumn
	public String name;

}