// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.Getter;
import jodd.introspector.PropertyDescriptor;
import jodd.json.meta.JsonAnnotationManager;

import java.lang.reflect.Modifier;

/**
 * Bean visitor that serializes properties of a bean.
 * It analyzes the rules for inclusion/exclusion of a property.
 */
public class BeanSerializer {

	private final JsonContext jsonContext;
	private final Object source;
	private boolean declared;
	private final String classMetadataName;
	private final Class type;

	private int count;

	private final JsonAnnotationManager.TypeData typeData;

	public BeanSerializer(JsonContext jsonContext, Object bean) {
		this.jsonContext = jsonContext;
		this.source = bean;
		this.count = 0;
		this.declared = false;
		this.classMetadataName = jsonContext.jsonSerializer.classMetadataName;

		type = bean.getClass();

		JsonAnnotationManager jsonAnnotationManager = JsonAnnotationManager.getInstance();

		typeData = jsonAnnotationManager.lookupTypeData(type);
	}

	/**
	 * Serializes a bean.
	 */
	public void serialize() {
		Class type = source.getClass();

		ClassDescriptor classDescriptor = ClassIntrospector.lookup(type);

		if (classMetadataName != null) {
			// process first 'meta' fields 'class'
			onProperty(classMetadataName, null, null, false);
		}

		PropertyDescriptor[] propertyDescriptors = classDescriptor.getAllPropertyDescriptors();

		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {

			Getter getter = propertyDescriptor.getGetter(declared);
			if (getter != null) {
				String propertyName = propertyDescriptor.getName();
				Class propertyType = propertyDescriptor.getType();

				boolean isTransient = false;
				// check for transient flag
				FieldDescriptor fieldDescriptor = propertyDescriptor.getFieldDescriptor();

				if (fieldDescriptor != null) {
					isTransient = Modifier.isTransient(fieldDescriptor.getField().getModifiers());
				}

				onProperty(propertyName, propertyType, propertyDescriptor, isTransient);
			}
		}
	}

	/**
	 * Invoked on each property.
	 */
	protected void onProperty(String propertyName, Class propertyType, PropertyDescriptor pd, boolean isTransient) {
		Path currentPath = jsonContext.path;

		currentPath.push(propertyName);

		// determine if name should be included/excluded

		boolean include = !typeData.strict;

		// + don't include transient fields

		if (isTransient) {
			include = false;
		}

		// + all collections are not serialized by default

		include = jsonContext.matchIgnoredPropertyTypes(propertyType, include);

		// + annotations

		include = typeData.rules.apply(propertyName, true, include);

		// + path queries: excludes/includes

		include = jsonContext.matchPathToQueries(include);

		// done

		if (!include) {
			currentPath.pop();
			return;
		}

		Object value;

		if (propertyType == null) {
			// metadata - classname
			value = source.getClass().getName();
		} else {
			value = readProperty(source, pd);

			// change name for properties

			propertyName = typeData.resolveJsonName(propertyName);
		}

		jsonContext.pushName(propertyName, count > 0);
		jsonContext.serialize(value);

		if (jsonContext.isNamePoped()) {
			count++;
		}

		currentPath.pop();
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