// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.db.oom.fixtures;

import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;
import jodd.db.oom.meta.DbTable;
import jodd.db.type.IntegerSqlType;
import jodd.mutable.MutableInteger;
import jodd.time.JulianDate;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
	public LocalDateTime timestamp2;

	@DbColumn
	public Clob clob;

	@DbColumn
	public Blob blob;

	@DbColumn
	public Double decimal;

	@DbColumn
	public BigDecimal decimal2;

	@DbColumn
	public JulianDate jdt1;

	@DbColumn
	public JulianDate jdt2;

}


