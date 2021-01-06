package jodd.methref;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@SuppressWarnings("rawtypes")
public class InterfaceMethodInvocationHandler implements InvocationHandler {
	private final Methref methref;

	public InterfaceMethodInvocationHandler(final Methref methref) {
		this.methref = methref;
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) {
		final String method_name = method.getName();
		methref.lastName(method_name);
		if (method.getReturnType().isPrimitive()) {
			final Class primitiveReturnType = method.getReturnType();
			if (primitiveReturnType.equals(boolean.class)) {
				return false;
			}
			return 0;
		}
		return null;
	}
}
