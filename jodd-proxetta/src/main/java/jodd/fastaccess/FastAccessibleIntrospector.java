package jodd.fastaccess;

import jodd.introspector.AccessibleIntrospector;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Fast {@link jodd.introspector.AccessibleIntrospector}
 */
public class FastAccessibleIntrospector extends AccessibleIntrospector {

	@Override
	protected ClassDescriptor describeClass(final Class type) {

		// fast class descriptor
		return new ClassDescriptor(type, true) {

			// fast method descriptor
			@Override
			protected MethodDescriptor createMethodDescriptor(Method method) {

				final MethodInvoker methodInvoker;

				try {
					methodInvoker = MethodInvokerClassBuilder.createNewInstance(method);
				} catch (Exception ex) {
					throw new FastAccessException(ex);
				}

				return new MethodDescriptor(this, method) {
					@Override
					public Object invoke(Object target, Object... parameters) {
						return methodInvoker.invoke(target, parameters);
					}
				};
			}

			// fast field descriptor
			@Override
			protected FieldDescriptor createFieldDescriptor(Field field) {
				final FieldInvoker fieldInvoker;

				try {
					fieldInvoker = FieldInvokerClassBuilder.createNewInstance(field);
				} catch (Exception ex) {
					throw new FastAccessException(ex);
				}

				return new FieldDescriptor(this, field) {
					@Override
					public void set(Object target, Object value) {
						fieldInvoker.set(target, value);
					}

					@Override
					public Object get(Object target) {
						return fieldInvoker.get(target);
					}
				};
			}
		};
	}

}