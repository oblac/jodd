// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime.format;

import jodd.datetime.JDateTime;
import jodd.datetime.DateTimeStamp;

/**
 * Immutable format-formatter pair.
 */
public class JdtFormat {

	protected final String format;
	protected final JdtFormatter formatter;

	public JdtFormat(JdtFormatter formatter, String format) {
		this.format = format;
		this.formatter = formatter;
	}

	/**
	 * Returns format.
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Returns formatter.
	 */
	public JdtFormatter getFormatter() {
		return formatter;
	}


	/**
	 * Delegates for {@link jodd.datetime.format.JdtFormatter#convert(jodd.datetime.JDateTime, String)}. 
	 */
	public String convert(JDateTime jdt) {
		return formatter.convert(jdt, format);
	}

	/**
	 * Delegates for {@link jodd.datetime.format.JdtFormatter#parse(String, String)}.
	 */
	public DateTimeStamp parse(String value) {
		return formatter.parse(value, format);
	}
}
