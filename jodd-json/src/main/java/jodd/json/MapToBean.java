package jodd.json;

import jodd.JoddJson;
import jodd.bean.BeanUtil;
import jodd.util.ReflectUtil;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * Map to bean converter.
 */
public class MapToBean {

	/**
	 * Converts map to bean.
	 */
	public void map2bean(Map map, Object target) {
		for (Object key : map.keySet()) {
			String keyName = key.toString();

			if (JoddJson.classMetadataName != null) {
				if (keyName.equals(JoddJson.classMetadataName)) {
					continue;
				}
			}

			Object value = map.get(key);

			if (value != null) {
				Class propertyType = BeanUtil.getDeclaredPropertyType(target, keyName);

				if (propertyType != null) {
					if (value instanceof Map && (ReflectUtil.isSubclass(propertyType, Map.class) == false)) {
						Object newValue = newObjectInstance(propertyType);

						map2bean((Map) value, newValue);

						value = newValue;
					}
				}
			}

			BeanUtil.setDeclaredPropertyForcedSilent(target, keyName, value);
		}
	}

	protected Object newObjectInstance(Class targetType) {
		try {
			Constructor ctor = targetType.getDeclaredConstructor();
			ctor.setAccessible(true);
			return ctor.newInstance();
		} catch (Exception e) {
			throw new JsonException(e);
		}
	}

}