// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

import java.util.Date;

/**
 * Date serializer.
 */
public class DateJsonSerializer implements TypeJsonSerializer<Date> {

	public void serialize(JsonContext jsonContext, Date date) {
		jsonContext.write(Long.toString(date.getTime()));
	}
}