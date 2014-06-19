// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

/**
 * Powerful tool for copying properties from one bean into another.
 * @see BeanVisitor
 */
public class BeanCopy extends BeanVisitor {

	protected Object destination;

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
	 * Defines excluded property names.
	 */
	public BeanCopy exclude(String... exclude) {
		excludeNames = exclude;
		return this;
	}

	/**
	 * Defines included property names.
	 */
	public BeanCopy include(String... include) {
		includeNames = include;
		return this;
	}

	/**
	 * Defines included property names as public properties
	 * of given template class.
	 */
	public BeanCopy includeAs(Class template) {
		String[] properties = getAllBeanGetterNames(template, false);

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
		if (declared) {
			BeanUtil.setDeclaredPropertySilent(destination, name, value);
		} else {
			BeanUtil.setPropertySilent(destination, name, value);
		}

		return true;
	}

}