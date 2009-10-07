// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.injector.ScopeData;
import jodd.madvoc.ScopeType;
import jodd.madvoc.MadvocException;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.InOut;
import jodd.madvoc.meta.Out;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.util.ReflectUtil;
import jodd.util.StringUtil;
import jodd.petite.meta.PetiteInject;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

/**
 * Manager for all scope data.
 */
public class ScopeDataManager {

	@PetiteInject
	protected MadvocConfig madvocConfig;

	protected final Map<ScopeType, Map<Class, ScopeData>> scopeDataMap;
	protected final Map<ScopeType, Map<Method, ScopeData>> methodDataMap;

	public ScopeDataManager() {
		this.scopeDataMap = new EnumMap<ScopeType, Map<Class, ScopeData>>(ScopeType.class);
		this.methodDataMap = new EnumMap<ScopeType, Map<Method, ScopeData>>(ScopeType.class);
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Returns Madvoc configuration instance. Usually needed for the injectors.
	 */
	public MadvocConfig getMadvocConfig() {
		return madvocConfig;
	}

	// ---------------------------------------------------------------- method data


	/**
	 * Lookups INput data for given method and scope type.
	 * Each returned element corresponds to method parameter.
	 * Returns <code>null</code> if no data is found.
	 */
	public ScopeData.In[] lookupInMethodData(Method method, ScopeType scopeType) {
		ScopeData scopeData = lookupScopeMethodData(method, scopeType);
		if ((scopeData == null) || (scopeData.in == null)) {
			return null;
		}
		return scopeData.in;
	}

	public ScopeData.Out[] lookupOutMethodData(Method method, ScopeType scopeType) {
		ScopeData scopeData = lookupScopeMethodData(method, scopeType);
		if ((scopeData == null) || (scopeData.out == null)) {
			return null;
		}
		return scopeData.out;
	}

	protected ScopeData lookupScopeMethodData(Method method, ScopeType scopeType) {
		Map<Method, ScopeData> methodMap = methodDataMap.get(scopeType);
		if (methodMap == null) {
			methodMap = new HashMap<Method, ScopeData>();
			methodDataMap.put(scopeType, methodMap);
		}

		ScopeData sd = methodMap.get(method);
		if (sd == null) {
			sd = inspectMethodScopeData(method, scopeType);
			methodMap.put(method, sd);
		}
		return sd;
	}

	protected ScopeData inspectMethodScopeData(Method method, ScopeType scopeType) {
		Annotation[][] annotations = method.getParameterAnnotations();
		Class<?>[] types = method.getParameterTypes();
		int paramsCount = types.length;
		ScopeData sd = new ScopeData(scopeType);
		sd.in = new ScopeData.In[paramsCount];
		sd.out = new ScopeData.Out[paramsCount];
		int incount = 0, outcount = 0;
		for (int i = 0; i < paramsCount; i++) {
			Annotation[] paramAnn = annotations[i];
			Class type = types[i];
			for (Annotation annotation : paramAnn) {
				if (annotation instanceof In) {
					sd.in[i] = inspectIn((In) annotation, scopeType, StringUtil.uncapitalize(type.getSimpleName()), type);
					incount++;
				} else if (annotation instanceof Out) {
					sd.out[i] = inspectOut((Out) annotation, scopeType, StringUtil.uncapitalize(type.getSimpleName()), type);
					outcount++;
				}
			}
		}
		if (incount == 0) {
			sd.in = null;
		}
		if (outcount == 0) {
			sd.out = null;
		}
		return sd;
	}



	// ---------------------------------------------------------------- scope data

	/**
	 * Lookups scope data from scope data map.
	 */
	protected ScopeData lookupScopeData(Object action, ScopeType scopeType) {
		Map<Class, ScopeData> dataMap = scopeDataMap.get(scopeType);
		if (dataMap == null) {
			dataMap = new HashMap<Class, ScopeData>();
			scopeDataMap.put(scopeType, dataMap);
		}

		Class actionClass = action.getClass();
		ScopeData sd = dataMap.get(actionClass);
		if (sd == null) {
			sd = inspectScopeData(action, scopeType);
			dataMap.put(actionClass, sd);
		}
		return sd;
	}

	/**
	 * Lookups INput data for given object and scope type.
	 * Returns <code>null</code> if no data is found.
	 */
	public ScopeData.In[] lookupInData(Object action, ScopeType scopeType) {
		ScopeData scopeData = lookupScopeData(action, scopeType);
		if ((scopeData == null) || (scopeData.in == null)) {
			return null;
		}
		return scopeData.in;
	}

	/**
	 * Lookups OUTput data for given object and scope type.
	 * Returns <code>null</code> if no data is found.
	 */
	public ScopeData.Out[] lookupOutData(Object action, ScopeType scopeType) {
		ScopeData scopeData = lookupScopeData(action, scopeType);
		if ((scopeData == null) || (scopeData.out == null)) {
			return null;
		}
		return scopeData.out;
	}

	// ---------------------------------------------------------------- inspect

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
		ii.remove = in.remove();
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
		ii.remove = inOut.remove();
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
	 */
	protected ScopeData inspectScopeData(Object action, ScopeType scopeType) {
		ClassDescriptor cd = ClassIntrospector.lookup(action.getClass());
		Field[] fields = cd.getAllFields(true);
		Method[] methods = cd.getAllMethods(true);

		List<ScopeData.In> listIn = new ArrayList<ScopeData.In>(fields.length + methods.length);
		List<ScopeData.Out> listOut = new ArrayList<ScopeData.Out>(fields.length + methods.length);


		// fields
		for (Field field : fields) {
			In in = field.getAnnotation(In.class);
			ScopeData.In ii = inspectIn(in, scopeType, field.getName(), field.getType());
			if (ii != null) {
				listIn.add(ii);
			}
			InOut inout = field.getAnnotation(InOut.class);
			if (inout != null) {
				if (in != null) {
					throw new MadvocException("InOut field annotation cannot be used together with In: " + field.getDeclaringClass() + '#' + field.getName());
				}
				ii = inspectIn(inout, scopeType, field.getName(), field.getType());
				if (ii != null) {
					listIn.add(ii);
				}
			}

			Out out = field.getAnnotation(Out.class);
			ScopeData.Out oi = inspectOut(out, scopeType, field.getName(), field.getType());
			if (oi != null) {
				listOut.add(oi);
			}
			inout = field.getAnnotation(InOut.class);
			if (inout != null) {
				if (out != null) {
					throw new MadvocException("InOut field annotation cannot be used together with Out: " + field.getDeclaringClass() + '#' + field.getName());
				}
				oi = inspectOut(inout, scopeType, field.getName(), field.getType());
				if (oi != null) {
					listOut.add(oi);
				}
			}
		}

		// methods
		for (Method method : methods) {
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
						throw new MadvocException("InOut method annotation cannot be used together with In: " + method.getDeclaringClass() + '#' + method.getName() + "()");
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
						throw new MadvocException("InOut method annotation cannot be used together with Out: " + method.getDeclaringClass() + '#' + method.getName() + "()");
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

		ScopeData scopeData = new ScopeData(scopeType);
		if (listIn.isEmpty() == false) {
			scopeData.in = listIn.toArray(new ScopeData.In[listIn.size()]);
		}
		if (listOut.isEmpty() == false) {
			scopeData.out = listOut.toArray(new ScopeData.Out[listOut.size()]);
		}
		return scopeData;
	}
}