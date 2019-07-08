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

package jodd.util;

import jodd.Jodd;
import jodd.bridge.ClassPathURLs;
import jodd.io.FileUtil;
import jodd.io.StreamUtil;
import jodd.util.cl.ClassLoaderStrategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Utilities to manipulate class path, define and find classes etc.
 */
public class ClassLoaderUtil {

	// ---------------------------------------------------------------- default class loader


	/**
	 * Returns default class loader. By default, it is {@link #getContextClassLoader() threads context class loader}.
	 * If this one is <code>null</code>, then class loader of the <b>caller class</b> is returned.
	 */
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = getContextClassLoader();
		if (cl == null) {
			Class callerClass = ClassUtil.getCallerClass(2);
			cl = callerClass.getClassLoader();
		}
		return cl;
	}

	/**
	 * Returns thread context class loader.
	 */
	public static ClassLoader getContextClassLoader() {
		if (System.getSecurityManager() == null) {
			return Thread.currentThread().getContextClassLoader();
		}
		else {
			return AccessController.doPrivileged(
				(PrivilegedAction<ClassLoader>) () -> Thread.currentThread().getContextClassLoader());
		}
	}

	/**
	 * Returns system class loader.
	 */
	public static ClassLoader getSystemClassLoader() {
		if (System.getSecurityManager() == null) {
			return ClassLoader.getSystemClassLoader();
		}
		else {
			return AccessController.doPrivileged(
				(PrivilegedAction<ClassLoader>) ClassLoader::getSystemClassLoader);
		}
	}

	// ---------------------------------------------------------------- classpath

	private static final String[] MANIFESTS = {"Manifest.mf", "manifest.mf", "MANIFEST.MF"};

	/**
	 * Returns classpath item manifest or <code>null</code> if not found.
	 */
	public static Manifest getClasspathItemManifest(final File classpathItem) {
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
					if (mFile.isFile()) {
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
	public static String getClasspathItemBaseDir(final File classpathItem) {
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
	 * <ul>
	 * <li>file URLs from <code>URLClassLoader</code> (other URL protocols are ignored)</li>
	 * <li>inner entries from containing <b>manifest</b> files (if exist)</li>
	 * <li>bootstrap classpath is ignored</li>
	 * </ul>
	 */
	public static File[] getDefaultClasspath(ClassLoader classLoader) {
		Set<File> classpaths = new TreeSet<>();

		while (classLoader != null) {
			URL[] urls = ClassPathURLs.of(classLoader, null);
			if (urls != null) {
				for (URL u : urls) {
					File f = FileUtil.toContainerFile(u);
					if ((f != null) && f.exists()) {
						try {
							f = f.getCanonicalFile();

							boolean newElement = classpaths.add(f);
							if (newElement) {
								addInnerClasspathItems(classpaths, f);
							}
						} catch (IOException ignore) {
						}
					}
				}
			}
			classLoader = classLoader.getParent();
		}

		File[] result = new File[classpaths.size()];
		return classpaths.toArray(result);
	}

	private static void addInnerClasspathItems(final Set<File> classpaths, final File item) {

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
			File file;

			// try file with the base path
			try {
				file = new File(base, t);
				file = file.getCanonicalFile();
				if (!file.exists()) {
					file = null;
				}
			} catch (Exception ignore) {
				file = null;
			}

			if (file == null) {
				// try file with absolute path
				try {
					file = new File(t);
					file = file.getCanonicalFile();
					if (!file.exists()) {
						file = null;
					}
				} catch (Exception ignore) {
					file = null;
				}
			}

			if (file == null) {
				// try the URL
				try {
					URL url = new URL(t);

					file = new File(url.getFile());
					file = file.getCanonicalFile();
					if (!file.exists()) {
						file = null;
					}
				} catch (Exception ignore) {
					file = null;
				}
			}

			if (file != null && file.exists()) {
				classpaths.add(file);
			}
		}
	}


	// ---------------------------------------------------------------- get resource

	/**
	 * Retrieves given resource as URL.
	 * @see #getResourceUrl(String, ClassLoader)
	 */
	public static URL getResourceUrl(final String resourceName) {
		return getResourceUrl(resourceName, null);
	}

	/**
	 * Retrieves given resource as URL. Resource is always absolute and may
	 * starts with a slash character.
	 * <p>
	 * Resource will be loaded using class loaders in the following order:
	 * <ul>
	 * <li>{@link Thread#getContextClassLoader() Thread.currentThread().getContextClassLoader()}</li>
	 * <li>{@link Class#getClassLoader() ClassLoaderUtil.class.getClassLoader()}</li>
	 * <li>if <code>callingClass</code> is provided: {@link Class#getClassLoader() callingClass.getClassLoader()}</li>
	 * </ul>
	 */
	public static URL getResourceUrl(String resourceName, final ClassLoader classLoader) {

		if (resourceName.startsWith("/")) {
			resourceName = resourceName.substring(1);
		}
		
		URL resourceUrl;

		// try #1 - using provided class loader
		if (classLoader != null) {
			resourceUrl = classLoader.getResource(resourceName);
			if (resourceUrl != null) {
				return resourceUrl;
			}
		}

		// try #2 - using thread class loader
		ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
		if ((currentThreadClassLoader != null) && (currentThreadClassLoader != classLoader)) {
			resourceUrl = currentThreadClassLoader.getResource(resourceName);
			if (resourceUrl != null) {
				return resourceUrl;
			}
		}

		// try #3 - using caller classloader, similar as Class.forName()
		Class callerClass = ClassUtil.getCallerClass(2);
		ClassLoader callerClassLoader = callerClass.getClassLoader();

		if ((callerClassLoader != classLoader) && (callerClassLoader != currentThreadClassLoader)) {
			resourceUrl = callerClassLoader.getResource(resourceName);
			if (resourceUrl != null) {
				return resourceUrl;
			}
		}

		return null;
	}

	// ---------------------------------------------------------------- get resource stream

	/**
	 * Opens a resource of the specified name for reading.
	 * @see #getResourceAsStream(String, ClassLoader)
	 */
	public static InputStream getResourceAsStream(final String resourceName) throws IOException {
		return getResourceAsStream(resourceName, null);
	}

	/**
	 * Opens a resource of the specified name for reading.
	 * @see #getResourceUrl(String, ClassLoader)
	 */
	public static InputStream getResourceAsStream(final String resourceName, final ClassLoader callingClass) throws IOException {
		URL url = getResourceUrl(resourceName, callingClass);
		if (url != null) {
			return url.openStream();
		}
		return null;
	}

	/**
	 * Opens a resource of the specified name for reading. Controls caching,
	 * that is important when the same jar is reloaded using custom classloader.
	 */
	public static InputStream getResourceAsStream(final String resourceName, final ClassLoader callingClass, final boolean useCache) throws IOException {
		URL url = getResourceUrl(resourceName, callingClass);
		if (url != null) {
			URLConnection urlConnection = url.openConnection();
			urlConnection.setUseCaches(useCache);
			return urlConnection.getInputStream();
		}
		return null;
	}

	/**
	 * Opens a class of the specified name for reading using class classloader.
	 * @see #getResourceAsStream(String, ClassLoader)
	 */
	public static InputStream getClassAsStream(final Class clazz) throws IOException {
		return getResourceAsStream(ClassUtil.convertClassNameToFileName(clazz), clazz.getClassLoader());
	}

	/**
	 * Opens a class of the specified name for reading. No specific classloader is used
	 * for loading class.
	 * @see #getResourceAsStream(String, ClassLoader)
	 */
	public static InputStream getClassAsStream(final String className) throws IOException {
		return getResourceAsStream(ClassUtil.convertClassNameToFileName(className));
	}

	/**
	 * Opens a class of the specified name for reading using provided class loader.
	 */
	public static InputStream getClassAsStream(final String className, final ClassLoader classLoader) throws IOException {
		return getResourceAsStream(ClassUtil.convertClassNameToFileName(className), classLoader);
	}

	// ---------------------------------------------------------------- load class

	/**
	 * Loads a class using default class loader strategy.
	 * @see jodd.util.cl.DefaultClassLoaderStrategy
	 */
	public static Class loadClass(final String className) throws ClassNotFoundException {
		return ClassLoaderStrategy.get().loadClass(className, null);
	}
	
	/**
	 * Loads a class using default class loader strategy.
	 * @see jodd.util.cl.DefaultClassLoaderStrategy
	 */
	public static Class loadClass(final String className, final ClassLoader classLoader) throws ClassNotFoundException {
		return ClassLoaderStrategy.get().loadClass(className, classLoader);
	}

	// ---------------------------------------------------------------- class location

	/**
	 * Returns location of the class. If class is not in a jar, it's classpath
	 * is returned; otherwise the jar location.
	 */
	public static String classLocation(final Class clazz) {
		return clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
	}

	/**
	 * Returns Jodd {@link #classLocation(Class) location}.
	 * @see #classLocation
	 */
	public static String joddLocation() {
		return classLocation(Jodd.class);
	}

}
