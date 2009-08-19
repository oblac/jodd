// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

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

	private static HashMap<Class, SqlType> types = new HashMap<Class, SqlType>();
	private static HashMap<Class<? extends SqlType>, SqlType> sqlTypes = new HashMap<Class<? extends SqlType>, SqlType>();

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
		register(MutableInteger.class, IntegerSqlType.class);
		
		register(Float.class, FloatSqlType.class);
		register(MutableFloat.class, FloatSqlType.class);

		register(Double.class, DoubleSqlType.class);
		register(MutableDouble.class, DoubleSqlType.class);

		register(Byte.class, ByteSqlType.class);
		register(MutableByte.class, ByteSqlType.class);

		register(Boolean.class, BooleanSqlType.class);
		register(MutableBoolean.class, BooleanSqlType.class);

		register(Long.class, LongSqlType.class);
		register(MutableLong.class, LongSqlType.class);

		register(Short.class, ShortSqlType.class);
		register(MutableShort.class, ShortSqlType.class);

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

	public static void register(Class type, Class<? extends SqlType> sqlTypeClass) {
		types.put(type, lookupSqlType(sqlTypeClass));
	}


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
	 * Returns sql type instance. Instances are stored for better perfromances.
	 */
	public static SqlType lookupSqlType(Class<? extends SqlType> sqlTypeClass) {
		SqlType sqlType = sqlTypes.get(sqlTypeClass);
		if (sqlType == null) {
			try {
				sqlType = sqlTypeClass.newInstance();
			} catch (Exception ex) {
				throw new DbSqlException("Unable to create sql type: " + sqlTypeClass.getSimpleName(), ex);
			}
			sqlTypes.put(sqlTypeClass, sqlType);
		}
		return sqlType;
	}

}