// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

/**
 * Marker class for {@link ProxyAdvice proxy} implementations.
 */
@SuppressWarnings({"UnusedDeclaration"})
public final class ProxyTarget {

	// ---------------------------------------------------------------- invocation

	/**
	 * Inserts the invocation of target method and getting the invocation results.
	 * Small types are converted to wrappers. If method is <code>void</code>,
	 * <code>null</code> is used for return value.
	 */
	public static Object invoke() {
		throw new ProxettaException();
	}

	// ---------------------------------------------------------------- arguments

	/**
	 * Inserts total number of method's arguments.
	 */
	public static int argumentsCount() {
		throw new ProxettaException();
	}

	/**
	 * Inserts type of method argument specified by 1-based index.
	 * Works correctly with <code>null</code> argument values. 
	 * @see #createArgumentsClassArray()
	 */
	public static Class argumentType(int index) {
		throw new ProxettaException();
	}

	/**
	 * Inserts value of method argument specified by 1-based index.
	 * @see #createArgumentsArray()
	 */
	public static Object argument(int index) {
		throw new ProxettaException();
	}

	/**
	 * Assigns new value for an argument specified by 1-based index.
	 */
	public static void setArgument(Object value, int index) {
		throw new ProxettaException();
	}

	// ---------------------------------------------------------------- arguments array

	/**
	 * Creates array of arguments values. It is more safely then to get one argument at time, since
	 * the returned array has correct length.
	 * Equals to: <code>new Object[] {arg1, arg2,...}</code>
	 * @see #createArgumentsClassArray()
	 * @see #argument(int) 
	 */
	public static Object[] createArgumentsArray() {
		throw new ProxettaException();
	}

	/**
	 * Creates array of arguments types. Works correctly with <code>null</code> argument values.
	 * Equals to: <code>new Class[] {Arg1Type.class, Arg2Type.class...}</code>
	 * @see #createArgumentsArray()
	 * @see #argumentType(int)
	 */
	public static Class[] createArgumentsClassArray() {
		throw new ProxettaException();
	}

	// ---------------------------------------------------------------- return value

	/**
	 * Inserts return type of target method. <code>null</code> is used for void.
	 */
	public static Class returnType() {
		throw new ProxettaException();
	}

	/**
	 * Prepares return value. Must be used as last method call:
	 * <pre>
	 *     ...
	 *     return ProxyTarget.returnValue(xxx);
	 * </pre>
	 *
	 * Used when returning values in general case, when return type may
	 * be either primitive or an object. Also, must be used when returning <code>null</code>
	 * for primitives.
	 */
	public static Object returnValue(Object value) {
		throw new ProxettaException();
	}

	// ---------------------------------------------------------------- target

	/**
	 * Inserts proxy (i.e. target) instance.
	 */
	public static Object target() {
		throw new ProxettaException();
	}
	
	/**
	 * Inserts target class.
	 */
	public static Class targetClass() {
		throw new ProxettaException();
	}

	/**
	 * Inserts target method name. Useful for reflection.
	 */
	public static String targetMethodName() {
		throw new ProxettaException();
	}

	/**
	 * Inserts target method signature, java alike, including the method name.
	 * Useful for identifying the method, since it is unique for class.
	 * @see #targetMethodDescription()
	 */
	public static String targetMethodSignature() {
		throw new ProxettaException();
	}

	/**
	 * Inserts target method description, bytecode alike, <b>without</b> method name.
	 * May be used for identifying the method.
	 * @see #targetMethodSignature() 
	 */
	public static String targetMethodDescription() {
		throw new ProxettaException();
	}

	/**
	 * Inserts targets method annotation value. Inserts <code>null</code>
	 * if annotation or element is missing.
	 */
	public static Object targetMethodAnnotation(String annotationClassName, String element) {
		throw new ProxettaException();
	}
	/**
	 * Inserts targets class annotation value. Inserts <code>null</code>
	 * if annotation or element is missing.
	 */
	public static Object targetClassAnnotation(String annotationClassName, String element) {
		throw new ProxettaException();
	}

	// ---------------------------------------------------------------- info

	/**
	 * Inserts populated {@link jodd.proxetta.ProxyTargetInfo} instance.
	 */
	public static ProxyTargetInfo info() {
		throw new ProxettaException();
	}

}
