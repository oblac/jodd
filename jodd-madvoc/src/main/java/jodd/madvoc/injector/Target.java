// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.bean.BeanUtil;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.madvoc.MadvocException;

/**
 * Injection target.
 */
public class Target {

	private static final Logger log = LoggerFactory.getLogger(Target.class);

	protected final String name;
	protected final Class type;
	protected Object value;

	/**
	 * Creates target over the value. Injection will be done into the value,
	 * hence the name and the types are irrelevant. Used for action itself
	 * and action non-annotated arguments.
	 */
	public Target(Object value) {
		this.name = null;
		this.type = null;
		this.value = value;
	}

	/**
	 * Creates target over a type with given name. Injection is actually a type conversion
	 * from input content to the given type. Used for simple annotated arguments.
	 */
	public Target(String name, Class type) {
		this.name = name;
		this.type = type;
		this.value = null;
	}

	/**
	 * Creates target over a type with the value. Injection target is the value itself.
	 * Used for bean annotated arguments.
	 */
	public Target(String name, Object value) {
		this.name = name;
		this.type = null;
		this.value = value;
	}

	/**
	 * Returns target name, if specified.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns targets type, if specified.
	 * @see #resolveType()
	 */
	public Class getType() {
		return type;
	}

	/**
	 * Resolves target type: either using {@link #getType() provided type}
	 * or type of the {@link #getValue() value}.
	 */
	public Class resolveType() {
		if (type != null) {
			return type;
		}
		return value.getClass();
	}

	/**
	 * Returns target value, if specified.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Sets target value.
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	// ---------------------------------------------------------------- write

	/**
	 * Writes value to this target.
	 */
	public void writeValue(String propertyName, Object propertyValue, boolean throwExceptionOnError) {
		if (name == null) {
			// 1) target name does not exist, inject into target value

			if (BeanUtil.hasDeclaredRootProperty(value, propertyName)) {
				try {
					BeanUtil.setDeclaredPropertyForced(value, propertyName, propertyValue);
				} catch (Exception ex) {
					if (throwExceptionOnError) {
						throw new MadvocException(ex);
					} else {
						if (log.isWarnEnabled()) {
							log.warn("Injection failed: " + propertyName + ". " + ex.toString());
						}
					}
				}
			}
		}

	}
}