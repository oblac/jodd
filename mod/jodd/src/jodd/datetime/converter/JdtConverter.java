// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime.converter;

import jodd.datetime.JDateTime;

/**
 * Interface for conversion from and to JDateTime.
 */
public interface JdtConverter<T> {

	// ---------------------------------------------------------------- load

	/**
	 * Loads date time information <b>from</b> object to provided <code>JDateTime</code> instance.
	 */
	void loadFrom(JDateTime jdt, T source);


	// ---------------------------------------------------------------- store

	/**
	 * Creates a new destination instance of specified class and stores date time
	 * information. After creating the instance, it usually calls {@link #storeTo(jodd.datetime.JDateTime, Object)}.
	 * @see #storeTo(jodd.datetime.JDateTime, Object) 
	 */
	T convertTo(JDateTime jdt);


	/**
	 * Stores date time information <b>to</b> destination object from provided <code>JDateTime</code> instance.
	 * @see #convertTo(jodd.datetime.JDateTime) 
	 */
	void storeTo(JDateTime jdt, T destination);

}
