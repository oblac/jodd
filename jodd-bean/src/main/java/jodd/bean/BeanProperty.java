// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.Introspector;
import jodd.introspector.PropertyDescriptor;
import jodd.introspector.Getter;
import jodd.introspector.Setter;

/**
 * Represents a bean named property. Contains two information:
 * <ol>
 * <li>Bean instance (and cached class descriptor)</li>
 * <li>Property name</li>
 * </ol>
 * Used only internally by {@link BeanUtil} and similar utils.
 */
class BeanProperty {

	BeanProperty(Object bean, String propertyName, boolean forced) {
		this.introspector = BeanUtil.getBeanUtilBean().getIntrospector();
		setName(propertyName);
		setBean(bean);
		this.last = true;
		this.first = true;
		this.forced = forced;
		this.fullName = bean.getClass().getSimpleName() + '#' + propertyName;
	}

	// ---------------------------------------------------------------- bean and descriptor

	final String fullName;  // initial name
	final Introspector introspector;
	Object bean;
	private ClassDescriptor cd;
	String name;        // property name
	boolean last;       // is it a last property (when nested)
	boolean first;		// is it first property (when nested)

	/**
	 * Sets current property name.
	 */
	public void setName(String name) {
		this.name = name;
		this.updateProperty = true;
	}

	/**
	 * Sets new bean instance.
	 */
	public void setBean(Object bean) {
		this.bean = bean;
		this.cd = (bean == null ? null : introspector.lookup(bean.getClass()));
		this.first = false;
		this.updateProperty = true;
	}

	// ---------------------------------------------------------------- flags

	final boolean forced;

	// ---------------------------------------------------------------- simple properties

	// indicates that property descriptor has to be updated
	// since there was some property-related change of BeanProperty state
	private boolean updateProperty = true;

	// most recent property descriptor
	private PropertyDescriptor propertyDescriptor;

	/**
	 * Loads property descriptor, if property was updated.
	 */
	private void loadPropertyDescriptor() {
		if (updateProperty) {
			if (cd == null) {
				propertyDescriptor = null;
			} else {
				propertyDescriptor = cd.getPropertyDescriptor(name, true);
			}
			updateProperty = false;
		}
	}

	/**
	 * Returns getter.
	 */
	public Getter getGetter(boolean declared) {
		loadPropertyDescriptor();
		return propertyDescriptor != null ? propertyDescriptor.getGetter(declared) : null;
	}

	/**
	 * Returns setter.
	 */
	public Setter getSetter(boolean declared) {
		loadPropertyDescriptor();
		return propertyDescriptor != null ? propertyDescriptor.getSetter(declared) : null;
	}

	/**
	 * Returns <code>true</code> if class is a map.
	 */
	public boolean isMap() {
		return cd.isMap();
	}

	String index;

	// ---------------------------------------------------------------- toString

	@Override
	public String toString() {
		return fullName + " (" + bean.getClass().getSimpleName() + '#' + name + ", forced=" + forced + ')';
	}
}