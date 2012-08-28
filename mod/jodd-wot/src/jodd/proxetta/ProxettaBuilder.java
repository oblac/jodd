// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.log.Log;
import jodd.proxetta.asm.TargetClassInfoReader;
import jodd.proxetta.asm.WorkData;
import jodd.util.StringUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import jodd.util.ClassLoaderUtil;
import jodd.io.StreamUtil;

import java.io.InputStream;
import java.io.IOException;

/**
 * Proxetta builder. While {@link Proxetta} only holds aspects and
 * configuration, <code>ProxettaBuilder</code> deals with the
 * actually building proxies and wrappers over provided target.
 */
public abstract class ProxettaBuilder {

	private static final Log log = Log.getLogger(ProxettaBuilder.class);

	protected final Proxetta proxetta;

	/**
	 * Creates new builder.
	 */
	protected ProxettaBuilder(Proxetta proxetta) {
		this.proxetta = proxetta;
	}
	// ---------------------------------------------------------------- IN

	/**
	 * Main target source.
	 */
	private InputStream targetInputStream;

	/**
	 * Target class, when available.
	 */
	private Class targetClass;

	/**
	 * Target class name, when available.
	 */
	private String targetClassName;

	/**
	 * Requested proxy class name (or class name template).
	 */
	protected String requestedProxyClassName;

	/**
	 * Sets requested proxy class name.
	 */
	public void setTargetProxyClassName(String targetProxyClassName) {
		this.requestedProxyClassName = targetProxyClassName;
	}

	// ---------------------------------------------------------------- IN targets

	/**
	 * Defines class input stream as a target.
	 */
	protected void setTarget(InputStream target) {
		checkTarget();

		targetInputStream = target;
		targetClass = null;
		targetClassName = null;
	}

	/**
	 * Defines class name as a target.
	 * Class will not be loaded by classloader!
	 */
	protected void setTarget(String targetName) {
		checkTarget();

		try {
			targetInputStream = ClassLoaderUtil.getClassAsStream(targetName);
			targetClassName = targetName;
			targetClass = null;
		} catch (IOException ioex) {
			StreamUtil.close(targetInputStream);
			throw new ProxettaException("Unable to stream class name: " + targetName, ioex);
		}
	}

	/**
	 * Defines class as a target.
	 */
	protected void setTarget(Class target) {
		checkTarget();

		try {
			targetInputStream = ClassLoaderUtil.getClassAsStream(target);
			targetClass = target;
			targetClassName = target.getName();
		} catch (IOException ioex) {
			StreamUtil.close(targetInputStream);
			throw new ProxettaException("Unable to stream class: " + target.getName(), ioex);
		}
	}

	/**
	 * Checks if target is not defined yet.
	 */
	private void checkTarget() {
		if (targetInputStream != null) {
			throw new ProxettaException("Target already defined");
		}

	}

	// ---------------------------------------------------------------- IN naming

	/**
	 * Number appended to proxy class name, incremented on each use to make classnames unique
	 * in the system (e.g. classloader).
	 *
	 * @see Proxetta#setVariableClassName(boolean)
 	 */
	protected static int suffixCounter;


	/**
	 * Returns new suffix or <code>null</code> if suffix is not in use.
	 */
	protected String resolveClassNameSuffix() {
		String classNameSuffix = proxetta.getClassNameSuffix();

		if (classNameSuffix == null) {
			return null;
		}

		if (proxetta.isVariableClassName() == false) {
			return classNameSuffix;
		}

		suffixCounter++;
		return classNameSuffix + suffixCounter;
	}

	// ---------------------------------------------------------------- PROCESS

	/**
	 * Creates custom class builder and process the target class with it.
	 */
	protected abstract WorkData process(ClassReader cr, TargetClassInfoReader targetClassInfoReader);

	// ---------------------------------------------------------------- ACCEPT

	protected ClassWriter destClassWriter;			// destination class writer
	protected boolean proxyApplied;
	protected String proxyClassName;

	/**
	 * Reads the target and creates destination class.
	 */
	protected void process() {
		if (targetInputStream == null) {
			throw new ProxettaException("Target not defined");
		}
		// create class reader
		ClassReader classReader;
		try {
			classReader = new ClassReader(targetInputStream);
		} catch (IOException ioex) {
			throw new ProxettaException("Error reading class input stream.", ioex);
		}

		// reads information
		TargetClassInfoReader targetClassInfoReader = new TargetClassInfoReader();
		classReader.accept(targetClassInfoReader, 0);

		this.destClassWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

		// create proxy
		if (log.isDebugEnabled()) {
			log.debug("processing: " + classReader.getClassName());
		}
		WorkData wd = process(classReader, targetClassInfoReader);

		// store important data
		proxyApplied = wd.proxyApplied;
		proxyClassName = wd.thisReference.replace('/', '.');
	}

	/**
	 * Returns byte array of created class.
	 */
	public byte[] create() {
		process();

		byte[] result = toByteArray();

		if ((proxetta.isForced() == false) && (isProxyApplied() == false)) {
			if (log.isDebugEnabled()) {
				log.debug("proxy not applied " + StringUtil.toSafeString(targetClassName));
			}
			return null;
		}

		if (log.isDebugEnabled()) {
			log.debug("proxy created " + StringUtil.toSafeString(targetClassName));
		}

		return result;
	}

	/**
	 * Defines class.
	 */
	public Class define() {
		process();

		if ((proxetta.isForced() == false) && (isProxyApplied() == false)) {
			if (log.isDebugEnabled()) {
				log.debug("proxy not applied " + StringUtil.toSafeString(targetClassName));
			}

			if (targetClass != null) {
				return targetClass;
			} else if (targetClassName != null) {
				try {
					return ClassLoaderUtil.loadClass(targetClassName);
				} catch (ClassNotFoundException cnfex) {
					throw new ProxettaException(cnfex);
				}
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("proxy created " + StringUtil.toSafeString(targetClassName));
		}

		try {
			ClassLoader classLoader = proxetta.getClassLoader();

			if (classLoader == null) {

				if (targetClass != null) {
					classLoader = targetClass.getClassLoader();
				}

				if (classLoader == null) {
					classLoader = ClassLoaderUtil.getDefaultClassLoader();
				}
			}

//FileUtil.writeBytes("d:\\xxx.class", cp.toByteArray());	// todo debug MODE!!!!! create classes somewhere on disk!!!!!

			return ClassLoaderUtil.defineClass(getProxyClassName(), toByteArray(), classLoader);
		} catch (Exception ex) {
			throw new ProxettaException("Class definition failed.", ex);
		}
	}

	/**
	 * Creates new instance of created class.
	 * Assumes default no-arg constructor.
	 */
	public Object newInstance() {
		Class type = define();
		try {
			return type.newInstance();
		} catch (Exception ex) {
			throw new ProxettaException("Unable to create new instance of Proxetta class.", ex);
		}
	}




	// ---------------------------------------------------------------- OUT

	/**
	 * Checks if proxy is created and throws an exception if not.
	 */
	protected void checkAccepted() {
		if (destClassWriter == null) {
			throw new ProxettaException("Target not accepted yet!");
		}
	}

	/**
	 * Returns raw bytecode.
	 */
	public byte[] toByteArray() {
		checkAccepted();
		return destClassWriter.toByteArray();
	}

	/**
	 * Returns <code>true</code> if at least one method was wrapped.
	 */
	public boolean isProxyApplied() {
		checkAccepted();
		return proxyApplied;
	}

	/**
	 * Returns proxy class name.
	 */
	public String getProxyClassName() {
		checkAccepted();
		return proxyClassName;
	}

}
