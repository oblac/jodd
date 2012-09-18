// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

/**
 * Proxetta creates dynamic proxy classes in the run-time.
 * <p>
 * To wrap a class with proxy Proxetta needs a target class (or its name or <code>InputStream</code>)
 * and one or more {@link jodd.proxetta.ProxyAspect proxy aspects} that will be applied to target.
 * Proxetta will examine target class and check if there are any methods to wrap, as defined by aspects pointcut.
 * If there is at least one matched method, new proxy class will be created that extends target class.
 * <p>
 * If no matching method founded, Proxetta may or may not create an empty proxy class.
 * This behaviour is defined by <b>forced</b> mode during creation.
 * <p>
 * There are several options that describes how proxy class will be named. By default,
 * proxy class name is created from target class name by adding default suffix. Suffix
 * can be changed, also, name can be variable, so each time class is created it will have a new name.
 * <p>
 * It is also possible to set proxy simple class name and/or package name. This is useful when
 * proxyfing JDK classes or any other that can't be loaded by some classloader. Requested proxy name
 * can be in the following forms:
 * <li> .Foo (starting with a dot) - proxy package name is equal to target package, just proxy simple class name is set.
 * <li> foo. (ending with a dot) - proxy package is set, proxy simple name is create from target simple class name.
 * <li> foo.Foo - full proxy class name is specified.
 *
 * @see ProxettaBuilder
 */
public abstract class Proxetta {

	// ---------------------------------------------------------------- properties

	protected boolean forced;
	protected ClassLoader classLoader;
	protected boolean variableClassName;
	protected String classNameSuffix;
	protected String debugFolder;

	/**
	 * Specifies 'forced' mode. If <code>true</code>, new proxy class will be created even if there are no
	 * matching pointcuts. If <code>false</code>, new proxy class will be created only if there is at least one
	 * matching pointcut - otherwise, original class will be returned.
	 */
	public void setForced(boolean forced) {
		this.forced = forced;
	}

	public boolean isForced() {
		return forced;
	}


	/**
	 * Specifies classloaders for loading created classes..
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}


	/**
	 * Sets variable proxy class name so every time when new proxy class is created
	 * its name will be different,so one classloader may load it without a problem.
	 * Otherwise, sets constant proxy class name so each time created proxy
	 * class will have the same name. Such class can be loaded only once by a classloader.
	 * <p>
	 * This prevents "<code>java.lang.LinkageError: duplicate class definition</code>" errors.
	 */
	public void setVariableClassName(boolean variableClassName) {
		this.variableClassName = variableClassName;
	}

	public boolean isVariableClassName() {
		return variableClassName;
	}


	/**
	 * Specifies custom classname suffix to be added to the class name of created proxy.
	 * Warning: when class name suffix is not used, full classname has to be
	 * specified that differs from target class name.
	 */
	public void setClassNameSuffix(String suffix) {
		this.classNameSuffix = suffix;
	}

	public String getClassNameSuffix() {
		return classNameSuffix;
	}

	/**
	 * Specifies the debug folder where all created classes will be
	 * written to, for debugging purposes.
	 */
	public void setDebugFolder(String debugFolder) {
		this.debugFolder = debugFolder;
	}

	public String getDebugFolder() {
		return debugFolder;
	}

	// ---------------------------------------------------------------- builder

	/**
	 * Creates {@link ProxettaBuilder} with current options.
	 */
	public abstract ProxettaBuilder builder();

}