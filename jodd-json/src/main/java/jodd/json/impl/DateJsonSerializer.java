package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

import java.util.Date;

/**
 * Date serializer.
 */
public class DateJsonSerializer implements TypeJsonSerializer<Date> {

	@Override
	public void serialize(JsonContext jsonContext, Date date) {
		jsonContext.write(Long.toString(date.getTime()));
	}
}