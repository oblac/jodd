package jodd.fastaccess;

/**
 * Method invoker.
 */
public interface MethodInvoker {

	/**
	 * Invokes method on given target with provided parameters.
	 */
	Object invoke(Object target, Object... parameters);

}