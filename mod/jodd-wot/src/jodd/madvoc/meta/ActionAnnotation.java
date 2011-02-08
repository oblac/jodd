// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.meta;

import jodd.typeconverter.Convert;
import jodd.util.ReflectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Action method annotation reader.
 */
public class ActionAnnotation<A extends Annotation> {

	protected final Class<A> annotationClass;

	@SuppressWarnings( {"unchecked"})
	public ActionAnnotation() {
		annotationClass = ReflectUtil.getGenericSupertype(this.getClass());
	}

	public ActionAnnotation(Class<A> annotationClass) {
		this.annotationClass = annotationClass;
	}

	/**
	 * Returns annotation class.
	 */
	public Class<A> getAnnotationClass() {
		return annotationClass;
	}

	/**
	 * Returns <code>true</code> if annotation is present on given method.
	 */
	public boolean hasAnnotation(Method method) {
		return method.isAnnotationPresent(annotationClass);
	}

	/**
	 * Reads {@link ActionAnnotationData annotation data} on provided action method.
	 * If annotation is not presented, <code>null</code> is returned.
	 */
	public ActionAnnotationData<A> readAnnotationData(Method actionMethod) {

		ActionAnnotationData<A> ad = createAnnotationData();

		ad.annotation = actionMethod.getAnnotation(annotationClass);
		if (ad.annotation == null) {
			return null;
		}

		ad.value = readElementValue(ad.annotation, "value");

		ad.extension = readElementValue(ad.annotation, "extension");

		ad.alias = readElementValue(ad.annotation, "alias");

		ad.method = readElementValue(ad.annotation, "method");

		return ad;
	}


	/**
	 * Creates new annotation data with optional default values.
	 */
	protected ActionAnnotationData<A> createAnnotationData() {
	    return new ActionAnnotationData<A>();
	}

	/**
	 * Reads non empty annotation element value. If annotation value is
	 * an empty string, returns <code>null</code>.
	 */
	protected String readElementValue(A annotation, String name) {
		String value = Convert.toString(ReflectUtil.readAnnotationValue(annotation, name));
		if (value != null) {
			value = value.trim();
			if (value.length() == 0) {
				value = null;
			}
		}
		return value;
	}

}
