// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.test;

import jodd.db.orm.meta.DbTable;
import jodd.db.orm.meta.DbId;
import jodd.db.orm.meta.DbColumn;
import jodd.db.type.IntegerSqlType;
import jodd.db.type.ClobSqlType;
import jodd.db.type.BlobSqlType;
import jodd.db.type.DoubleSqlType;
import jodd.mutable.MutableFloat;
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


