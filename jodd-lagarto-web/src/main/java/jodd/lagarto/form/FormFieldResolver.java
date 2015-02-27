// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.form;

/**
 * Resolver for form fields.
 */
public interface FormFieldResolver {

	/**
	 * Resolves form field value.
	 */
	Object value(String name);


}