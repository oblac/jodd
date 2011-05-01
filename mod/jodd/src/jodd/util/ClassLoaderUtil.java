// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.io.FileUtil;
import jodd.io.StreamUtil;

import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ClassLoaderUtil {

	// ---------------------------------------------------------------- default class loader

	/**
	 * Returns default class loader. By default, it is thread context class loader.
	 * If this one is <code>null</code> then class loader of this class is
	 * returned.
	 */
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
			cl = ClassLoaderUtil.class.getClassLoader();
		}
		return cl;
	}

	// ---------------------------------------------------------------- add class path

	/**
	 * Adds additional file or path to classpath during runtime.
	 * @see #addUrlToClassPath(java.net.URL, java.net.URLClassLoader)
	 */
	public static void addFileToClassPath(String path) {
		addFileToClassPath(path, (URLClassLoader) getDefaultClassLoader());
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
			addFileToClassPath(path, (URLClassLoader) getDefaultClassLoader());
	}

	/**
	 * Adds additional file or path to classpath during runtime.
	 * @see #addUrlToClassPath(java.net.URL, java.net.URLClassLoader)
	 */
	public static void addFileToClassPath(File path, URLClassLoader classLoader) {
		try {
			addUrlToClassPath(path.toURL(), classLoader);
		} catch (MalformedURLException muex) {
			throw new IllegalArgumentException("Unable to convert path to URL: '" + path + "'.", muex);
		}
	}

	/**
	 * Adds the content pointed by the URL to the classpath during runtime.
	 * @see #addUrlToClassPath(java.net.URL, java.net.URLClassLoader) 
	 */
	public static void addUrlToClassPath(URL url) {
		addUrlToClassPath(url, (URLClassLoader) getDefaultClassLoader());
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
			throw new IllegalArgumentException("Unable to extend classpath with URL: '" + url + "'.", ex);
		}
	}


	// ---------------------------------------------------------------- define class

	/**
	 * Defines a class from byte array into the system class loader.
	 * @see #defineClass(String, byte[], ClassLoader) 
	 */
	public static Class defineClass(byte[] classData) {
		return defineClass(null, classData, getDefaultClassLoader());
	}

	/**
	 * Defines a class from byte array into the system class loader.
	 * @see #defineClass(String, byte[], ClassLoader)
	 */
	public static Class defineClass(String className, byte[] classData) {
		return defineClass(className, classData, getDefaultClassLoader());
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
	 * Warning: this is a <b>hack</b>!
	 * @param className optional class name, may be <code>null</code>
	 * @param classData bytecode data
	 * @param classLoader classloader that will load class
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

	public static Class findClass(String className, File[] classPath) {
		URL[] urls = new URL[classPath.length];
		for (int i = 0; i < classPath.length; i++) {
			File file = classPath[i];
			try {
				urls[i] = file.toURL();
			} catch (MalformedURLException ignore) {
			}
		}
		return findClass(className, urls, null);
	}


	/**
	 * @see #findClass(String, java.net.URL[], ClassLoader)
	 */
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

	private static final String[] MANIFESTS = {"Manifest.mf", "manifest.mf", "MANIFEST.MF"};

	/**
	 * Returns classpath item manifest or <code>null</code> if not found.
	 */
	public static Manifest getClasspathItemManifest(File classpathItem) {
		Manifest manifest = null;

		if (classpathItem.isFile()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(classpathItem);
				JarFile jar = new JarFile(classpathItem);
				manifest = jar.getManifest();
			} catch (IOException ignore) {
			}
			finally {
				StreamUtil.close(fis);
			}
		} else {
			File metaDir = new File(classpathItem, "META-INF");
			File manifestFile = null;
			if (metaDir.isDirectory()) {
				for (String m : MANIFESTS) {
					File mFile = new File(metaDir, m);
					if (mFile.isFile() == true) {
						manifestFile = mFile;
						break;
					}
				}
			}
			if (manifestFile != null) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(manifestFile);
					manifest = new Manifest(fis);
				} catch (IOException ignore) {
				}
				finally {
					StreamUtil.close(fis);
				}
			}
		}

		return manifest;
	}

	/**
	 * Returns base folder for classpath item. If item is a (jar) file,
	 * its parent is returned. If item is a directory, its name is returned.
	 */
	public static String getClasspathItemBaseDir(File classpathItem) {
		String base;
		if (classpathItem.isFile()) {
			base = classpathItem.getParent();
		} else {
			base = classpathItem.toString();
		}
		return base;
	}

	/**
	 * Returns default classpath using
	 * {@link #getDefaultClassLoader() default classloader}.
	 */
	public static File[] getDefaultClasspath() {
		return getDefaultClasspath(getDefaultClassLoader());
	}

	/**
	 * Returns default class path from all available <code>URLClassLoader</code>
	 * in classloader hierarchy. The following is added to the classpath list:
	 * <li>file URLs from <code>URLClassLoader</code> (other URL protocols are ignored)
	 * <li>inner entries from containing <b>manifest</b> files (if exist)
	 * <li>bootstrap classpath
	 */
	public static File[] getDefaultClasspath(ClassLoader classLoader) {
		Set<File> classpaths = new HashSet<File>();

		while (classLoader != null) {
			if (classLoader instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader) classLoader).getURLs();
				for (URL u : urls) {
					File f = FileUtil.toFile(u);
					if (f != null) {
						try {
							f = f.getCanonicalFile();
							classpaths.add(f);
							addInnerClasspathItems(classpaths, f);
						} catch (IOException ignore) {
						}
					}
				}
			}
			classLoader = classLoader.getParent();
		}

		String bootstrap = SystemUtil.getSunBoothClassPath();
		if (bootstrap != null) {
			classpaths.add(new File(bootstrap));
		}

		File[] result = new File[classpaths.size()];
		return classpaths.toArray(result);
	}

	private static void addInnerClasspathItems(Set<File> classpaths, File item) {

		Manifest manifest = getClasspathItemManifest(item);
		if (manifest == null) {
			return;
		}

		Attributes attributes = manifest.getMainAttributes();
		if (attributes == null) {
			return;
		}

		String s = attributes.getValue(Attributes.Name.CLASS_PATH);
		if (s == null) {
			return;
		}

		String base = getClasspathItemBaseDir(item);

		String[] tokens = StringUtil.splitc(s, ' ');
		for (String t : tokens) {
			try {
				File file = new File(base, t);
				file = file.getCanonicalFile();

				if (file.exists()) {
					classpaths.add(file);
				}
			} catch (IOException ignore) {
			}
		}
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
		} catch (URISyntaxException ignore) {
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

		// try #1
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader != null) {
				return classLoader.loadClass(className);
			}
		} catch (ClassNotFoundException ignore) {
		}

		// try #2
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException ignore) {
		}

		// try #3
		try {
			ClassLoader classLoader = ClassLoaderUtil.class.getClassLoader();
			if (classLoader != null) {
				return classLoader.loadClass(className);
			}
		} catch (ClassNotFoundException ignore) {
		}

		// try #4
		if (callingClass != null) {
			ClassLoader classLoader = callingClass.getClassLoader();
			if (classLoader != null) {
				return classLoader.loadClass(className);
			}
		}

		throw new ClassNotFoundException("Class not found: '" + className + '\'');
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
