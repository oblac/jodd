package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

import java.util.Calendar;

/**
 * Calendar serializer.
 */
public class CalendarJsonSerializer implements TypeJsonSerializer<Calendar> {

	@Override
	public void serialize(JsonContext jsonContext, Calendar calendar) {
		jsonContext.write(Long.toString(calendar.getTimeInMillis()));
	}
}