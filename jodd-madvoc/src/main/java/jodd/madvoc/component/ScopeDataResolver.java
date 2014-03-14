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

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * Collection of {@link jodd.madvoc.ScopeData scope data} of certain type.
 * For each action class and action method it holds an array of ScopeData objects.
 * Each element of that array represents data for one ScopeType.
 * Some elements might be <code>null</code> as well.
 */
public class ScopeDataResolver {

	private static final ScopeData[] EMPTY_SCOPEDATA = new ScopeData[0];

	protected Map<Object, ScopeData[]> scopeMap = new HashMap<Object, ScopeData[]>();

	// ---------------------------------------------------------------- bean

	/**
	 * Lookups for {@link jodd.madvoc.ScopeData.In} data for given type and scope type.
	 */
	public ScopeData.In[] lookupInData(Class type, ScopeType scopeType) {
		ScopeData[] scopeData = lookupScopeData(type);
		if (scopeData == null) {
			return null;
		}

		ScopeData sd = scopeData[scopeType.value()];
		if (sd == null) {
			return null;
		}

		return sd.in;
	}

	/**
	 * Lookups for {@link jodd.madvoc.ScopeData.Out} data for given type and scope type.
	 */
	public ScopeData.Out[] lookupOutData(Class type, ScopeType scopeType) {
		ScopeData[] scopeData = lookupScopeData(type);
		if (scopeData == null) {
			return null;
		}

		ScopeData sd = scopeData[scopeType.value()];
		if (sd == null) {
			return null;
		}

		return sd.out;
	}

	/**
	 * Lookups for cached scope data of all scope types. Returns <code>null</code>
	 * if no scope data exist.
	 */
	public ScopeData[] lookupScopeData(Class type) {
		ScopeData[] scopeData = scopeMap.get(type);

		if (scopeData == null) {
			scopeData = inspectScopeData(type);

			scopeMap.put(type, scopeData);
		}

		if (scopeData.length == 0) {
			return null;
		}

		return scopeData;
	}

	// ---------------------------------------------------------------- common

	/**
	 * Inspects and returns scope data for all available scopes.
	 */
	protected ScopeData[] inspectScopeData(Object key) {
		final ScopeType[] allScopeTypes = ScopeType.values();

		ScopeData[] scopeData = new ScopeData[allScopeTypes.length];

		int count = 0;
		if (key instanceof Class) {
			for (ScopeType scopeType : allScopeTypes) {
				ScopeData sd = inspectClassScopeData((Class) key, scopeType);
				if (sd != null) {
					count++;
				}
				scopeData[scopeType.value()] = sd;
			}
		}

		/*else if (key instanceof Method) {
			for (ScopeType scopeType : allScopeTypes) {
				ScopeData sd = inspectMethodScopeData((Method) key, scopeType);
				if (sd != null) {
					count++;
				}
				scopeData[scopeType.value()] = sd;
			}
		} */
		else {
			throw new MadvocException("Invalid type: " + key);
		}
		if (count == 0) {
			scopeData = EMPTY_SCOPEDATA;
		}

		return scopeData;
	}


	// ---------------------------------------------------------------- inspect method

	/**
	 * Inspects all method parameters for scope data.
	 */
/*	protected ScopeData inspectMethodScopeData(Method method, ScopeType scopeType) {

		String[] methodParameterNames = actionParameterNamesResolver.resolveActionParameterNames(method);

		Annotation[][] annotations = method.getParameterAnnotations();
		Class<?>[] types = method.getParameterTypes();

		int paramsCount = types.length;

		ScopeData sd = new ScopeData();
		sd.in = new ScopeData.In[paramsCount];
		sd.out = new ScopeData.Out[paramsCount];

		int incount = 0, outcount = 0;

		for (int i = 0; i < paramsCount; i++) {
			Annotation[] paramAnnotations = annotations[i];

			Class type = types[i];
			String name = methodParameterNames[i];

			for (Annotation annotation : paramAnnotations) {

				if (annotation instanceof In) {
					sd.in[i] = inspectIn((In) annotation, scopeType, name, type);
					if (sd.in[i] != null) {
						incount++;
					}
				} else if (annotation instanceof Out) {
					sd.out[i] = inspectOut((Out) annotation, scopeType, StringUtil.uncapitalize(type.getSimpleName()), type);
					if (sd.out[i] != null) {
						outcount++;
					}
				}
			}
		}

		if (incount == 0 && outcount == 0) {
			return null;
		}
		if (incount == 0) {
			sd.in = null;
		}
		if (outcount == 0) {
			sd.out = null;
		}
		return sd;
	}
*/

	// ---------------------------------------------------------------- inspect class

	/**
	 * Fills value and property name.
	 */
	protected void fillNameTarget(ScopeData.In ii, String value, String propertyName) {
		value = value.trim();
		if (value.length() > 0) {
			ii.name = value;
			ii.target = propertyName;
		} else {
			ii.name = propertyName;
			ii.target = null;
		}
	}

	/**
	 * Fills value and property name.
	 */
	protected void fillNameTarget(ScopeData.Out oi, String value, String propertyName) {
		value = value.trim();
		if (value.length() > 0) {
			oi.name = value;
			oi.target = propertyName;
		} else {
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
		fillNameTarget(ii, in.value(), propertyName);
		ii.type = propertyType;
		ii.create = in.create();
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
		fillNameTarget(ii, inOut.value(), propertyName);
		ii.type = propertyType;
		ii.create = inOut.create();
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
		fillNameTarget(oi, out.value(), propertyName);
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
		fillNameTarget(oi, inOut.value(), propertyName);
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