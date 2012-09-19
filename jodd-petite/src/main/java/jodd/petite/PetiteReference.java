// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

/**
 * {@link jodd.petite.meta.PetiteInject Petite injection} references.
 * When injection annotation is used without the explicit bean name,
 * Petite Container will try to resolve name from various sources,
 * defined by this enumeration.
 */
public enum PetiteReference {

	/**
	 * Field or argument name is used as bean name.
	 */
	NAME,

	/**
	 * Un-capitalized short type name is used as bean name.
	 */
	TYPE_SHORT_NAME,

	/**
	 * Full type name (package and class name) is used as bean name.
	 */
	TYPE_FULL_NAME

}
