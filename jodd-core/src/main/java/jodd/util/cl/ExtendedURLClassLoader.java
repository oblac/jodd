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

package jodd.util.cl;

import jodd.util.ArraysUtil;
import jodd.util.StringUtil;
import jodd.util.SystemUtil;
import jodd.util.Wildcard;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Class loader that offers two loading strategies: <b>parent-first</b> and <b>parent-last</b>.
 * The strategy defines the class loading order between this classloader and parent class loader;
 * i.e. defines the <i>first</i> class loader to be used for loading classes.
 * Extends <code>URLClassLoader</code> to provide just minimal set of modifications.
 * <p>
 * Either way, you can specify wildcard rules to force some behavior - like loading some package/class
 * exclusively with specific loader. There are the following groups:
 * <ul>
 *     <li>parent-only - classes will be loaded only with parent classloader.</li>
 *     <li>loader-only - classes will be loaded only with this loader.</li>
 *     <li>default - classes will be loaded as specified by strategy (no need to define).</li>
 * </ul>
 * <p>
 * The order of matching is also set by loading strategy. For <b>parent-first</b> strategy,
 * <b>loader</b> group rules will be checked first. For <b>parent-last</b> strategy,
 * <b>parent</b> group rules will be checked first.
 * <p>
 * By default, the list of <b>parent-only</b> group is populated with
 * {@link jodd.system.SystemInfo#getJrePackages()}  JRE packages}.
 * <p>
 * When <b>parent-last</b> strategy is used, be aware how you use
 * {@link jodd.util.ClassLoaderUtil} as it is designed to follow <b>parent-first</b>
 * strategy. Use with caution as setting <b>parent-last</b> violates the
 * class loader hierarchy.
 */
public class ExtendedURLClassLoader extends URLClassLoader {

	protected final ClassLoader parentClassLoader;
	protected String[] parentOnlyRules;
	protected String[] loaderOnlyRules;
	protected final boolean parentFirst;
	protected boolean matchResourcesAsPackages = true;

	public ExtendedURLClassLoader(final URL[] classpath, final ClassLoader parent, final boolean parentFirst) {
		this(classpath, parent, parentFirst, true);
	}

	/**
	 * Creates class loader with given loading strategy.
	 */
	public ExtendedURLClassLoader(
		final URL[] classpath, final ClassLoader parent,
		final boolean parentFirst, final boolean excludeJrePackagesFromLoader) {

		super(classpath, parent);

		this.parentFirst = parentFirst;

		if (parent == null) {
			throw new IllegalArgumentException("Parent classloader not specified");
		}
		parentClassLoader = parent;

		parentOnlyRules = new String[0];
		loaderOnlyRules = new String[0];

		if (excludeJrePackagesFromLoader) {
			final String[] corePackages = SystemUtil.info().getJrePackages();

			for (final String corePackage : corePackages) {
				if (corePackage.equals("javax")) {
					// javax is NOT forbidden
					continue;
				}

				addParentOnlyRules(corePackage + ".*");
			}
		}
	}

	// ---------------------------------------------------------------- rules

	/**
	 * Adds parent only rules for classes which must be loaded on the
	 * parent loader.
	 */
	public void addParentOnlyRules(final String... packages) {
		parentOnlyRules = ArraysUtil.join(parentOnlyRules, packages);
	}

	/**
	 * Adds loader-only rules for classes which must be loaded using this
	 * loader.
	 */
	public void addLoaderOnlyRules(final String... packages) {
		loaderOnlyRules = ArraysUtil.join(loaderOnlyRules, packages);
	}

	/**
	 * When set, resources will be matched in the same way as packages.
	 * If disabled, resources must be matched only with separate rules
	 * that uses "/".
	 */
	public void setMatchResourcesAsPackages(final boolean matchResourcesAsPackages) {
		this.matchResourcesAsPackages = matchResourcesAsPackages;
	}

	/**
	 * Returns <code>true</code> if class or resource name matches
	 * at least one package rule from the list.
	 */
	protected boolean isMatchingRules(final String name, final String... rules) {
		for (final String rule : rules) {
			if (Wildcard.equalsOrMatch(name, rule)) {
				return true;
			}
		}
		return false;
	}

	// ---------------------------------------------------------------- resolver

	protected static class Loading {
		public Loading(final boolean withParent, final boolean withLoader) {
			this.withParent = withParent;
			this.withLoader = withLoader;
		}

		protected final boolean withParent;
		protected final boolean withLoader;
	}

	/**
	 * Resolves loading rules.
	 */
	protected Loading resolveLoading(final boolean parentFirstStrategy, final String className) {
		boolean withParent = true;
		boolean withLoader = true;

		if (parentFirstStrategy) {
			if (isMatchingRules(className, loaderOnlyRules)) {
				withParent = false;
			}
			else if (isMatchingRules(className, parentOnlyRules)) {
				withLoader = false;
			}
		}
		else {
			if (isMatchingRules(className, parentOnlyRules)) {
				withLoader = false;
			}
			else if (isMatchingRules(className, loaderOnlyRules)) {
				withParent = false;
			}
		}

		return new Loading(withParent, withLoader);
	}

	/**
	 * Resolves resources.
	 */
	protected Loading resolveResourceLoading(final boolean parentFirstStrategy, String resourceName) {
		if (matchResourcesAsPackages) {
			resourceName = StringUtil.replaceChar(resourceName, '/', '.');
		}

		return resolveLoading(parentFirstStrategy, resourceName);
	}


	// ---------------------------------------------------------------- overrides

	/**
	 * Loads class using parent-first or parent-last strategy.
	 */
	@Override
	protected synchronized Class<?> loadClass(final String className, final boolean resolve) throws ClassNotFoundException {

		// check first if the class has already been loaded

		Class<?> c = findLoadedClass(className);

		if (c != null) {
			if (resolve) {
				resolveClass(c);
			}

			return c;
		}

		// class not loaded yet

		final Loading loading = resolveLoading(parentFirst, className);

		if (parentFirst) {
			// PARENT FIRST
			if (loading.withParent) {
				try {
					c = parentClassLoader.loadClass(className);
				}
				catch (final ClassNotFoundException ignore) {
				}
			}

			if (c == null) {
				if (loading.withLoader) {
					c = this.findClass(className);
				}
				else {
					throw new ClassNotFoundException("Class not found: " + className);
				}
			}
		} else {
			// THIS FIRST
			if (loading.withLoader) {
				try {
					c = this.findClass(className);
				}
				catch (final ClassNotFoundException ignore) {
				}
			}

			if (c == null) {
				if (loading.withParent) {
					c = parentClassLoader.loadClass(className);
				}
				else {
					throw new ClassNotFoundException("Class not found: " + className);
				}
			}
		}

		if (resolve) {
			resolveClass(c);
		}
		return c;
	}

	/**
	 * Returns a resource using parent-first or parent-last strategy.
	 */
	@Override
	public URL getResource(final String resourceName) {

		URL url = null;

		final Loading loading = resolveResourceLoading(parentFirst, resourceName);

		if (parentFirst) {
			// PARENT FIRST
			if (loading.withParent) {
				url = parentClassLoader.getResource(resourceName);
			}

			if (url == null) {
				if (loading.withLoader) {
					url = this.findResource(resourceName);
				}
			}
		} else {
			// THIS FIRST
			if (loading.withLoader) {
				url = this.findResource(resourceName);
			}

			if (url == null) {
				if (loading.withParent) {
					url = parentClassLoader.getResource(resourceName);
				}
			}
		}

		return url;
	}

	@Override
	public Enumeration<URL> getResources(final String resourceName) throws IOException {

		final List<URL> urls = new ArrayList<>();

		final Enumeration<URL> loaderUrls = this.findResources(resourceName);
		final Enumeration<URL> parentUrls = parentClassLoader.getResources(resourceName);

		final Loading loading = resolveResourceLoading(parentFirst, resourceName);

		if (parentFirst) {
			if (loading.withParent) {
				while (parentUrls.hasMoreElements()) {
					urls.add(parentUrls.nextElement());
				}
			}
			if (loading.withLoader) {
				while (loaderUrls.hasMoreElements()) {
					urls.add(loaderUrls.nextElement());
				}
			}
		}
		else {
			if (loading.withLoader) {
				while (loaderUrls.hasMoreElements()) {
					urls.add(loaderUrls.nextElement());
				}
			}
			if (loading.withParent) {
				while (parentUrls.hasMoreElements()) {
					urls.add(parentUrls.nextElement());
				}
			}
		}
		return new Enumeration<URL>() {
			final Iterator<URL> iterator = urls.iterator();

			@Override
			public boolean hasMoreElements() {
				return iterator.hasNext();
			}

			@Override
			public URL nextElement() {
				return iterator.next();
			}
		};
	}

}
