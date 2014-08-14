// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.datetime.JDateTime;
import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * Serializer for JDateTime.
 */
public class JDateTimeSerializer implements TypeJsonSerializer<JDateTime> {

	public void serialize(JsonContext jsonContext, JDateTime value) {
		jsonContext.write(String.valueOf(value.getTimeInMillis()));
	}
}