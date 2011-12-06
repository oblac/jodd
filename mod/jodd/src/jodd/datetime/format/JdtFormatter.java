// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime.format;

import jodd.datetime.DateTimeStamp;
import jodd.datetime.JDateTime;

/**
 * Date time formatter performs conversion both from and to string representation of time.
 * 
 * @see AbstractFormatter
 */
public interface JdtFormatter {

	/**
	 * Converts date time to a string using specified format.
	 *
	 * @param jdt       JDateTime to read from
	 * @param format    format
	 *
	 * @return formatted string with date time information
	 */
	String convert(JDateTime jdt, String format);

	/**
	 * Parses string given in specified format and extracts time information.
	 * It returns a new instance of <code>DateTimeStamp</code> or <code>null</code> if error occurs.
	 *
	 * @param value     string containing date time values
	 * @param format    format
	 *
	 * @return DateTimeStamp instance with populated data
	 */
	DateTimeStamp parse(String value, String format);
}
