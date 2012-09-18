// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

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
		throw new UnsupportedOperationException();
	}

	// ---------------------------------------------------------------- arguments

	/**
	 * Inserts total number of method's arguments.
	 */
	public static int argumentsCount() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Inserts type of method argument specified by 1-based index.
	 * Works correctly with <code>null</code> argument values. 
	 * @see #createArgumentsClassArray()
	 */
	public static Class argumentType(int index) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Inserts value of method argument specified by 1-based index.
	 * @see #createArgumentsArray()
	 */
	public static Object argument(int index) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Assigns new value for an argument specified by 1-based index.
	 */
	public static void setArgument(Object value, int index) {
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates array of arguments types. Works correctly with <code>null</code> argument values.
	 * Equals to: <code>new Class[] {Arg1Type.class, Arg2Type.class...}</code>
	 * @see #createArgumentsArray()
	 * @see #argumentType(int)
	 */
	public static Class[] createArgumentsClassArray() {
		throw new UnsupportedOperationException();
	}

	// ---------------------------------------------------------------- return value

	/**
	 * Inserts return type of target method. <code>null</code> is used for void.
	 */
	public static Class returnType() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Push default result value, so the following <code>return</code> instruction returns it.
	 */
	public static void pushDefaultResultValue() {
		throw new UnsupportedOperationException();
	}

	// ---------------------------------------------------------------- target

	/**
	 * Inserts proxy (i.e. target) instance.
	 */
	public static Object target() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Inserts target class.
	 */
	public static Class targetClass() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Inserts target method name. Useful for reflection.
	 */
	public static String targetMethodName() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Inserts target method signature, java alike, including the method name.
	 * Useful for identifying the method, since it is unique for class.
	 * @see #targetMethodDescription()
	 */
	public static String targetMethodSignature() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Inserts target method description, bytecode alike, <b>without</b> method name.
	 * May be used for identifying the method.
	 * @see #targetMethodSignature() 
	 */
	public static String targetMethodDescription() {
		throw new UnsupportedOperationException();
	}

}
