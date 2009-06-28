// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.ProxettaException;
import jodd.util.ClassLoaderUtil;
import jodd.io.StreamUtil;

import java.io.InputStream;
import java.io.IOException;

/**
 * Creates the proxy subclass using ASM library.
 */
public class ProxettaCreator {

	protected static final Logger log = LoggerFactory.getLogger(ProxettaCreator.class);

	// ---------------------------------------------------------------- ctor

	protected final ProxyAspect[] aspects;

	public ProxettaCreator(ProxyAspect... aspects) {
		this.aspects = aspects;
	}

	// ---------------------------------------------------------------- variable name

	/**
	 * Number appended to proxy class name, incremented on each use to make classnames unique
	 * in the system (e.g. classloader).
	 * @see #setUseVariableClassName(boolean)
 	 */
	protected static int suffixCounter;

	protected boolean useSuffix;

	/**
	 * Specifies class name will vary on each creation. This prevents
	 * <code>java.lang.LinkageError: duplicate class definition.</code>
	 */
	public void setUseVariableClassName(boolean useVariableClassName) {
		useSuffix = useVariableClassName;
	}

	/**
	 * Returns new suffix or <code>null</code> if suffix is not in use.
	 */
	protected String getNewSuffix() {
		if (useSuffix == false) {
			return null;
		}
		suffixCounter++;
		return String.valueOf(suffixCounter);
	}


	// ---------------------------------------------------------------- accept

	protected ClassWriter destClassWriter;			// destination class writer
	protected boolean proxyApplied;
	protected String proxyClassName;

	/**
	 * Single point of class reader acceptance. Reads the target and creates destination class.
	 */
	protected ProxettaCreator accept(ClassReader cr) {
		if (log.isDebugEnabled()) {
			log.debug("Creating proxy for " + cr.getClassName());
		}
		// reads information
		TargetClassInfoReader targetClassInfoReader = new TargetClassInfoReader();
		cr.accept(targetClassInfoReader, 0);
		// create proxy
		this.destClassWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		ProxettaClassBuilder pcb = new ProxettaClassBuilder(destClassWriter, aspects, getNewSuffix(), targetClassInfoReader);
		cr.accept(pcb, 0);
		proxyApplied = pcb.wd.proxyApplied;
		proxyClassName = pcb.wd.thisReference.replace('/', '.');
		return this;
	}

	public ProxettaCreator accept(InputStream in) {
		ClassReader cr;
		try {
			cr = new ClassReader(in);
		} catch (IOException ioex) {
			throw new ProxettaException("Error reading class input stream.", ioex);
		}
		return accept(cr);
	}

	public ProxettaCreator accept(String targetName) {
		InputStream inputStream = null;
		try {
			inputStream = ClassLoaderUtil.getClassAsStream(targetName);
			return accept(inputStream);
		} catch (IOException ioex) {
			throw new ProxettaException("Unable to open stream for class name: " + targetName, ioex);
		} finally {
			StreamUtil.close(inputStream);
		}
	}

	public ProxettaCreator accept(Class target) {
		InputStream inputStream = null;
		try {
			inputStream = ClassLoaderUtil.getClassAsStream(target);
			return accept(inputStream);
		} catch (IOException ioex) {
			throw new ProxettaException("Unable to open stream for: " + target.getName(), ioex);
		} finally {
			StreamUtil.close(inputStream);
		}
	}


	// ---------------------------------------------------------------- after

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