// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.BeanSerializer;
import jodd.json.JsonContext;

/**
 * Main serializer for objects. It is also the last serializer, used when
 * no other serializer is found.
 */
public class ObjectJsonSerializer extends ValueJsonSerializer<Object> {

	public void serializeValue(final JsonContext jsonContext, Object value) {
		jsonContext.writeOpenObject();

		BeanSerializer beanVisitor = new BeanSerializer(jsonContext, value);
		beanVisitor.serialize();

		jsonContext.writeCloseObject();
	}

}