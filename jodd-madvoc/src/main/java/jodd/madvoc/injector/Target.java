// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.bean.BeanUtil;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.madvoc.MadvocException;
import jodd.typeconverter.TypeConverterManager;

import java.lang.reflect.Constructor;

/**
 * Injection target.
 */
public class Target {

	private static final Logger log = LoggerFactory.getLogger(Target.class);

	protected final Class type;
	protected Object value;

	/**
	 * Creates target over the value. Injection will be done into the value,
	 * hence the name and the types are irrelevant. Used for action itself
	 * and action non-annotated arguments.
	 */
	public Target(Object value) {
		this.value = value;
		this.type = null;
	}
	public Target(Object value, Class type) {
		this.value = value;
		this.type = type;
	}

	/**
	 * Creates target over a type with given name. Injection is actually a type conversion
	 * from input content to the given type. Used for annotated arguments.
	 */
	public Target(Class type) {
		this.type = type;
		this.value = null;
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

	// ---------------------------------------------------------------- read

	/**
	 * Reads value from the target.
	 */
	public Object readValue(String propertyName) {
		if (type != null) {
			int dotNdx = propertyName.indexOf('.');

			if (dotNdx == -1) {
				return value;
			}

			propertyName = propertyName.substring(dotNdx + 1);
		}

		return BeanUtil.getDeclaredProperty(value, propertyName);
	}

	// ---------------------------------------------------------------- write

	/**
	 * Writes value to this target.
	 */
	public void writeValue(String propertyName, Object propertyValue, boolean throwExceptionOnError) {
		if (type != null) {
			// target type specified, save into target value!

			int dotNdx = propertyName.indexOf('.');

			if (dotNdx == -1) {
				try {
					value = TypeConverterManager.convertType(propertyValue, type);
				} catch (Exception ex) {
					handleException(propertyName, throwExceptionOnError, ex);
				}
				return;
			}

			if (value == null) {
				createValueInstance();
			}

			propertyName = propertyName.substring(dotNdx + 1);
		}

		// inject into target value

		if (BeanUtil.hasDeclaredRootProperty(value, propertyName)) {
			try {
				BeanUtil.setDeclaredPropertyForced(value, propertyName, propertyValue);
			} catch (Exception ex) {
				handleException(propertyName, throwExceptionOnError, ex);
			}
		}
	}

	/**
	 * Creates new instance of a type and stores it in the value.
	 */
	@SuppressWarnings({"unchecked", "NullArgumentToVariableArgMethod"})
	protected void createValueInstance() {
		try {
			Constructor ctor = type.getDeclaredConstructor(null);
			ctor.setAccessible(true);
			value = ctor.newInstance();
		} catch (Exception ex) {
			throw new MadvocException(ex);
		}
	}

	/**
	 * Deals with write exceptions.
	 */
	protected void handleException(String propertyName, boolean throwExceptionOnError, Exception ex) {
		if (throwExceptionOnError) {
			if (ex instanceof MadvocException) {
				throw (MadvocException) ex;
			}
			throw new MadvocException(ex);
		} else {
			if (log.isWarnEnabled()) {
				log.warn("Injection failed: " + propertyName + ". " + ex.toString());
			}
		}
	}
}