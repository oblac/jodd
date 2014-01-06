// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Class loader that offers two loading strategies: <b>parent-first</b> and <b>parent-last</b>.
 * Extends <code>URLClassLoader</code> (for now:) to provide just minimal set of modifications.
 * <p>
 * When <b>parent-last</b> strategy is used, be aware how you use {@link ClassLoaderUtil} as
 * it is designed to follow <b>parent-first</b> strategy.
 */
public class ExtendedURLClassLoader extends URLClassLoader {

	protected ClassLoader parentClassLoader;
	protected String[] systemPackages;
	protected String[] loaderPackages;
	protected boolean parentFirst;

	/**
	 * Creates class loader with <b>parent-first</b> loading strategy.
	 * This is aligned how java class loaders work.
	 */
	public ExtendedURLClassLoader(URL[] classpath, ClassLoader parent) {
		this(classpath, parent, true);
	}

	/**
	 * Creates class loader with given loading strategy.
	 */
	public ExtendedURLClassLoader(URL[] classpath, ClassLoader parent, boolean parentFirst) {
		super(classpath, parent);

		this.parentFirst = parentFirst;

		if (parent == null) {
			parent = getSystemClassLoader();
		}
		parentClassLoader = parent;

		systemPackages = new String[0];
		loaderPackages = new String[0];

		addSystemPackage(SystemUtil.getJrePackages());
	}

	// ---------------------------------------------------------------- properties

	/**
	 * Controls whether class lookup is delegated to the parent loader first
	 * or after this loader. Use with extreme caution as setting this to
	 * false (i.e. to <b>parent-last</b>) violates the class loader hierarchy.
	 */
	public void setParentFirst(boolean parentFirst) {
		this.parentFirst = parentFirst;
	}

	/**
	 * Adds system packages or package roots to the list of packages
	 * which must be loaded on the parent loader. By default, the list
	 * is already populated with {@link jodd.util.SystemUtil#getJrePackages() JRE packages}.
	 */
	public void addSystemPackage(String... packages) {
		systemPackages = joinPackages(systemPackages, packages);
	}

	/**
	 * Adds loader packages or package roots to the list of packages
	 * which must be loaded using this loader.
	 */
	public void addLoaderPackage(String... packages) {
		loaderPackages = joinPackages(loaderPackages, packages);
	}

	/**
	 * Join packages and appends dot to package names if missing.
	 */
	protected String[] joinPackages(String[] dest, String[] src) {
		int len = dest.length;

		String[] result = new String[len + src.length];
		System.arraycopy(dest, 0, result, 0, len);

		for (int i = 0; i < src.length; i++) {
			String pck = src[i];
			pck += pck.endsWith(".") ? "" : ".";

			result[len + i] = pck;
		}

		return result;
	}

	/**
	 * Returns <code>true</code> if class or resource name matches
	 * at least one package root from the list.
	 */
	protected boolean isInPackageList(String name, String[] packages) {
		for (String pck : packages) {
			if (name.startsWith(pck)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if parent-first strategy should be used.
	 */
	protected boolean isParentFirst(String resourceName) {
		boolean useParentFirst = parentFirst;

		if (isInPackageList(resourceName, systemPackages)) {
			useParentFirst = true;
		}
		if (isInPackageList(resourceName, loaderPackages)) {
			useParentFirst = false;
		}

		return useParentFirst;
	}

	// ---------------------------------------------------------------- overrides

	/**
	 * Loads class using parent-first or parent-last strategy.
	 */
	@Override
	protected synchronized Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {

		// check first if the class has already been loaded

		Class<?> c = findLoadedClass(className);

		if (c != null) {
			if (resolve) {
				resolveClass(c);
			}

			return c;
		}

		// class not loaded yet

		boolean loadUsingParentFirst = isParentFirst(className);

		if (loadUsingParentFirst) {
			try {
				c = parentClassLoader.loadClass(className);
			} catch (ClassNotFoundException ignore) {
			}

			if (c == null) {
				c = findClass(className);
			}
		} else {
			try {
				c = findClass(className);
			} catch (ClassNotFoundException ignore) {
			}

			if (c == null) {
				c = parentClassLoader.loadClass(className);
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
	public URL getResource(String resourceName) {

		boolean loadUsingParentFirst = isParentFirst(resourceName);

		URL url;

		if (loadUsingParentFirst) {
			url = parentClassLoader.getResource(resourceName);

			if (url == null) {
				url = findResource(resourceName);
			}
		} else {
			url = findResource(resourceName);

			if (url == null) {
				url = parentClassLoader.getResource(resourceName);
			}
		}

		return url;
	}

	/**
	 * Similar to its super method, except local resources are enumerated
	 * before parent resources.
	 */
	@Override
	public Enumeration<URL> getResources(String resourceName) throws IOException {

		final List<URL> urls = new ArrayList<URL>();

		Enumeration<URL> localUrls = findResources(resourceName);
		Enumeration<URL> parentUrls = parentClassLoader.getResources(resourceName);

		boolean loadUsingParentFirst = isParentFirst(resourceName);

		if (loadUsingParentFirst) {
			while (parentUrls.hasMoreElements()) {
				urls.add(parentUrls.nextElement());
			}
			while (localUrls.hasMoreElements()) {
				urls.add(localUrls.nextElement());
			}
		} else {
			while (localUrls.hasMoreElements()) {
				urls.add(localUrls.nextElement());
			}
			while (parentUrls.hasMoreElements()) {
				urls.add(parentUrls.nextElement());
			}
		}

		return new Enumeration<URL>() {
			Iterator<URL> iterator = urls.iterator();

			public boolean hasMoreElements() {
				return iterator.hasNext();
			}

			public URL nextElement() {
				return iterator.next();
			}
		};
	}

}