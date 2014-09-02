// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.introspector.PropertyDescriptor;

/**
 * Property injection point.
 */
public class PropertyInjectionPoint {

	public static final PropertyInjectionPoint[] EMPTY = new PropertyInjectionPoint[0]; 

	public final PropertyDescriptor propertyDescriptor;
	public final String[] references;

	PropertyInjectionPoint(PropertyDescriptor propertyDescriptor, String[] references) {
		this.propertyDescriptor = propertyDescriptor;
		this.references = references;
	}

}
