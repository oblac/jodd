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

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.zip.ZipFile;

/**
 * Methods that requires different implementations on various Java Platforms.
 */
public class Java {

	private final static boolean JAVA_9 = SystemUtil.javaVersionNumber() >= 9;

	/**
	 * Returns urls for the classloader
	 *
	 * @param ldr classloader in which to find urls
	 * @return list of urls or {@code null} if not found
	 */
	public static URL[] getURLs(ClassLoader ldr) {
		try {
			if (!JAVA_9) {
				URLClassLoader cl = (URLClassLoader) ldr;
				return cl.getURLs();
			}
			else {
				Field ucpField = Class
					.forName("jdk.internal.loader.BuiltinClassLoader")
					.getDeclaredField("ucp");

				ClassUtil.forceAccess(ucpField);

				Object ucpObject = ucpField.get(ldr);

				return (URL[]) Class
					.forName("jdk.internal.loader.URLClassPath")
					.getMethod("getURLs")
					.invoke(ucpObject);
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Closes ClassLoader.
	 */
	public static void close(ClassLoader clsLdr) {
		if (clsLdr == null)
			return;

		Object loaders;

		try {
			Field ucpField = clsLdr.getClass().getDeclaredField("ucp");
			ClassUtil.forceAccess(ucpField);

			if (!JAVA_9) {
				Object ucpObject = ucpField.get(clsLdr);
				Field ldrFld = Class
					.forName("sun.misc.URLClassPath")
					.getDeclaredField("loaders");

				ClassUtil.forceAccess(ldrFld);
				loaders = ldrFld.get(ucpObject);
			}
			else {
				Field ldrFld = Class
					.forName("jdk.internal.loader.URLClassPath")
					.getField("loaders");

				ClassUtil.forceAccess(ldrFld);
				loaders = ucpField.get(clsLdr);
			}

			Iterable ldrs = (Iterable) loaders;

			for (Object ldr : ldrs) {
				if (ldr.getClass().getName().endsWith("JarLoader"))
					try {
						Field jarFld = ldr.getClass().getDeclaredField("jar");

						ClassUtil.forceAccess(jarFld);

						ZipFile jar = (ZipFile) jarFld.get(ldr);

						jar.close();

					} catch (Exception e) {
						System.err.println("Failed to close resource: " + e.getMessage());
					}
			}
		} catch (Exception e) {
			System.err.println("Failed to close resource: " + e.getMessage());
		}
	}
}
