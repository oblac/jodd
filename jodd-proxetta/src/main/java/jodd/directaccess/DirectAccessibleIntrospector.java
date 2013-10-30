package jodd.directaccess;

import jodd.introspector.AccessibleIntrospector;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Direct version of {@link jodd.introspector.AccessibleIntrospector}
 * that does not use reflection to access methods and fields.
 */
public class DirectAccessibleIntrospector extends AccessibleIntrospector {

	@Override
	protected ClassDescriptor describeClass(final Class type) {

		// direct class descriptor
		return new ClassDescriptor(type, true) {

			// direct method descriptor
			@Override
			protected MethodDescriptor createMethodDescriptor(Method method) {

				final MethodInvoker methodInvoker;

				try {
					methodInvoker = MethodInvokerClassBuilder.createNewInstance(method);
				} catch (Exception ex) {
					throw new IllegalArgumentException(ex);
				}

				return new MethodDescriptor(this, method) {
					@Override
					public Object invoke(Object target, Object... parameters) {
						return methodInvoker.invoke(target, parameters);
					}
				};
			}

			// direct field descriptor
			@Override
			protected FieldDescriptor createFieldDescriptor(Field field) {
				final FieldInvoker fieldInvoker;

				try {
					fieldInvoker = FieldInvokerClassBuilder.createNewInstance(field);
				} catch (Exception ex) {
					throw new IllegalArgumentException(ex);
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