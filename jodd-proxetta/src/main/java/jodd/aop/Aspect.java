package jodd.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Base aspect class that holds the target instance.
 */
public abstract class Aspect implements InvocationHandler {

	private Object target;

	public Aspect(Object target) {
		this.target = target;
	}

	/**
	 * Returns target object.
	 */
	public final Object getTarget() {
		return this.target;
	}

	/**
	 * Runs before targets method. Returns {@code true} if target method
	 * should run.
	 */
	public abstract boolean before(Object target, Method method, Object[] args);

	/**
	 * Runs after targets method. Returns {@code true} if aspect method should
	 * return value, otherwise {@code null}.
	 */
	public abstract boolean after(Object target, Method method, Object[] args);

	/**
	 * Invoked after exception.
	 */
	public abstract boolean afterException(Object target, Method method, Object[] args, Throwable throwable);

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;

		if (before(target, method, args)) {
			try {
				result = method.invoke(target, args);
			}
			catch (InvocationTargetException e) {
				afterException(args, method, args, e.getTargetException());
			}
			catch (Exception ex) {
				throw ex;
			}
		}
		if (after(target, method, args)) {
			return result;
		}
		return null;
	}

}