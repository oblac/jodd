package jodd.introspector;

import jodd.fastaccess.FastAccessException;
import jodd.fastaccess.MethodInvoker;
import jodd.fastaccess.MethodInvokerClassBuilder;

import java.lang.reflect.Method;

public class FastAccessibleIntrospector extends AccessibleIntrospector {

	@Override
	protected ClassDescriptor describeClass(final Class type) {

		// fast class descriptor
		return new ClassDescriptor(type, true) {

			// fast method descriptor
			@Override
			protected MethodDescriptor createMethodDescriptor(Method method) {

				final MethodInvoker methodInvokerInstance;

				try {
					methodInvokerInstance = MethodInvokerClassBuilder.createNewInstane(method);
				} catch (Exception ex) {
					throw new FastAccessException(ex);
				}

				return new MethodDescriptor(this, method) {
					@Override
					public Object invoke(Object target, Object... parameters) {
						return methodInvokerInstance.invoke(target, parameters);
					}
				};
			}
		};
	}

}