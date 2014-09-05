// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.introspector.Getter;
import jodd.introspector.PropertyDescriptor;

/**
 * Bean visitor that serializes properties of a bean.
 * It analyzes the rules for inclusion/exclusion of a property.
 */
public class BeanSerializer extends TypeJsonVisitor {

	protected final Object source;

	public BeanSerializer(JsonContext jsonContext, Object bean) {
		super(jsonContext, bean.getClass());

		this.source = bean;
	}

	/**
	 * Serializes a bean.
	 */
	public void serialize() {
		visit();
	}

	/**
	 * Reads property value and {@link #onSerializableProperty(String, Class, Object) serializes it}.
	 */
	@Override
	protected final void onSerializableProperty(String propertyName, PropertyDescriptor propertyDescriptor) {
		Object value;

		if (propertyDescriptor == null) {
			// metadata - classname
			value = source.getClass().getName();
		} else {
			value = readProperty(source, propertyDescriptor);

			// change name for properties

			propertyName = typeData.resolveJsonName(propertyName);
		}

		onSerializableProperty(
				propertyName,
				propertyDescriptor == null ? null : propertyDescriptor.getType(),
				value);
	}

	/**
	 * Invoked on serializable properties, that have passed all the rules.
	 * Property type is <code>null</code> for metadata class name property.
	 */
	protected void onSerializableProperty(String propertyName, Class propertyType, Object value) {
		jsonContext.pushName(propertyName, count > 0);

		jsonContext.serialize(value);

		if (jsonContext.isNamePopped()) {
			count++;
		}
	}

	/**
	 * Reads property using property descriptor.
	 */
	private Object readProperty(Object source, PropertyDescriptor propertyDescriptor) {
		Getter getter = propertyDescriptor.getGetter(declared);

		if (getter != null) {
			try {
				return getter.invokeGetter(source);
			}
			catch (Exception ex) {
				throw new JsonException(ex);
			}
		}

		return null;
	}

}