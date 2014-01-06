// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

/**
 * Jodd PROXETTA module.
 */
public class JoddProxetta {

	/**
	 * {@link jodd.proxetta.ProxyAdvice#execute()}
	 */
	public static String executeMethodName = "execute";

	/**
	 * Proxy class name suffix.
	 */
	public static String proxyClassNameSuffix = "$$Proxetta";

	/**
	 * Invoke proxy class name suffix.
	 */
	public static String invokeProxyClassNameSuffix = "$$Clonetou";

	/**
	 * Wrapper class name suffix.
	 */
	public static String wrapperClassNameSuffix = "$$Wraporetto";

	/**
	 * Prefix for advice method names.
	 */
	public static String methodPrefix = "$__";

	/**
	 * Divider for method names.
	 */
	public static String methodDivider = "$";

	/**
	 * Method name for advice 'clinit' methods.
	 */
	public static String clinitMethodName = "$clinit";

	/**
	 * Method name for advice default constructor ('init') methods.
	 */
	public static String initMethodName = "$init";

	/**
	 * Prefix for advice field names.
	 */
	public static String fieldPrefix = "$__";

	/**
	 * Divider for field names.
	 */
	public static String fieldDivider = "$";

	/**
	 * Wrapper target field name.
	 */
	public static String wrapperTargetFieldName = "_target";

}