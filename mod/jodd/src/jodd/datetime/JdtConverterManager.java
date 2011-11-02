// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import jodd.datetime.converter.CalendarConverter;
import jodd.datetime.converter.DateConverter;
import jodd.datetime.converter.DateTimeStampConverter;
import jodd.datetime.converter.GregorianCalendarConverter;
import jodd.datetime.converter.JdtConverter;
import jodd.datetime.converter.SqlDateConverter;
import jodd.datetime.converter.SqlTimeConverter;
import jodd.datetime.converter.SqlTimestampConverter;

import java.sql.Time;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Map;
import java.util.GregorianCalendar;
import java.util.Date;
import java.sql.Timestamp;

/**
 * Manager for {@link JdtConverter} instances.
 * @see #registerDefaults()
 */
public class JdtConverterManager {

	// ---------------------------------------------------------------- manager

	private static Map<Class, JdtConverter> converters = new HashMap<Class, JdtConverter>();

    static {
		registerDefaults();
    }

	/**
	 * Unregister all converters.
	 */
	public static void unregisterAll() {
		converters.clear();
	}


	/**
	 * Registers default set of converters.
	 * @see #register(Class, jodd.datetime.converter.JdtConverter)
	 */
	public static void registerDefaults() {
		register(DateTimeStamp.class, new DateTimeStampConverter());
		register(Calendar.class, new CalendarConverter());
		register(GregorianCalendar.class, new GregorianCalendarConverter());
		register(Date.class, new DateConverter());
		register(java.sql.Date.class, new SqlDateConverter());
		register(Time.class, new SqlTimeConverter());
		register(Timestamp.class, new SqlTimestampConverter());
	}

	/**
	 * Registers converter for an objects of specific type.

	 * @see #registerDefaults() 
	 */
	public static <T> void register(Class<T> type, JdtConverter<T> converter) {
		converters.put(type, converter);
	}

	public static void unregister(Class type) {
		converters.remove(type);
	}


	// ---------------------------------------------------------------- lookup


	/**
	 * Returns converter for the specific object type.
	 * @return converter instance, <code>null</code> if nothing found.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> JdtConverter<T> lookup(Class<T> type) {
		return converters.get(type);
	}

	/**
	 * Performs more thoroughly search for converter loader. It examines all available
	 * converters and returns the first that matches the object type.
	 */
	public static JdtConverter lookup(Object source) {
		JdtConverter converter = lookup(source.getClass());
		if (converter == null) {					// class not found, scan for instanceof matching
			for (Class key : converters.keySet()) {
				if (key.isInstance(source)) {
					//noinspection unchecked
					converter = lookup(key);
					break;
				}
			}
		}
		return converter;
	}

}
