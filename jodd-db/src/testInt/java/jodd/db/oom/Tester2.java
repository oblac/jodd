// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.datetime.JDateTime;
import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;
import jodd.db.oom.meta.DbTable;

import java.sql.Timestamp;

@DbTable
public class Tester2 {
	@DbId
	public long id;
	@DbColumn
	public String name;
	@DbColumn
	public Integer value;
	@DbColumn
	public Timestamp time;
	@DbColumn
	public JDateTime time2;
}