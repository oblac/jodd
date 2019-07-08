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

import jodd.proxetta.impl.InvokeProxetta;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.impl.WrapperProxetta;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
 * <ul>
 * <li> .Foo (starting with a dot) - proxy package name is equal to target package, just proxy simple class name is set.</li>
 * <li> foo. (ending with a dot) - proxy package is set, proxy simple name is create from target simple class name.</li>
 * <li> foo.Foo - full proxy class name is specified.</li>
 * </ul>
 * @see ProxettaFactory
 */
public abstract class Proxetta<T extends Proxetta, A> {

	/**
	 * Creates a new instance of {@link WrapperProxetta}.
	 */
	public static WrapperProxetta wrapperProxetta() {
		return new WrapperProxetta();
	}

	/**
	 * Creates a new instance of {@link ProxyProxetta}.
	 */
	public static ProxyProxetta proxyProxetta() {
		return new ProxyProxetta();
	}

	/**
	 * Creates a new instance of {@link InvokeProxetta}.
	 */
	public static InvokeProxetta invokeProxetta() {
		return new InvokeProxetta();
	}


	@SuppressWarnings("unchecked")
	protected T _this() {
		return (T) this;
	}

	// ---------------------------------------------------------------- properties

	protected boolean forced;
	protected ClassLoader classLoader;
	protected boolean variableClassName;
	protected String classNameSuffix;
	protected File debugFolder;
	protected final List<A> proxyAspectList = new ArrayList<>();

	// ---------------------------------------------------------------- aspects

	/**
	 * Adds an aspect.
	 */
	public T withAspect(final A proxyAspect) {
		proxyAspectList.add(proxyAspect);
		return _this();
	}

	public T withAspects(final A... aspects) {
		Collections.addAll(proxyAspectList, aspects);
		return _this();
	}

	public A[] getAspects(final A[] array) {
		return proxyAspectList.toArray(array);
	}

	/**
	 * Specifies 'forced' mode. If <code>true</code>, new proxy class will be created even if there are no
	 * matching pointcuts. If <code>false</code>, new proxy class will be created only if there is at least one
	 * matching pointcut - otherwise, original class will be returned.
	 */
	public T setForced(final boolean forced) {
		this.forced = forced;
		return _this();
	}

	public boolean isForced() {
		return forced;
	}


	/**
	 * Specifies classloaders for loading created classes.
	 * If classloader not specified, default one will be used.
	 */
	public T setClassLoader(final ClassLoader classLoader) {
		this.classLoader = classLoader;
		return _this();
	}

	/**
	 * Returns specified classloader for loading created classes.
	 * If classloader is not specified, returns <code>null</code>.
	 */
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
	public T setVariableClassName(final boolean variableClassName) {
		this.variableClassName = variableClassName;
		return _this();
	}

	public boolean isVariableClassName() {
		return variableClassName;
	}


	/**
	 * Specifies custom classname suffix to be added to the class name of created proxy.
	 * Warning: when class name suffix is not used, full classname has to be
	 * specified that differs from target class name.
	 */
	public T setClassNameSuffix(final String suffix) {
		this.classNameSuffix = suffix;
		return _this();
	}

	public String getClassNameSuffix() {
		return classNameSuffix;
	}

	/**
	 * Specifies the debug folder where all created classes will be
	 * written to, for debugging purposes.
	 */
	public T setDebugFolder(final String debugFolder) {
		this.debugFolder = new File(debugFolder);
		return _this();
	}

	public T setDebugFolder(final File debugFolder) {
		this.debugFolder = debugFolder;
		return _this();
	}

	/**
	 * Returns debug folder or {@code null} if debug folder does not exist.
	 */
	public File getDebugFolder() {
		return debugFolder;
	}

	// ---------------------------------------------------------------- builder

	/**
	 * Creates {@link ProxettaFactory} with of this Proxetta.
	 */
	public abstract ProxettaFactory proxy();

}