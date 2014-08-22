// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

/**
 * Powerful tool for copying properties from one bean into another.
 * @see BeanVisitor
 */
public class BeanCopy extends BeanVisitor {

	protected Object destination;
	protected boolean declaredTarget;

	// ---------------------------------------------------------------- ctor

	public BeanCopy(Object source, Object destination) {
		this.source = source;
		this.destination = destination;
	}

	/**
	 * Static factory.
	 */
	public static BeanCopy beans(Object source, Object destination) {
		return new BeanCopy(source, destination);
	}

	// ---------------------------------------------------------------- properties

	/**
	 * Excludes all properties, i.e. enables blacklist mode.
	 */
	public BeanCopy excludeAll() {
		blacklist = false;
		return this;
	}

	/**
	 * Defines excluded property names.
	 */
	public BeanCopy exclude(String... excludes) {
		for (String ex : excludes) {
			rules.exclude(ex);
		}
		return this;
	}

	/**
	 * Exclude a property.
	 */
	public BeanCopy exclude(String exclude) {
		rules.exclude(exclude);
		return this;
	}

	/**
	 * Defines included property names.
	 */
	public BeanCopy include(String... includes) {
		for (String in : includes) {
			rules.include(in);
		}
		return this;
	}

	/**
	 * Include a property.
	 */
	public BeanCopy include(String include) {
		rules.include(include);
		return this;
	}

	/**
	 * Defines included property names as public properties
	 * of given template class. Sets to black list mode.
	 */
	public BeanCopy includeAs(Class template) {
		blacklist = false;

		String[] properties = getAllBeanPropertyNames(template, false);

		include(properties);

		return this;
	}

	/**
	 * Defines if <code>null</code> values should be ignored.
	 */
	public BeanCopy ignoreNulls(boolean ignoreNulls) {
		this.ignoreNullValues = ignoreNulls;

		return this;
	}

	/**
	 * Defines if all properties should be copied (when set to <code>true</code>)
	 * or only public (when set to <code>false</code>, default).
	 */
	public BeanCopy declared(boolean declared) {
		this.declared = declared;
		this.declaredTarget = declared;
		return this;
	}

	/**
	 * Fine-tuning of the declared behaviour.
	 */
	public BeanCopy declared(boolean declaredSource, boolean declaredTarget) {
		this.declared = declaredSource;
		this.declaredTarget = declaredTarget;
		return this;
	}

	/**
	 * Defines if fields without getters should be copied too.
	 */
	public BeanCopy includeFields(boolean includeFields) {
		this.includeFields = includeFields;
		return this;
	}

	// ---------------------------------------------------------------- visitor

	/**
	 * Performs the copying.
	 */
	public void copy() {
		visit();
	}

	/**
	 * Copies single property to the destination.
	 * Exceptions are ignored, so copying continues if
	 * destination does not have some of the sources properties.
	 */
	@Override
	protected boolean visitProperty(String name, Object value) {
		if (declaredTarget) {
			BeanUtil.setDeclaredPropertySilent(destination, name, value);
		} else {
			BeanUtil.setPropertySilent(destination, name, value);
		}

		return true;
	}

}