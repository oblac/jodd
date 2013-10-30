package jodd.directaccess;

/**
 * Field invoker.
 */
public interface FieldInvoker {

	/**
	 * Sets field value.
	 */
	void set(Object target, Object value);

	/**
	 * Returns fields value.
	 */
	Object get(Object target);

}