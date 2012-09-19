// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import java.lang.reflect.Field;

/**
 * Property injection point.
 */
public class PropertyInjectionPoint {

	public static final PropertyInjectionPoint[] EMPTY = new PropertyInjectionPoint[0]; 

	public final Field field;
	public final String[] reference;

	PropertyInjectionPoint(Field field, String[] reference) {
		this.field = field;
		this.reference = reference;
	}

}
