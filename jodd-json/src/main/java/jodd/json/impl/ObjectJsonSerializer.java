// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.BeanSerializer;
import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * Main serializer for objects. It is also the last serializer, used when
 * no other serializer is found.
 */
public class ObjectJsonSerializer implements TypeJsonSerializer<Object> {

	public void serialize(final JsonContext jsonContext, Object value) {
		if (jsonContext.isUsed(value)) {
			// prevent circular dependencies
			return;
		}

		jsonContext.writeOpenObject();

		BeanSerializer beanVisitor = new BeanSerializer(jsonContext, value);
		beanVisitor.serialize();

		jsonContext.writeCloseObject();

		jsonContext.unuseValue();
	}

}