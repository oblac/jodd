package jodd.bean;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DeepBeanCopy extends BeanCopy {

	private Set<Class<?>> collectionTypes = new HashSet<Class<?>>(Arrays.asList(List.class, Set.class));
	private Set<Class<?>> primitiveTypes = new HashSet<Class<?>>(Arrays.asList(Boolean.class, Double.class, Float.class, Integer.class, Locale.class, Long.class, String.class));
	private Map<Class<?>, Class<?>> mappingTypes = new HashMap<>();
	
	public DeepBeanCopy(Object source, Object destination) {
		super(source, destination);
	}
	
	public static DeepBeanCopy beans(Object source, Object destination) {
		return new DeepBeanCopy(source, destination);
	}
	
	@Override
	public void copy() {
		try {
			copyProperties(source, destination);
		} 
		catch (Exception e) {
			e.printStackTrace();
			new RuntimeException(e);
		}
	}
	
	protected PropertyDescriptor[] getPropertyDescriptors(Class<?> beanClass) 
		throws Exception {
		
		BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
		
		return beanInfo.getPropertyDescriptors();
	}
	
	protected PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String name)
		throws Exception {

		if (name.equals("class")) {
			return null;
		}

		BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);

		for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
			if (name.equals(propertyDescriptor.getName())) {
				return propertyDescriptor;
			}
		}

		return null;
	}
	
	protected void copyProperties(Object source, Object destination) throws Exception {
		for (PropertyDescriptor destinationPropertyDescriptor : getPropertyDescriptors(destination.getClass())) {
			String name = destinationPropertyDescriptor.getName();
			
			PropertyDescriptor sourcePropertyDescriptor = getPropertyDescriptor(source.getClass(), name);
			
			if (!isPropertyReadable(sourcePropertyDescriptor) || !isPropertyWritable(destinationPropertyDescriptor)) {
				continue;
			}
			
			Method readMethod = sourcePropertyDescriptor.getReadMethod();
			
			Object readValue = readMethod.invoke(source);
			
			if (readValue == null) {
				continue;
			}

			Method writeMethod = destinationPropertyDescriptor.getWriteMethod();
			
			if (isCollection(sourcePropertyDescriptor.getPropertyType(), destinationPropertyDescriptor.getPropertyType())) {
				Class<?> collectionBeanClass = getCollectionBeanClass(writeMethod);

				writeMethod.invoke(destination, copyCollection((Collection<?>)readValue, collectionBeanClass));
			}
			else if (isMap(sourcePropertyDescriptor.getPropertyType(), destinationPropertyDescriptor.getPropertyType())) {
				copyMap((Map<?, ?>)readValue, destination, writeMethod);
			}
			else if (isPrimitive(sourcePropertyDescriptor.getPropertyType(), destinationPropertyDescriptor.getPropertyType())) {
				writeMethod.invoke(destination, readValue);
			}
			else {
				Class<?> destinationPropertyType = destinationPropertyDescriptor.getPropertyType();
				
				Object newDestination = null;
				
				if (destinationPropertyType.isInterface()) {
					Class<?> newDestinationType = mappingTypes.get(readValue.getClass());
					
					if (newDestinationType != null) {
						newDestination = newDestinationType.newInstance();
					}
				}
				else {
					newDestination = destinationPropertyType.newInstance();
				}
				
				copyProperties(readValue, newDestination);
				
				writeMethod.invoke(destination, newDestination);
			}
		}
	}
	
	protected void copyMap(
			Map<?, ?> sourceMap, Object destination, Method writeMethod)
		throws Exception {
		
		Class<?> destinationKeyType = getMapKeyType(writeMethod);

		Class<?> destinationValueType = getMapValueType(writeMethod);

		Map<Object, Object> targetMap =
			(Map<Object, Object>)sourceMap.getClass().newInstance();

		for (Object key : sourceMap.keySet()) {
			Object destinationKey = null;
			Object destinationValue = null;

			Object sourceValue = sourceMap.get(key);

			if (isPrimitive(key.getClass(), destinationKeyType)) {
				destinationKey = key;
			}
			else {
				destinationKey = destinationKeyType.newInstance();
				
				copyProperties(key, destinationKey);
			}

			if (isPrimitive(sourceValue.getClass(), destinationValueType)) {
				destinationValue = sourceValue;
			}
			else {
				destinationValue = destinationValueType.newInstance();
				
				copyProperties(sourceValue, destinationValue);
			}

			targetMap.put(destinationKey, destinationValue);
		}

		writeMethod.invoke(destination, targetMap);
	}
	
	protected Class<?> getMapKeyType(Method writeMethod) {
		Type[] types = writeMethod.getGenericParameterTypes();

		ParameterizedType parameterizedType = (ParameterizedType)types[0];

		return (Class<?>)parameterizedType.getActualTypeArguments()[0];
	}

	protected Class<?> getMapValueType(Method writeMethod) {
		Type[] types = writeMethod.getGenericParameterTypes();

		ParameterizedType parameterizedType = (ParameterizedType)types[0];

		return (Class<?>)parameterizedType.getActualTypeArguments()[1];
	}
	protected Collection<?> copyCollection(
			Collection<?> sourceCollection, Class<?> collectionBeanClass)
		throws Exception {

		if (sourceCollection == null) {
			return null;
		}

		Collection<Object> target = null;
		
		try {
			target = (Collection<Object>)sourceCollection.getClass().newInstance();
		}
		catch (Exception e) {

			// Catch Arrays.asList case which is not instatiable

			if (List.class.isAssignableFrom(sourceCollection.getClass())) {
				target = new ArrayList<Object>();
			}
			else {
				target = new HashSet<Object>();
			}
		}
		
		for (Object source : sourceCollection) {
			if (isPrimitive(source.getClass(), collectionBeanClass)) {
				target.add(source);
			}
			else {
				Object destination = collectionBeanClass.newInstance();
				
				copyProperties(source, destination);
				
				target.add(destination);
			}
		}

		return target;
	}
	
	protected Class<?> getCollectionBeanClass(Method writeMethod) {
		Type[] types = writeMethod.getGenericParameterTypes();

		ParameterizedType parameterizedType = (ParameterizedType)types[0];

		return (Class<?>)parameterizedType.getActualTypeArguments()[0];
	}
	
	protected boolean isCollection(Class<?> sourcePropertyType, Class<?> destinationPropertyType) {
		if (collectionTypes.contains(sourcePropertyType) && collectionTypes.contains(destinationPropertyType)) {
			return true;
		}

		return false;
	}
	
	protected boolean isMap(Class<?> sourcePropertyType, Class<?> destinationPropertyType) {
		if (sourcePropertyType.equals(Map.class) && destinationPropertyType.equals(Map.class)) {
			return true;
		}

		return false;
	}
	
	protected boolean isPrimitive(Class<?> sourcePropertyType, Class<?> destinationPropertyType) {
		if (!destinationPropertyType.isAssignableFrom(sourcePropertyType)) {
			return false;
		}
		
		if (sourcePropertyType.isPrimitive() || primitiveTypes.contains(sourcePropertyType)) {
			return true;
		}
		
		return false;
	}

	
	protected boolean isPropertyReadable(PropertyDescriptor propertyDescriptor) {
		if (propertyDescriptor == null) {
			return false;
		}
		
		if ((propertyDescriptor.getPropertyType() != null) && (propertyDescriptor.getReadMethod() != null)) {
			return true;
		}
		
		return false;
	} 
	
	protected boolean isPropertyWritable(PropertyDescriptor propertyDescriptor) {
		if (propertyDescriptor == null) {
			return false;
		}
		
		if ((propertyDescriptor.getPropertyType() != null) && (propertyDescriptor.getWriteMethod() != null)) {
			return true;
		}
		
		return false;
	}

	public void addMappingType(Class<?> sourceType, Class<?> destinationType) {
		mappingTypes.put(sourceType, destinationType);
	} 
	
}
