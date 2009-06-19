// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import java.io.InputStream;
import jodd.util.ClassLoaderUtil;
import jodd.proxetta.asm.ProxettaCreator;
import jodd.exception.ExceptionUtil;

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
 */
public class Proxetta {

	protected final ProxyAspect[] aspects;

	public Proxetta(ProxyAspect... aspects) {
		this.aspects = aspects;
	}

	/**
	 * Specifies aspects for the target and creates Proxetta instance.
	 */
	public static Proxetta withAspects(ProxyAspect... aspects) {
		return new Proxetta(aspects);
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

	// ---------------------------------------------------------------- ProxyCreator

	/**
	 * Creates {@link jodd.proxetta.asm.ProxettaCreator} with current options.
	 */
	protected ProxettaCreator createProxettaCreator() {
		ProxettaCreator pc = new ProxettaCreator(this.aspects);
		pc.setUseVariableClassName(variableClassName);
		return pc;
	}

	// ----------------------------------------------------------------  create

	/**
	 * Generates proxy bytecode for provided class. Returns <code>null</code> if
	 * there was no matching pointcuts and forced mode is off.
	 */
	public byte[] createProxy(Class target) {
		return createProxy(createProxettaCreator().accept(target));
	}

	/**
	 * Generates proxy bytecode for provided class. Returns <code>null</code> if
	 * there was no matching pointcuts and forced mode is off.
	 */
	public byte[] createProxy(String targetName) {
		return createProxy(createProxettaCreator().accept(targetName));
	}


	/**
	 * Generates proxy bytecode for class provided as <code>InputStream</code>. Returns <code>null</code> if
	 * there was no matching pointcuts and forced mode is off.
	 */
	public byte[] createProxy(InputStream in) {
		return createProxy(createProxettaCreator().accept(in));
	}

	/**
	 * Returns byte array of invoked proxetta creator.
	 */
	protected byte[] createProxy(ProxettaCreator pc) {
		byte[] result = pc.toByteArray();
		if ((forced == false) && (pc.isProxyApplied() == false)) {
			return null;
		}
		return result;
	}


	// ---------------------------------------------------------------- define

	/**
	 * Defines new proxy class.
	 */
	public Class defineProxy(Class target) {
		ProxettaCreator pc = createProxettaCreator();
		pc.accept(target);
		if ((forced == false) && (pc.isProxyApplied() == false)) {
			return target;
		}
		try {
			if (classLoader == null) {
				return ClassLoaderUtil.defineClass(pc.getProxyClassName(), pc.toByteArray());
			}
			return ClassLoaderUtil.defineClass(pc.getProxyClassName(), pc.toByteArray(), classLoader);
		} catch (RuntimeException rex) {
			ClassFormatError cause = ExceptionUtil.findCause(rex, ClassFormatError.class);
			if (cause == null) {
				throw rex;
			} else {
				throw new ProxettaException("Proxy creation was unsuccessful due to possible bug in Proxetta.", rex);
			}
		}
	}

	/**
	 * Defines new proxy class.
	 */
	public Class defineProxy(String targetName) {
		ProxettaCreator pc = createProxettaCreator();
		pc.accept(targetName);
		if ((forced == false) && (pc.isProxyApplied() == false)) {
			try {
				return ClassLoaderUtil.loadClass(targetName, Proxetta.class);
			} catch (ClassNotFoundException cnfex) {
				throw new ProxettaException(cnfex);
			}
		}
		if (classLoader == null) {
			return ClassLoaderUtil.defineClass(pc.getProxyClassName(), pc.toByteArray());
		}
		return ClassLoaderUtil.defineClass(pc.getProxyClassName(), pc.toByteArray(), classLoader);
	}

	// ---------------------------------------------------------------- instance

	@SuppressWarnings({"unchecked"})
	public <T> T createProxyInstance(Class<T> target) {
		Class<T> c = defineProxy(target);
		try {
			return c.newInstance();
		} catch (Exception ex) {
			throw new ProxettaException("Unable to create proxy instance.", ex);
		}
	}

	public Object createProxyInstance(String targetName) {
		Class c = defineProxy(targetName);
		try {
			return c.newInstance();
		} catch (Exception ex) {
			throw new ProxettaException("Unable to create proxy instance.", ex);
		}
	}

}
