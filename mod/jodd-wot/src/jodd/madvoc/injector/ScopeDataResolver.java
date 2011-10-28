// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ScopeType;
import jodd.madvoc.MadvocException;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.InOut;
import jodd.madvoc.meta.Out;
import jodd.paramo.MethodParameter;
import jodd.paramo.Paramo;
import jodd.paramo.ParamoException;
import jodd.util.ReflectUtil;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.annotation.Annotation;

/**
 * Collection of {@link ScopeData scope data} of certain type.
 * For each action class and action method it holds an array of ScopeData objects.
 * Each element of that array represents data for one ScopeType.
 * Some elements might be null as well.
 */
public class ScopeDataResolver {

	private static final ScopeData[] EMPTY_SCOPEDATA = new ScopeData[0]; 

	protected Map<Object, ScopeData[]> scopeMap = new HashMap<Object, ScopeData[]>();

	// ---------------------------------------------------------------- main

	/**
	 * Lookups INput data for given object and scope type.
	 * Returns <code>null</code> if no data is found.
	 */
	public ScopeData.In[] lookupInData(Class actionClass, ScopeType scopeType) {
		return lookupIn(actionClass, scopeType);
	}
	/**
	 * Lookups INput data for given method and scope type.
	 * Returns <code>null</code> if no data is found.
	 */
	public ScopeData.In[] lookupInData(Method actionMethod, ScopeType scopeType) {
		return lookupIn(actionMethod, scopeType);
	}

	protected ScopeData.In[] lookupIn(Object key, ScopeType scopeType) {
		ScopeData[] scopeData = scopeMap.get(key);
		if (scopeData == null) {
			scopeData = inspectAllScopeData(key);
		}
		if (scopeData.length == 0) {
			return null;
		}
		ScopeData sd = scopeData[scopeType.value()];
		if (sd == null) {
			return null;
		}
		return sd.in;
	}

	/**
	 * Lookups OUTput data for given object and scope type.
	 * Returns <code>null</code> if no data is found.
	 */
	public ScopeData.Out[] lookupOutData(Class actionClass, ScopeType scopeType) {
		ScopeData[] scopeData = scopeMap.get(actionClass);
		if (scopeData == null) {
			scopeData = inspectAllScopeData(actionClass);
		}
		if (scopeData.length == 0) {
			return null;
		}
		ScopeData sd = scopeData[scopeType.value()];
		if (sd == null) {
			return null;
		}
		return sd.out;
	}

	/**
	 * Inspects and returns scope data for all availiable scopes.
	 */
	protected ScopeData[] inspectAllScopeData(Object key) {
		ScopeData[] scopeData;
		ScopeType[] allScopeTypes = ScopeType.values();
		scopeData = new ScopeData[allScopeTypes.length];
		int count = 0;
		if (key instanceof Class) {
			for (ScopeType st : allScopeTypes) {
				ScopeData sd = inspectScopeData((Class) key, st);
				if (sd != null) {
					count++;
				}
				scopeData[st.value()] = sd;
			}
		} else if (key instanceof Method) {
			for (ScopeType st : allScopeTypes) {
				ScopeData sd = inspectMethodScopeData((Method) key, st);
				if (sd != null) {
					count++;
				}
				scopeData[st.value()] = sd;
			}
		} else {
			throw new MadvocException("IN data are available only for Class and Method.");
		}
		if (count == 0) {
			scopeData = EMPTY_SCOPEDATA;
		}
		scopeMap.put(key, scopeData);
		return scopeData;
	}


	// ---------------------------------------------------------------- method data

	/**
	 * Inspects all method parameters for scope data.
	 */
	protected ScopeData inspectMethodScopeData(Method method, ScopeType scopeType) {
		Annotation[][] annotations = method.getParameterAnnotations();
		Class<?>[] types = method.getParameterTypes();
		MethodParameter[] methodParameters;
		try {
			methodParameters = Paramo.resolveParameters(method);
		} catch (ParamoException pex) {
			methodParameters = null;
		}

		int paramsCount = types.length;
		ScopeData sd = new ScopeData();
		sd.in = new ScopeData.In[paramsCount];
		sd.out = null;//new ScopeData.Out[paramsCount];
		int incount = 0;//, outcount = 0;
		for (int i = 0; i < paramsCount; i++) {
			Annotation[] paramAnn = annotations[i];
			Class type = types[i];
			String name = methodParameters != null ? methodParameters[i].getName() : null;
			boolean hasAnnotation = false;
			for (Annotation annotation : paramAnn) {
				if (annotation instanceof In) {
					sd.in[i] = inspectIn((In) annotation, scopeType, name, type);
					if (sd.in[i] != null) {
						incount++;
					}
					hasAnnotation = true;
				}/* else if (annotation instanceof Out) {
					sd.out[i] = inspectOut((Out) annotation, scopeType, StringUtil.uncapitalize(type.getSimpleName()), type);
					outcount++;
				}*/

			}
			// annotations not available, treat it as request scope type
			if ((hasAnnotation == false) && (scopeType == ScopeType.REQUEST) && (methodParameters != null)) {
				sd.in[i] = defaultRequestScopeIn(scopeType, name, type);
				incount++;
			}
		}
		if (incount == 0) {
			sd.in = null;
		}
/*
		if (outcount == 0) {
			sd.out = null;
		}
*/
		return sd;
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
	 * Creates default request scope IN data for a method parameter.
	 */
	protected ScopeData.In defaultRequestScopeIn(ScopeType scopeType, String paramName, Class propertyType) {
		if (scopeType != ScopeType.REQUEST) {
			return null;
		}
		ScopeData.In ii = new ScopeData.In();
		ii.name = paramName;
		ii.target = null;
		ii.type = propertyType;
		ii.create = false;
		ii.remove = false;
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
	 * Returns <code>null</code> if there are no In and Out data.
	 */
	protected ScopeData inspectScopeData(Class actionClass, ScopeType scopeType) {
		ClassDescriptor cd = ClassIntrospector.lookup(actionClass);
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
