// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import java.io.InputStream;

import jodd.JoddDefault;
import jodd.proxetta.asm.ClassProcessor;
import jodd.proxetta.impl.InvokeProxetta;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.util.ClassLoaderUtil;

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
 */
public abstract class Proxetta {

	/**
	 * Specifies aspects for the target and creates Proxetta instance.
	 */
	public static Proxetta withAspects(ProxyAspect... aspects) {
		return new ProxyProxetta(aspects);
	}

	/**
	 * Specifies invoke replacement aspects and creates Proxetta instance.
	 */
	public static Proxetta withAspects(InvokeAspect... aspects) {
		return new InvokeProxetta(aspects);
	}

	// ---------------------------------------------------------------- forced

	protected boolean forced;

	/**
	 * Specifies 'forced' mode. If <code>true</code>, new proxy class will be created even if there are no
	 * matching pointcuts. If <code>false</code>, new proxy class will be created only if there is at least one
	 * matching pointcut - otherwise, original class will be returned.
	 */
	public Proxetta forced(boolean forced) {
		this.forced = forced;
		return this;
	}

	// ---------------------------------------------------------------- class loader

	protected ClassLoader classLoader;

	/**
	 * Specifies classloaders for use.
	 */
	public Proxetta loadsWith(ClassLoader classLoader) {
		this.classLoader = classLoader;
		return this;
	}

	// ---------------------------------------------------------------- var name

	protected boolean variableClassName;

	/**
	 * Sets variable proxy class name so every time when new proxy class is created its name will be different,
	 * so one classloader may load it without a problem.
	 */
	public Proxetta variableClassName() {
		variableClassName = true;
		return this;
	}

	/**
	 * Sets constant proxy class name so each time created proxy class will have the same name.
	 * Such class can be loaded only once by a classloader.
	 */
	public Proxetta constantClassName() {
		variableClassName = false;
		return this;
	}


	// ---------------------------------------------------------------- suffix

	protected String classNameSuffix;

	/**
	 * Specifies custom classname suffix to be added to the class name of created proxy.
	 */
	public Proxetta useClassNameSuffix(String suffix) {
		this.classNameSuffix = suffix;
		return this;
	}

	/**
	 * Specifies not to append class name suffix when creating proxy class.
	 * Warning: when class name suffix is not used, full classname has to be
	 * specified that differs from target class name.
	 */
	public Proxetta dontUseClassNameSuffix() {
		this.classNameSuffix = null;
		return this;
	}

	// ---------------------------------------------------------------- ProxyCreator

	/**
	 * Creates {@link jodd.proxetta.asm.ProxettaCreator} with current options.
	 */
	protected abstract ClassProcessor createClassProcessor();

	/**
	 * {@link #createClassProcessor() Creates} and prepares class processor.
	 */
	protected ClassProcessor prepareClassProcessor() {
		ClassProcessor cp = createClassProcessor();
		cp.setUseVariableClassName(variableClassName);
		cp.setClassNameSuffix(classNameSuffix);
		return cp;
	}

	// ----------------------------------------------------------------  create

	/**
	 * Generates proxy bytecode for provided class. Returns <code>null</code> if
	 * there was no matching pointcuts and forced mode is off.
	 */
	public byte[] createProxy(Class target) {
		return createProxy(target, null);
	}

	public byte[] createProxy(Class target, String proxyClassName) {
		return createProxy(prepareClassProcessor().accept(target, proxyClassName));
	}

	/**
	 * Generates proxy bytecode for provided class. Returns <code>null</code> if
	 * there was no matching pointcuts and forced mode is off.
	 */
	public byte[] createProxy(String targetName) {
		return createProxy(targetName, null);
	}

	public byte[] createProxy(String targetName, String proxyClassName) {
		return createProxy(prepareClassProcessor().accept(targetName, proxyClassName));
	}


	/**
	 * Generates proxy bytecode for class provided as <code>InputStream</code>. Returns <code>null</code> if
	 * there was no matching pointcuts and forced mode is off.
	 */
	public byte[] createProxy(InputStream in) {
		return createProxy(in, null);
	}

	public byte[] createProxy(InputStream in, String proxyClassName) {
		return createProxy(prepareClassProcessor().accept(in, proxyClassName));
	}

	/**
	 * Returns byte array of invoked proxetta creator.
	 */
	protected byte[] createProxy(ClassProcessor cp) {
		byte[] result = cp.toByteArray();
		if ((forced == false) && (cp.isProxyApplied() == false)) {
			return null;
		}
		return result;
	}


	// ---------------------------------------------------------------- define

	/**
	 * Defines new proxy class.
	 */
	public Class defineProxy(Class target) {
		return defineProxy(target, null);
	}

	public Class defineProxy(Class target, String proxyClassName) {
		ClassProcessor cp = prepareClassProcessor();
		cp.accept(target, proxyClassName);
		if ((forced == false) && (cp.isProxyApplied() == false)) {
			return target;
		}
		try {
			if (classLoader == null) {
				ClassLoader cl = target.getClassLoader();
				if (cl == null) {
					cl = JoddDefault.classLoader;
				}
				return ClassLoaderUtil.defineClass(cp.getProxyClassName(), cp.toByteArray(), cl);
			}
			return ClassLoaderUtil.defineClass(cp.getProxyClassName(), cp.toByteArray(), classLoader);
		} catch (Exception ex) {
			throw new ProxettaException("Proxy class definition was unsuccessful.", ex);
		}
	}

	/**
	 * Defines new proxy class.
	 */
	public Class defineProxy(String targetName) {
	    return defineProxy(targetName, null);
	}

	public Class defineProxy(String targetName, String proxyClassName) {
		ClassProcessor cp = prepareClassProcessor();
		cp.accept(targetName, proxyClassName);
		if ((forced == false) && (cp.isProxyApplied() == false)) {
			try {
				return ClassLoaderUtil.loadClass(targetName, Proxetta.class);
			} catch (ClassNotFoundException cnfex) {
				throw new ProxettaException(cnfex);
			}
		}
		try {
			if (classLoader == null) {
				return ClassLoaderUtil.defineClass(cp.getProxyClassName(), cp.toByteArray());
			}
			return ClassLoaderUtil.defineClass(cp.getProxyClassName(), cp.toByteArray(), classLoader);
		} catch (Exception ex) {
			throw new ProxettaException("Proxy class definition was unsuccessful.", ex);
		}
	}

	// ---------------------------------------------------------------- instance

	public <T> T createProxyInstance(Class<T> target) {
		return createProxyInstance(target, null);
	}

	@SuppressWarnings({"unchecked"})
	public <T> T createProxyInstance(Class<T> target, String proxyClassName) {
		Class<T> c = defineProxy(target, proxyClassName);
		try {
			return c.newInstance();
		} catch (Exception ex) {
			throw new ProxettaException("Unable to create new proxy instance.", ex);
		}
	}

	public Object createProxyInstance(String targetName) {
		return createProxyInstance(targetName, null);
	}
	public Object createProxyInstance(String targetName, String proxyClassName) {
		Class c = defineProxy(targetName, proxyClassName);
		try {
			return c.newInstance();
		} catch (Exception ex) {
			throw new ProxettaException("Unable to create new proxy instance.", ex);
		}
	}

}
