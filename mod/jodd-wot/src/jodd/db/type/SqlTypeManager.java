// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.util.HashMap;

/**
 * Provides dynamic object conversion to a type.
 * Contains a map of registered converters. User may add new converter.
 */
public class SqlTypeManager {

	private static HashMap<Class, SqlType> types = new HashMap<Class, SqlType>();

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
	 * Registers default set of converters.
	 */
	public static void registerDefaults() {
		register(new IntegerSqlType());
		register(new FloatSqlType());
		register(new DoubleSqlType());
		register(new ByteSqlType());
		register(new BooleanSqlType());
		register(new StringSqlType());
		register(new LongSqlType());
		register(new ShortSqlType());
		register(new MutableIntegerSqlType());
		register(new MutableBooleanSqlType());
		register(new MutableByteSqlType());
		register(new MutableDoubleSqlType());
		register(new MutableFloatSqlType());
		register(new MutableLongSqlType());
		register(new MutableShortSqlType());
		register(new SqlDateSqlType());
		register(new TimestampSqlType());
		register(new TimeSqlType());
		register(new DateSqlType());
		register(new BigDecimalSqlType());
		register(new BigIntegerSqlType());
		register(new ByteArraySqlType());
		register(new URLSqlType());
		register(new BlobSqlType());
		register(new ClobSqlType());
		register(new SqlArraySqlType());
		register(new SqlRefSqlType());
	}

	/**
	 * Registers SQL type.
	 */
	public static void register(SqlType type) {
		types.put(type.getSqlType(), type);
	}

//	public static void register(Class type, SqlType sqlType) {
//		types.put(type, sqlType);
//	}


	public static void unregister(Class type) {
		types.remove(type);
	}

	// ---------------------------------------------------------------- lookup

	/**
	 * Retrieves SQL type for provided type.
	 */
	public static SqlType lookup(Class clazz) {
		return types.get(clazz);
	}

}