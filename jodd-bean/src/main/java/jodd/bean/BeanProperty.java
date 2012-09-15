// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.introspector.ClassIntrospector;
import jodd.introspector.ClassDescriptor;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * Represents a bean named property. Contains two information:
 * <ol>
 * <li>Bean instance (and cached class descriptor)</li>
 * <li>Property name</li>
 * </ol>
 * Should be used only by {@link BeanUtil} and similar utils.
 */
class BeanProperty {

	BeanProperty(Object bean, String propertyName, boolean forced) {
		this.name = propertyName;
		setBean(bean);
		this.last = true;
		this.first = true;
		this.forced = forced;
		this.fullName = bean.getClass().getSimpleName() + '#' + propertyName;
	}

	// ---------------------------------------------------------------- bean and descriptor

	final String fullName;  // initial name
	Object bean;
	ClassDescriptor cd;
	String name;        // property name
	boolean last;       // is it a last property (when nested)
	boolean first;		// is it first property (when nested)

	/**
	 * Sets new bean instance.
	 */
	public void setBean(Object bean) {
		this.bean = bean;
		this.cd = (bean == null ? null : ClassIntrospector.lookup(bean.getClass()));
		this.first = false;
	}

	// ---------------------------------------------------------------- flags

	final boolean forced;

	// ---------------------------------------------------------------- simple properties

	Method method;
	Field field;
	String index;
	
	// ---------------------------------------------------------------- toString

	@Override
	public String toString() {
		return '\'' + fullName + "' (actual:'" + bean.getClass().getSimpleName() + '#' + name + "', forced=" + forced + ')';
	}
}
