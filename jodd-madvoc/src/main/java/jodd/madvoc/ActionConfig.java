// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.result.Result;
import jodd.util.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Action configuration and shared run-time data, used internally.
 */
public class ActionConfig {

	// configuration
	public final Class actionClass;
	public final Method actionClassMethod;
	public final String actionPath;
	public final String actionMethod;
	public final Field resultField;
	public final boolean async;

	// scope data information matrix: [scope-type][target-index][founded ins/outs]
	public final ScopeData.In[][][] ins;
	public final ScopeData.Out[][][] outs;

	public final boolean hasArguments;
	//public final String[] actionPathElements;

	// run-time data
	protected ActionConfigSet actionConfigSet;
	public final ActionFilter[] filters;
	public final ActionInterceptor[] interceptors;

	public ActionConfig(
			Class actionClass,
			Method actionClassMethod,
			ActionFilter[] filters,
			ActionInterceptor[] interceptors,
			String actionPath,
			String actionMethod,
			boolean async,
			ScopeData.In[][][] ins,
			ScopeData.Out[][][] outs)
	{
		this.actionClass = actionClass;
		this.actionClassMethod = actionClassMethod;
		this.actionPath = actionPath;
		this.actionMethod = actionMethod;
		this.hasArguments = actionClassMethod.getParameterTypes().length != 0;
		this.async = async;

		this.ins = ins;
		this.outs = outs;

		this.filters = filters;
		this.interceptors = interceptors;

		this.resultField = findResultField(actionClass);
	}

	// ---------------------------------------------------------------- result

	/**
	 * Finds result field in the action class.
	 */
	protected Field findResultField(Class actionClass) {
		FieldDescriptor[] fields = ClassIntrospector.lookup(actionClass).getAllFieldDescriptors();
		for (FieldDescriptor fd : fields) {
			Field field = fd.getField();
			if (ReflectUtil.isSubclass(field.getType(), Result.class)) {
				field.setAccessible(true);
				return field;
			}
		}
		return null;
	}

	// ---------------------------------------------------------------- getters

	/**
	 * Returns action class.
	 */
	public Class getActionClass() {
		return actionClass;
	}

	/**
	 * Returns action class method.
	 */
	public Method getActionClassMethod() {
		return actionClassMethod;
	}

	/**
	 * Returns action path.
	 */
	public String getActionPath() {
		return actionPath;
	}

	/**
	 * Returns action method.
	 */
	public String getActionMethod() {
		return actionMethod;
	}

	/**
	 * Returns interceptor instances.
	 */
	public ActionInterceptor[] getInterceptors() {
		return interceptors;
	}

	/**
	 * Returns <code>true</code> if action is asynchronous.
	 */
	public boolean isAsync() {
		return async;
	}

	public ActionConfigSet getActionConfigSet() {
		return actionConfigSet;
	}


	// ---------------------------------------------------------------- to string

	/**
	 * Returns action string in form 'actionClass#actionMethod'.
	 */
	public String getActionString() {
		return actionClass.getName() + '#' + actionClassMethod.getName();
	}

	@Override
	public String toString() {
		return "action: " + actionPath + (actionMethod == null ? "" : '#' + actionMethod) + "  -->  " + getActionString();
	}

}