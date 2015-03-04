// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.CtorDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.introspector.Setter;
import jodd.typeconverter.TypeConverterManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Just a base class of {@link jodd.json.JsonParser} that contains
 * various utilities, to reduce the size of a parser.
 */
public abstract class JsonParserBase {

	/**
	 * Creates new instance of {@link jodd.json.MapToBean}.
	 */
	protected MapToBean createMapToBean(String classMetadataName) {
		return new MapToBean(this, classMetadataName);
	}

	// ---------------------------------------------------------------- object tools

	/**
	 * Creates new type for JSON array objects.
	 * It should (?) always return a list, for performance reasons.
	 * Later, the list will be converted into the target type.
	 */
	protected List<Object> newArrayInstance(Class targetType) {
		if (targetType == null || targetType == List.class || targetType.isArray()) {
			return new ArrayList<Object>();
		}

		try {
			return (List) targetType.newInstance();
		} catch (Exception e) {
			throw new JsonException(e);
		}
	}

	/**
	 * Creates new object or a <code>HashMap</code> if type is not specified.
	 */
	protected Object newObjectInstance(Class targetType) {
		if (targetType == null) {
			return new HashMap();
		}

		if (targetType == Map.class) {
			return new HashMap();
		}

		ClassDescriptor cd = ClassIntrospector.lookup(targetType);

		CtorDescriptor ctorDescriptor = cd.getDefaultCtorDescriptor(true);
		if (ctorDescriptor == null) {
			throw new JsonException("Default ctor not found for: " + targetType.getName());
		}

		try {
			return ctorDescriptor.getConstructor().newInstance();
		} catch (Exception e) {
			throw new JsonException(e);
		}
	}

	/**
	 * Injects value into the targets property.
	 */
	protected void injectValueIntoObject(Object target, PropertyDescriptor pd, Object value) {
		Object convertedValue = value;

		if (value != null) {
			Class targetClass = pd.getType();

			convertedValue = convertType(value, targetClass);
		}

		try {
			Setter setter = pd.getSetter(true);
			if (setter != null) {
				setter.invokeSetter(target, convertedValue);
			}
		} catch (Exception ex) {
			throw new JsonException(ex);
		}
	}

	/**
	 * Converts type of the given value.
	 */
	protected Object convertType(Object value, Class targetType) {
		Class valueClass = value.getClass();

		if (valueClass == targetType) {
			return value;
		}

		try {
			return TypeConverterManager.convertType(value, targetType);
		}
		catch (Exception ex) {
			throw new JsonException("Type conversion failed", ex);
		}
	}

}