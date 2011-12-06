// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

/**
 * Proxetta naming conventions.
 */
public interface ProxettaNaming {

	// ---------------------------------------------------------------- constants

	/**
	 * {@link jodd.proxetta.ProxyAdvice#execute()}
	 */
	String EXECUTE_METHOD_NAME = "execute";


	// ---------------------------------------------------------------- settings

	/**
	 * Proxy class name suffix.
	 */
	String PROXY_CLASS_NAME_SUFFIX = "$Proxetta";

	/**
	 * Proxy class name suffix.
	 */
	String INVOKE_PROXY_CLASS_NAME_SUFFIX = "$Clone";

	/**
	 * Prefix for advice method names.
	 */
	String METHOD_PREFIX = "$__";

	/**
	 * Divider for method names.
	 */
	String METHOD_DIVIDER = "$";


	/**
	 * Method name for advice 'clinit' methods.
	 */
	String CLINIT_METHOD_NAME = "$clinit";

	/**
	 * Method name for advice default constructor ('init') methods.
	 */
	String INIT_METHOD_NAME = "$init";

	/**
	 * Prefix for advice field names.
	 */
	String FIELD_PREFIX = "$__";

	/**
	 * Divider for field names.
	 */
	String FIELD_DIVIDER = "$";

}
