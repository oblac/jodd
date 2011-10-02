// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

/**
 * Resolves bean template macro values.
 */
public interface BeanTemplateMacroResolver {

	/**
	 * Resolves founded macro name in bean template.
	 * It may throw an {@link BeanException exception}
	 * if name is not found.
	 */
	Object resolve(String name);
}
