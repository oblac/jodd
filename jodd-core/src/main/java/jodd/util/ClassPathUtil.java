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
import jodd.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static jodd.util.ClassLoaderUtil.getClasspathItemBaseDir;
import static jodd.util.ClassLoaderUtil.getClasspathItemManifest;

/**
 * Utilities to manipulate class path, define and find classes etc.
 */
public class ClassPathUtil {

	/**
	 * Returns default classpath using
	 */
	public static File[] getDefaultClasspath() {
		return getDefaultClasspath(ClassLoaderUtil.getDefaultClassLoader());
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
		final Set<File> classpaths = new TreeSet<>();

		while (classLoader != null) {
			final URL[] urls = ClassPathURLs.of(classLoader, null);
			if (urls != null) {
				for (final URL u : urls) {
					File f = FileUtil.toContainerFile(u);
					if ((f != null) && f.exists()) {
						try {
							f = f.getCanonicalFile();

							final boolean newElement = classpaths.add(f);
							if (newElement) {
								addInnerClasspathItems(classpaths, f);
							}
						} catch (final IOException ignore) {
						}
					}
				}
			}
			classLoader = classLoader.getParent();
		}

		final File[] result = new File[classpaths.size()];
		return classpaths.toArray(result);
	}

	private static void addInnerClasspathItems(final Set<File> classpaths, final File item) {

		final Manifest manifest = getClasspathItemManifest(item);
		if (manifest == null) {
			return;
		}

		final Attributes attributes = manifest.getMainAttributes();
		if (attributes == null) {
			return;
		}

		final String s = attributes.getValue(Attributes.Name.CLASS_PATH);
		if (s == null) {
			return;
		}

		final String base = getClasspathItemBaseDir(item);

		final String[] tokens = StringUtil.splitc(s, ' ');
		for (final String t : tokens) {
			File file;

			// try file with the base path
			try {
				file = new File(base, t);
				file = file.getCanonicalFile();
				if (!file.exists()) {
					file = null;
				}
			} catch (final Exception ignore) {
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
				} catch (final Exception ignore) {
					file = null;
				}
			}

			if (file == null) {
				// try the URL
				try {
					final URL url = new URL(t);

					file = new File(url.getFile());
					file = file.getCanonicalFile();
					if (!file.exists()) {
						file = null;
					}
				} catch (final Exception ignore) {
					file = null;
				}
			}

			if (file != null && file.exists()) {
				classpaths.add(file);
			}
		}
	}

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
