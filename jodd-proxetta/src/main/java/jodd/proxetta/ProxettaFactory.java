// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.proxetta;

import jodd.asm7.ClassReader;
import jodd.asm7.ClassWriter;
import jodd.bridge.DefineClass;
import jodd.io.FileUtil;
import jodd.io.StreamUtil;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.proxetta.asm.TargetClassInfoReader;
import jodd.proxetta.asm.WorkData;
import jodd.util.ClassLoaderUtil;
import jodd.util.ClassUtil;
import jodd.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Proxetta builder. While {@link Proxetta} only holds aspects and
 * configuration, <code>ProxettaBuilder</code> deals with the
 * actually building proxies and wrappers over provided target.
 */
public abstract class ProxettaFactory<T extends ProxettaFactory, P extends Proxetta> {

	Logger log = LoggerFactory.getLogger(ProxettaFactory.class);

	protected final P proxetta;

	/**
	 * Creates new builder.
	 */
	protected ProxettaFactory(final P proxetta) {
		this.proxetta = proxetta;
	}

	@SuppressWarnings("unchecked")
	protected T _this() {
		return (T) this;
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
	public T setTargetProxyClassName(final String targetProxyClassName) {
		this.requestedProxyClassName = targetProxyClassName;
		return _this();
	}

	// ---------------------------------------------------------------- IN targets

	/**
	 * Defines class input stream as a target.
	 */
	protected T setTarget(final InputStream target) {
		assertTargetIsNotDefined();

		targetInputStream = target;
		targetClass = null;
		targetClassName = null;

		return _this();
	}

	/**
	 * Defines class name as a target.
	 * Class will not be loaded by classloader!
	 */
	protected T setTarget(final String targetName) {
		assertTargetIsNotDefined();

		try {
			targetInputStream = ClassLoaderUtil.getClassAsStream(targetName);
			if (targetInputStream == null) {
				throw new ProxettaException("Target class not found: " + targetName);
			}
			targetClassName = targetName;
			targetClass = null;
		}
		catch (IOException ioex) {
			StreamUtil.close(targetInputStream);
			throw new ProxettaException("Unable to get stream class name: " + targetName, ioex);
		}
		return _this();
	}

	/**
	 * Defines class as a target.
	 */
	public T setTarget(final Class target) {
		assertTargetIsNotDefined();

		try {
			targetInputStream = ClassLoaderUtil.getClassAsStream(target);
			if (targetInputStream == null) {
				throw new ProxettaException("Target class not found: " + target.getName());
			}
			targetClass = target;
			targetClassName = target.getName();
		}
		catch (IOException ioex) {
			StreamUtil.close(targetInputStream);
			throw new ProxettaException("Unable to stream class: " + target.getName(), ioex);
		}
		return _this();
	}

	/**
	 * Checks if target is not defined yet.
	 */
	private void assertTargetIsNotDefined() {
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

		if (!proxetta.isVariableClassName()) {
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
			throw new ProxettaException("Target missing: " + targetClassName);
		}
		// create class reader
		final ClassReader classReader;
		try {
			classReader = new ClassReader(targetInputStream);
		} catch (IOException ioex) {
			throw new ProxettaException("Error reading class input stream", ioex);
		}

		// reads information
		final TargetClassInfoReader targetClassInfoReader = new TargetClassInfoReader(proxetta.getClassLoader());
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

		dumpClassInDebugFolder(result);

		if ((!proxetta.isForced()) && (!isProxyApplied())) {
			if (log.isDebugEnabled()) {
				log.debug("Proxy not applied: " + StringUtil.toSafeString(targetClassName));
			}
			return null;
		}

		if (log.isDebugEnabled()) {
			log.debug("Proxy created " + StringUtil.toSafeString(targetClassName));
		}

		return result;
	}

	/**
	 * Defines class.
	 */
	public Class define() {
		process();

		if ((!proxetta.isForced()) && (!isProxyApplied())) {
			if (log.isDebugEnabled()) {
				log.debug("Proxy not applied: " + StringUtil.toSafeString(targetClassName));
			}

			if (targetClass != null) {
				return targetClass;
			}

			if (targetClassName != null) {
				try {
					return ClassLoaderUtil.loadClass(targetClassName);
				} catch (ClassNotFoundException cnfex) {
					throw new ProxettaException(cnfex);
				}
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("Proxy created: " + StringUtil.toSafeString(targetClassName));
		}

		try {
			ClassLoader classLoader = proxetta.getClassLoader();

			if (classLoader == null) {
				classLoader = ClassLoaderUtil.getDefaultClassLoader();

				if ((classLoader == null) && (targetClass != null)) {
					classLoader = targetClass.getClassLoader();
				}
			}

			final byte[] bytes = toByteArray();

			dumpClassInDebugFolder(bytes);

			return DefineClass.of(getProxyClassName(), bytes, classLoader);
		} catch (Exception ex) {
			throw new ProxettaException("Class definition failed", ex);
		}
	}

	/**
	 * Creates new instance of created class.
	 * Assumes default no-arg constructor.
	 */
	public Object newInstance() {
		Class type = define();
		try {
			return ClassUtil.newInstance(type);
		} catch (Exception ex) {
			throw new ProxettaException("Invalid Proxetta class", ex);
		}
	}


	// ---------------------------------------------------------------- debug

	/**
	 * Writes created class content to output folder for debugging purposes.
	 */
	protected void dumpClassInDebugFolder(final byte[] bytes) {
		File debugFolder = proxetta.getDebugFolder();
		if (debugFolder == null) {
			return;
		}

		if (!debugFolder.exists() || !debugFolder.isDirectory()) {
			log.warn("Invalid debug folder: " + debugFolder);
		}

		String fileName = proxyClassName;
		if (fileName == null) {
			fileName = "proxetta-" + System.currentTimeMillis();
		}

		fileName += ".class";

		File file = new File(debugFolder, fileName);
		try {
			FileUtil.writeBytes(file, bytes);
		} catch (IOException ioex) {
			log.warn("Error writing class as " + file, ioex);
		}
	}

	// ---------------------------------------------------------------- OUT

	/**
	 * Checks if proxy is created and throws an exception if not.
	 */
	protected void assertProxyIsCreated() {
		if (destClassWriter == null) {
			throw new ProxettaException("Target not accepted yet!");
		}
	}

	/**
	 * Returns raw bytecode.
	 */
	protected byte[] toByteArray() {
		assertProxyIsCreated();
		return destClassWriter.toByteArray();
	}

	/**
	 * Returns <code>true</code> if at least one method was wrapped.
	 */
	public boolean isProxyApplied() {
		assertProxyIsCreated();
		return proxyApplied;
	}

	/**
	 * Returns proxy class name.
	 */
	public String getProxyClassName() {
		assertProxyIsCreated();
		return proxyClassName;
	}

}
