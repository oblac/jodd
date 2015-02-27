// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

import java.util.Calendar;

/**
 * Calendar serializer.
 */
public class CalendarJsonSerializer implements TypeJsonSerializer<Calendar> {

	public void serialize(JsonContext jsonContext, Calendar calendar) {
		jsonContext.write(Long.toString(calendar.getTimeInMillis()));
	}
}