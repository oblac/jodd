// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.test;

import jodd.db.oom.meta.DbTable;
import jodd.db.oom.meta.DbId;
import jodd.db.oom.meta.DbColumn;
import jodd.db.type.IntegerSqlType;
import jodd.db.type.TimestampSqlType;
import jodd.mutable.MutableInteger;
import jodd.datetime.JDateTime;

import java.sql.Timestamp;
import java.sql.Clob;
import java.sql.Blob;
import java.math.BigDecimal;

@DbTable
public class Foo {

	@DbId
	public long id;

	@DbColumn
	public MutableInteger number;

	@DbColumn(sqlType = IntegerSqlType.class)
	public String string;

	@DbColumn
	public String string2;

	@DbColumn
	public Boo boo;

	@DbColumn
	public FooColor color;

	@DbColumn(sqlType = FooWeigthSqlType.class)
	public FooWeight weight;

	@DbColumn
	public Timestamp timestamp;

	@DbColumn
	public JDateTime timestamp2;

	@DbColumn
	public Clob clob;

	@DbColumn
	public Blob blob;

	@DbColumn
	public BigDecimal decimal;

	@DbColumn
	public BigDecimal decimal2;

	@DbColumn
	public JDateTime jdt1;

	@DbColumn
	public JDateTime jdt2;

}


