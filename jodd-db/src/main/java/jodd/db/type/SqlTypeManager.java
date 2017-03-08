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

package jodd.db.type;

import jodd.mutable.MutableInteger;
import jodd.mutable.MutableFloat;
import jodd.mutable.MutableDouble;
import jodd.mutable.MutableByte;
import jodd.mutable.MutableBoolean;
import jodd.mutable.MutableLong;
import jodd.mutable.MutableShort;
import jodd.db.DbSqlException;
import jodd.datetime.JDateTime;

import java.util.HashMap;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Time;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Array;
import java.sql.Ref;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;

/**
 * Provides dynamic object conversion to a type.
 * Contains a map of registered converters. User may add new converter.
 */
public class SqlTypeManager {

	private static HashMap<Class, SqlType> types = new HashMap<>();
	private static HashMap<Class<? extends SqlType>, SqlType> sqlTypes = new HashMap<>();

	static {
		registerDefaults();
	}

	/**
	 * Unregisters all converters.
	 */
	public static void unregisterAll() {
		types.clear();
	}

	/**
	 * Registers default set of SQL types.
	 */
	public static void registerDefaults() {
		register(Integer.class, IntegerSqlType.class);
		register(int.class, IntegerSqlType.class);
		register(MutableInteger.class, IntegerSqlType.class);
		
		register(Float.class, FloatSqlType.class);
		register(float.class, FloatSqlType.class);
		register(MutableFloat.class, FloatSqlType.class);

		register(Double.class, DoubleSqlType.class);
		register(double.class, DoubleSqlType.class);
		register(MutableDouble.class, DoubleSqlType.class);

		register(Byte.class, ByteSqlType.class);
		register(byte.class, ByteSqlType.class);
		register(MutableByte.class, ByteSqlType.class);

		register(Boolean.class, BooleanSqlType.class);
		register(boolean.class, BooleanSqlType.class);
		register(MutableBoolean.class, BooleanSqlType.class);

		register(Long.class, LongSqlType.class);
		register(long.class, LongSqlType.class);
		register(MutableLong.class, LongSqlType.class);

		register(Short.class, ShortSqlType.class);
		register(short.class, ShortSqlType.class);
		register(MutableShort.class, ShortSqlType.class);

		register(Character.class, CharacterSqlType.class);
		register(char.class, CharacterSqlType.class);

		register(BigDecimal.class, BigDecimalSqlType.class);
		register(BigInteger.class, BigIntegerSqlType.class);

		register(String.class, StringSqlType.class);

		register(Date.class, SqlDateSqlType.class);
		register(Timestamp.class, TimestampSqlType.class);
		register(Time.class, TimeSqlType.class);
		register(java.util.Date.class, DateSqlType.class);
		register(JDateTime.class, JDateTimeSqlType.class);

		register(byte[].class, ByteArraySqlType.class);
		register(URL.class, URLSqlType.class);

		register(Blob.class, BlobSqlType.class);
		register(Clob.class, ClobSqlType.class);
		register(Array.class, SqlArraySqlType.class);
		register(Ref.class, SqlRefSqlType.class);
	}

	/**
	 * Registers sql type for provided type.
	 */
	public static void register(Class type, Class<? extends SqlType> sqlTypeClass) {
		types.put(type, lookupSqlType(sqlTypeClass));
	}

	/**
	 * Unregisters some sql type.
	 */
	public static void unregister(Class type) {
		types.remove(type);
	}

	// ---------------------------------------------------------------- lookup

	/**
	 * Retrieves SQL type for provided type. All subclasses and interfaces are examined
	 * for matching sql type.
	 */
	public static SqlType lookup(Class clazz) {
		SqlType sqlType;
		for (Class x = clazz; x != null; x = x.getSuperclass()) {
			sqlType = types.get(clazz);
			if (sqlType != null) {
				return sqlType;
			}
			Class[] interfaces = x.getInterfaces();
			for (Class i : interfaces) {
				sqlType = types.get(i);
				if (sqlType != null) {
					return sqlType;
				}
			}
		}
		return null;
	}

	/**
	 * Returns sql type instance. Instances are stored for better performances.
	 */
	public static SqlType lookupSqlType(Class<? extends SqlType> sqlTypeClass) {
		SqlType sqlType = sqlTypes.get(sqlTypeClass);
		if (sqlType == null) {
			try {
				sqlType = sqlTypeClass.newInstance();
			} catch (Exception ex) {
				throw new DbSqlException("SQL type not found: " + sqlTypeClass.getSimpleName(), ex);
			}
			sqlTypes.put(sqlTypeClass, sqlType);
		}
		return sqlType;
	}

}