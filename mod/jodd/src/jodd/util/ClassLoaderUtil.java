// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class ClassLoaderUtil {

	// ---------------------------------------------------------------- add class path

	/**
	 * Adds additional file or path to classpath during runtime.
	 * @see #addUrlToClassPath(java.net.URL, java.net.URLClassLoader)
	 */
	public static void addFileToClassPath(String path) {
		addFileToClassPath(path, (URLClassLoader) ClassLoader.getSystemClassLoader());
	}

	/**
	 * Adds additional file or path to classpath during runtime.
	 * @see #addUrlToClassPath(java.net.URL, java.net.URLClassLoader)
	 */
	public static void addFileToClassPath(String path, URLClassLoader classLoader) {
		addFileToClassPath(new File(path), classLoader);
	}

	/**
	 * Adds additional file or path to classpath during runtime.
	 * @see #addUrlToClassPath(java.net.URL, java.net.URLClassLoader)
	 */
	public static void addFileToClassPath(File path) {
			addFileToClassPath(path, (URLClassLoader) ClassLoader.getSystemClassLoader());
	}

	/**
	 * Adds additional file or path to classpath during runtime.
	 * @see #addUrlToClassPath(java.net.URL, java.net.URLClassLoader)
	 */
	public static void addFileToClassPath(File path, URLClassLoader classLoader) {
		try {
			addUrlToClassPath(path.toURL(), classLoader);
		} catch (MalformedURLException muex) {
			throw new RuntimeException("Unable to convert path to URL: '" + path + "'.", muex);
		}
	}

	/**
	 * Adds the content pointed by the URL to the classpath during runtime.
	 * @see #addUrlToClassPath(java.net.URL, java.net.URLClassLoader) 
	 */
	public static void addUrlToClassPath(URL url) {
		addUrlToClassPath(url, (URLClassLoader) ClassLoader.getSystemClassLoader());
	}

	/**
	 * Adds the content pointed by the URL to the classpath during runtime.
	 * Uses reflection since <code>addURL</code> method of
	 * <code>URLClassLoader</code> is protected.
	 */
	public static void addUrlToClassPath(URL url, URLClassLoader classLoader) {
		try {
			ReflectUtil.invokeDeclared(URLClassLoader.class, classLoader, "addURL",
					new Class[]{URL.class}, new Object[]{url});
		} catch (Exception ex) {
			throw new RuntimeException("Unable to extend classpath with URL: '" + url + "'.", ex);
		}
	}


	// ---------------------------------------------------------------- define class

	/**
	 * Defines a class from byte array into the system class loader.
	 * @see #defineClass(String, byte[], ClassLoader) 
	 */
	public static Class defineClass(byte[] classData) {
		return defineClass(null, classData, ClassLoader.getSystemClassLoader());
	}

	/**
	 * Defines a class from byte array into the system class loader.
	 * @see #defineClass(String, byte[], ClassLoader)
	 */
	public static Class defineClass(String className, byte[] classData) {
		return defineClass(className, classData, ClassLoader.getSystemClassLoader());
	}

	/**
	 * Defines a class from byte array into the specified class loader.
	 * @see #defineClass(String, byte[], ClassLoader)
	 */
	public static Class defineClass(byte[] classData, ClassLoader classLoader) {
		return defineClass(null, classData, classLoader);
	}

	/**
	 * Defines a class from byte array into the specified class loader.
	 * @see #defineClass(String, byte[], ClassLoader)
	 */
	public static Class defineClass(String className, byte[] classData, ClassLoader classLoader) {
		try {
			return (Class) ReflectUtil.invokeDeclared(ClassLoader.class, classLoader, "defineClass",
					new Class[]{String.class, byte[].class, int.class, int.class},
					new Object[]{className, classData, new Integer(0), new Integer(classData.length)});
		} catch (Throwable th) {
			throw new RuntimeException("Unable to define class '" + className + "'.", th);
		}
	}

	// ---------------------------------------------------------------- find class


	public static Class findClass(String className) {
		return findClass(className, getFullClassPath(ClassLoaderUtil.class), null);
	}

	public static Class findClass(String className, ClassLoader parent) {
		return findClass(className, getFullClassPath(ClassLoaderUtil.class), parent);
	}

	public static Class findClass(String className, URL[] classPath) {
		return findClass(className, classPath, null);
	}

	/**
	 * Finds and loads class on classpath even if it was already loaded.
	 */
	public static Class findClass(String className, URL[] classPath, ClassLoader parent) {
		URLClassLoader tempClassLoader = parent != null ? new URLClassLoader(classPath, parent) : new URLClassLoader(classPath);
		try {
			return (Class) ReflectUtil.invokeDeclared(URLClassLoader.class, tempClassLoader, "findClass",
					new Class[] {String.class},
					new Object[] {className});
		} catch (Throwable th) {
			throw new RuntimeException("Unable to find class '" + className + "'.", th);
		}
	}


	// ---------------------------------------------------------------- classpath


	/**
	 * Returns complete class path from all available <code>URLClassLoaders</code>
	 * starting from class loader that has loaded the specified class. 
	 */
	public static URL[] getFullClassPath(Class clazz) {
		return getFullClassPath(clazz.getClassLoader());
	}

	/**
	 * Returns complete class path from all available <code>URLClassLoader</code>s.
	 */
	public static URL[] getFullClassPath(ClassLoader classLoader) {
		List<URL> list = new ArrayList<URL>();
		while (classLoader != null) {
			if (classLoader instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader) classLoader).getURLs();
				list.addAll(Arrays.asList(urls));
			}
			classLoader = classLoader.getParent();
		}

		URL[] result = new URL[list.size()];
		return list.toArray(result);
	}


	// ---------------------------------------------------------------- get resource

	/**
	 * Retrieves given resource as URL.
	 * @see #getResourceUrl(String, Class)
	 */
	public static URL getResourceUrl(String resourceName) {
		return getResourceUrl(resourceName, null);
	}

	/**
	 * Retrieves given resource as URL.
	 * <p>
	 * Resource will be loaded using class loaders in the following order:
	 * <ul>
	 * <li>{@link Thread#getContextClassLoader() Thread.currentThread().getContextClassLoader()}
	 * <li>{@link Class#getClassLoader() ClassLoaderUtil.class.getClassLoader()}
	 * <li>if <code>callingClass</code> is provided: {@link Class#getClassLoader() callingClass.getClassLoader()}
	 * </ul>
	 */
	public static URL getResourceUrl(String resourceName, Class callingClass) {
		URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
		if (url == null) {
			url = ClassLoaderUtil.class.getClassLoader().getResource(resourceName);
		}
		if ((url == null) && (callingClass != null)) {
			ClassLoader cl = callingClass.getClassLoader();
			if (cl != null) {
				url = cl.getResource(resourceName);
			}
		}
		return url;
	}

	// ---------------------------------------------------------------- get resource file

	/**
	 * Retrieves resource as file.
	 * @see #getResourceFile(String) 
	 */
	public static File getResourceFile(String resourceName) {
		return getResourceFile(resourceName, null);
	}

	/**
	 * Retrieves resource as file. Resource is retrieved as {@link #getResourceUrl(String, Class) URL},
	 * than it is converted to URI so it can be used by File constructor.
	 */
	public static File getResourceFile(String resourceName, Class callingClass) {
		try {
			return new File(getResourceUrl(resourceName, callingClass).toURI());
		} catch (URISyntaxException usex) {
			return null;
		}
	}

	// ---------------------------------------------------------------- get resource stream

	/**
	 * Opens a resource of the specified name for reading.
	 * @see #getResourceAsStream(String, Class) 
	 */
	public static InputStream getResourceAsStream(String resourceName) throws IOException {
		return getResourceAsStream(resourceName, ClassLoaderUtil.class);
	}

	/**
	 * Opens a resource of the specified name for reading.
	 * @see #getResourceUrl(String, Class)
	 */
	public static InputStream getResourceAsStream(String resourceName, Class callingClass) throws IOException {
		URL url = getResourceUrl(resourceName, callingClass);
		if (url != null) {
			return url.openStream();
		}
		return null;
	}

	/**
	 * Opens a class of the specified name for reading.
	 * @see #getResourceAsStream(String, Class)
	 */
	public static InputStream getClassAsStream(Class clazz) throws IOException {
		return getResourceAsStream(getClassFileName(clazz), clazz);
	}

	/**
	 * Opens a class of the specified name for reading.
	 * @see #getResourceAsStream(String, Class)
	 */
	public static InputStream getClassAsStream(String className) throws IOException {
		return getResourceAsStream(getClassFileName(className), ClassLoaderUtil.class);
	}


	// ---------------------------------------------------------------- load class

	/**
	 * Loads a class with a given name dynamically.
	 * @see #loadClass(String, Class) 
	 */
	public static Class loadClass(String className) throws ClassNotFoundException {
		return loadClass(className, null);
	}

	/**
	 * Loads a class with a given name dynamically, more reliable then <code>Class.forName</code>.
	 * <p>
	 * Class will be loaded using class loaders in the following order:
	 * <ul>
	 * <li>{@link Thread#getContextClassLoader() Thread.currentThread().getContextClassLoader()}
	 * <li>the basic {@link Class#forName(java.lang.String)}
	 * <li>{@link Class#getClassLoader() ClassLoaderUtil.class.getClassLoader()}
	 * <li>if <code>callingClass</code> is provided: {@link Class#getClassLoader() callingClass.getClassLoader()}
	 * </ul>
	 */
	public static Class loadClass(String className, Class callingClass) throws ClassNotFoundException {
		try {
			return Thread.currentThread().getContextClassLoader().loadClass(className);
		} catch (ClassNotFoundException cnfex1) {
			try {
				return Class.forName(className);
			} catch (ClassNotFoundException cnfex2) {
				try {
					return ClassLoaderUtil.class.getClassLoader().loadClass(className);
				} catch (ClassNotFoundException cnfex3) {
					if (callingClass != null) {
						return callingClass.getClassLoader().loadClass(className);
					}
					throw cnfex3;
				}
			}
		}
	}

	// ---------------------------------------------------------------- classloader

	/**
	 * Retrieves current class loader for the given class.
	 */
	public static ClassLoader getClassLoader(Class clazz) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = clazz.getClassLoader();
		}
		return loader;
	}

	/**
	 * Retrieves current class loader.
	 * @see #getClassLoader(Class)
	 */
	public static ClassLoader getClassLoader() {
		return getClassLoader(ClassLoaderUtil.class);
	}

	// ---------------------------------------------------------------- misc


	/**
	 * Resolves class file name from class name by replacing dot's with '/' separator
	 * and adding class extension at the end.
	 */
	public static String getClassFileName(Class clazz) {
		if (clazz.isArray()) {
			clazz = clazz.getComponentType();
		}
		return getClassFileName(clazz.getName());
	}

	/**
	 * Resolves class file name from class name by replacing dot's with '/' separator.
	 */
	public static String getClassFileName(String className) {
		return className.replace('.', '/') + ".class";
	}

}
