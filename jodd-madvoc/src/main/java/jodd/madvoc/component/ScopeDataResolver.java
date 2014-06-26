// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.madvoc.ScopeData;
import jodd.madvoc.ScopeType;
import jodd.madvoc.MadvocException;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.InOut;
import jodd.madvoc.meta.Out;
import jodd.util.ReflectUtil;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * Resolver for {@link jodd.madvoc.ScopeData scope data} for certain types.
 * It does not cache anything as Scope data is cached in {@link jodd.madvoc.ActionConfig}.
 * Resolving only happens during the initialization, and it might be repeated for
 * certain types (as there is no cache), but that is acceptable to reduce memory
 * usage (no cache) and several lookups (for each interceptor) during every request.
 */
public class ScopeDataResolver {

	/**
	 * Resolve scope data in given type for all scope types.
	 * Returns <code>null</code> if no scope data exist.
	 */
	public ScopeData[] resolveScopeData(Class type) {
		final ScopeType[] allScopeTypes = ScopeType.values();

		ScopeData[] scopeData = new ScopeData[allScopeTypes.length];

		int count = 0;

		for (ScopeType scopeType : allScopeTypes) {
			ScopeData sd = inspectClassScopeData(type, scopeType);
			if (sd != null) {
				count++;
			}
			scopeData[scopeType.value()] = sd;
		}

		if (count == 0) {
			return null;
		}

		return scopeData;
	}

	/**
	 * Resolves scope data in given annotations for all scope types.
	 * Returns <code>null</code> if no scope data exist.
	 */
	public ScopeData[] resolveScopeData(String name, Class type, Annotation[] annotations) {
		final ScopeType[] allScopeTypes = ScopeType.values();

		ScopeData[] scopeData = new ScopeData[allScopeTypes.length];

		int count = 0;

		for (ScopeType scopeType : allScopeTypes) {
			ScopeData sd = inspectMethodParameterScopeData(name, type, annotations, scopeType);
			if (sd != null) {
				count++;
			}
			scopeData[scopeType.value()] = sd;
		}

		if (count == 0) {
			return null;
		}

		return scopeData;
	}

	/**
	 * Scans annotation and returns type of Madvoc annotations.
	 */
	public Class<? extends Annotation> detectAnnotationType(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof In) {
				return annotation.annotationType();
			}
			else if (annotation instanceof Out) {
				return annotation.annotationType();
			}
			else if (annotation instanceof InOut) {
				return annotation.annotationType();
			}
		}
		return null;
	}


	// ---------------------------------------------------------------- inspect method

	/**
	 * Inspects all method parameters for scope type.
	 */
	protected ScopeData inspectMethodParameterScopeData(String name, Class type, Annotation[] annotations, ScopeType scopeType) {
		ScopeData sd = new ScopeData();
		int count = 0;

		for (Annotation annotation : annotations) {

			if (annotation instanceof In) {
				ScopeData.In scopeDataIn = inspectIn((In) annotation, scopeType, name, type);
				if (scopeDataIn != null) {
					count++;
					sd.in = new ScopeData.In[] {scopeDataIn};
				}
			}
			else if (annotation instanceof Out) {
				ScopeData.Out scopeDataOut = inspectOut((Out) annotation, scopeType, name, type);
				if (scopeDataOut != null) {
					count++;
					sd.out = new ScopeData.Out[] {scopeDataOut};
				}
			}
			else if (annotation instanceof InOut) {
				ScopeData.In scopeDataIn = inspectIn((InOut) annotation, scopeType, name, type);
				if (scopeDataIn != null) {
					count++;
					sd.in = new ScopeData.In[] {scopeDataIn};
				}

				ScopeData.Out scopeDataOut = inspectOut((InOut) annotation, scopeType, name, type);
				if (scopeDataOut != null) {
					count++;
					sd.out = new ScopeData.Out[] {scopeDataOut};
				}
			}
		}

		if (count == 0) {
			return null;
		}

		return sd;
	}

	// ---------------------------------------------------------------- inspect class

	/**
	 * Saves value and property name.
	 */
	protected void saveNameTarget(ScopeData.In ii, String value, String propertyName) {
		value = value.trim();
		if (value.length() > 0) {
			ii.name = value;
			ii.target = propertyName;
		}
		else {
			ii.name = propertyName;
			ii.target = null;
		}
	}

	/**
	 * Saves value and property name.
	 */
	protected void saveNameTarget(ScopeData.Out oi, String value, String propertyName) {
		value = value.trim();
		if (value.length() > 0) {
			oi.name = value;
			oi.target = propertyName;
		}
		else {
			oi.name = propertyName;
			oi.target = null;
		}
	}

	/**
	 * Inspects single IN annotation for a property.
	 */
	protected ScopeData.In inspectIn(In in, ScopeType scopeType, String propertyName, Class propertyType) {
		if (in == null) {
			return null;
		}
		ScopeType scope = in.scope();
		if (scope != scopeType) {
			return null;
		}
		ScopeData.In ii = new ScopeData.In();
		saveNameTarget(ii, in.value(), propertyName);
		ii.type = propertyType;
		return ii;
	}

	/**
	 * Inspects single INOUT annotation as IN.
	 * @see #inspectIn(jodd.madvoc.meta.In, jodd.madvoc.ScopeType, String, Class)
	 */
	protected ScopeData.In inspectIn(InOut inOut, ScopeType scopeType, String propertyName, Class propertyType) {
		if (inOut == null) {
			return null;
		}
		ScopeType scope = inOut.scope();
		if (scope != scopeType) {
			return null;
		}
		ScopeData.In ii = new ScopeData.In();
		saveNameTarget(ii, inOut.value(), propertyName);
		ii.type = propertyType;
		return ii;
	}

	/**
	 * Inspects single OUT annotation for a property.
	 */
	protected ScopeData.Out inspectOut(Out out, ScopeType scopeType, String propertyName, Class propertyType) {
		if (out == null) {
			return null;
		}
		ScopeType scope = out.scope();
		if (scope != scopeType) {
			return null;
		}
		ScopeData.Out oi = new ScopeData.Out();
		saveNameTarget(oi, out.value(), propertyName);
		oi.type = propertyType;
		return oi;
	}

	/**
	 * Inspects single INOUT annotation as OUT.
	 * @see #inspectOut(jodd.madvoc.meta.Out, jodd.madvoc.ScopeType, String, Class)
	 */
	protected ScopeData.Out inspectOut(InOut inOut, ScopeType scopeType, String propertyName, Class propertyType) {
		if (inOut == null) {
			return null;
		}
		ScopeType scope = inOut.scope();
		if (scope != scopeType) {
			return null;
		}
		ScopeData.Out oi = new ScopeData.Out();
		saveNameTarget(oi, inOut.value(), propertyName);
		oi.type = propertyType;
		return oi;
	}

	/**
	 * Inspect action for all In/Out annotations.
	 * Returns <code>null</code> if there are no In and Out data.
	 */
	protected ScopeData inspectClassScopeData(Class actionClass, ScopeType scopeType) {
		ClassDescriptor cd = ClassIntrospector.lookup(actionClass);
		FieldDescriptor[] fields = cd.getAllFieldDescriptors();
		MethodDescriptor[] methods = cd.getAllMethodDescriptors();

		List<ScopeData.In> listIn = new ArrayList<ScopeData.In>(fields.length + methods.length);
		List<ScopeData.Out> listOut = new ArrayList<ScopeData.Out>(fields.length + methods.length);


		// fields
		for (FieldDescriptor fieldDescriptor : fields) {
			Field field = fieldDescriptor.getField();

			Class fieldType = ReflectUtil.getRawType(field.getGenericType(), actionClass);

			In in = field.getAnnotation(In.class);
			ScopeData.In ii = inspectIn(in, scopeType, field.getName(), fieldType);
			if (ii != null) {
				listIn.add(ii);
			}
			InOut inout = field.getAnnotation(InOut.class);
			if (inout != null) {
				if (in != null) {
					throw new MadvocException("@InOut can not be used with @In: " + field.getDeclaringClass() + '#' + field.getName());
				}
				ii = inspectIn(inout, scopeType, field.getName(), field.getType());
				if (ii != null) {
					listIn.add(ii);
				}
			}

			Out out = field.getAnnotation(Out.class);
			ScopeData.Out oi = inspectOut(out, scopeType, field.getName(), fieldType);
			if (oi != null) {
				listOut.add(oi);
			}
			inout = field.getAnnotation(InOut.class);
			if (inout != null) {
				if (out != null) {
					throw new MadvocException("@InOut can not be used with @Out: " + field.getDeclaringClass() + '#' + field.getName());
				}
				oi = inspectOut(inout, scopeType, field.getName(), field.getType());
				if (oi != null) {
					listOut.add(oi);
				}
			}
		}

		// methods
		for (MethodDescriptor methodDescriptor : methods) {
			Method method = methodDescriptor.getMethod();

			String propertyName = ReflectUtil.getBeanPropertySetterName(method);
			if (propertyName != null) {
				In in = method.getAnnotation(In.class);
				ScopeData.In ii = inspectIn(in, scopeType, propertyName, method.getParameterTypes()[0]);
				if (ii != null) {
					listIn.add(ii);
				}
				InOut inout = method.getAnnotation(InOut.class);
				if (inout != null) {
					if (in != null) {
						throw new MadvocException("@InOut can not be used with @In: " + method.getDeclaringClass() + '#' + method.getName());
					}
					ii = inspectIn(inout, scopeType, propertyName, method.getParameterTypes()[0]);
					if (ii != null) {
						listIn.add(ii);
					}
				}
			}

			propertyName = ReflectUtil.getBeanPropertyGetterName(method);
			if (propertyName != null) {
				Out out = method.getAnnotation(Out.class);
				ScopeData.Out oi = inspectOut(out, scopeType, propertyName, method.getReturnType());
				if (oi != null) {
					listOut.add(oi);
				}
				InOut inout = method.getAnnotation(InOut.class);
				if (inout != null) {
					if (out != null) {
						throw new MadvocException("@InOut can not be used with @Out: " + method.getDeclaringClass() + '#' + method.getName());
					}
					oi = inspectOut(inout, scopeType, propertyName, method.getReturnType());
					if (oi != null) {
						listOut.add(oi);
					}
				}

			}
		}

		if ((listIn.isEmpty()) && (listOut.isEmpty())) {
			return null;
		}

		ScopeData scopeData = new ScopeData();
		if (listIn.isEmpty() == false) {
			scopeData.in = listIn.toArray(new ScopeData.In[listIn.size()]);
		}
		if (listOut.isEmpty() == false) {
			scopeData.out = listOut.toArray(new ScopeData.Out[listOut.size()]);
		}
		return scopeData;
	}

}