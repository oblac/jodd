// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

/**
 * Proxetta naming conventions.
 */
public class ProxettaNaming {

	// ---------------------------------------------------------------- constants

	/**
	 * {@link jodd.proxetta.ProxyAdvice#execute()}
	 */
	public static final String EXECUTE_METHOD_NAME = "execute";


	// ---------------------------------------------------------------- settings

	/**
	 * Proxy class name suffix.
	 */
	public static String PROXY_CLASS_NAME_SUFFIX = "$Proxetta";

	/**
	 * Prefix for advice method names.
	 */
	public static String METHOD_PREFIX = "$__";

	/**
	 * Divider for method names.
	 */
	public static String METHOD_DIVIDER = "$";


	/**
	 * Method name for advice 'clinit' methods.
	 */
	public static String CLINIT_METHOD_NAME = "$clinit";

	/**
	 * Method name for advice default constructor ('init') methods.
	 */
	public static String INIT_METHOD_NAME = "$init";

	/**
	 * Prefix for advice field names.
	 */
	public static String FIELD_PREFIX = "$__";

	/**
	 * Divider for field names.
	 */
	public static String FIELD_DIVIDER = "$";

}
