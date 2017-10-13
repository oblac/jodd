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

package jodd.joy.i18n;

import jodd.util.ResourceBundleMessageResolver;
import jodd.util.LocaleUtil;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.ResourceBundle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Central localization messages manager.
 * @see ResourceBundleMessageResolver
 */
public class LocalizationUtil {

	private static final Logger log = LoggerFactory.getLogger(LocalizationUtil.class);

	/**
	 * Central message bundle instance.
	 */
	public static final ResourceBundleMessageResolver MESSAGE_RESOLVER = new ResourceBundleMessageResolver() {
		@Override
		public ResourceBundle getBundle(String bundleName, Locale locale, ClassLoader classLoader) {
			if (!isCacheResourceBundles()) {
				//ResourceBundle.clearCache(classLoader);
				clearResourceBundleCache();
				clearTomcatCache();
			}
			return super.getBundle(bundleName, locale, classLoader);

		}
	};

	static {
		MESSAGE_RESOLVER.addDefaultBundle("messages");
		MESSAGE_RESOLVER.addDefaultBundle("validation");
	}

	// ---------------------------------------------------------------- session/request

	public static final String REQUEST_BUNDLE_NAME_ATTR = ResourceBundleMessageResolver.class.getName() + ".BUNDLE.";
	public static final String SESSION_LOCALE_ATTR = ResourceBundleMessageResolver.class.getName() + ".LOCALE.";

	/**
	 * Sets bundle name for provided servlet request.
	 */
	public static void setRequestBundleName(ServletRequest request, String bundleName) {
		if (log.isDebugEnabled()) {
			log.debug("Bundle name for this request: " + bundleName);
		}
		request.setAttribute(REQUEST_BUNDLE_NAME_ATTR, bundleName);
	}

	/**
	 * Saves locale to HTTP session.
	 */
	public static void setSessionLocale(HttpSession session, String localeCode) {
		if (log.isDebugEnabled()) {
			log.debug("Locale stored to session: " + localeCode);
		}
		Locale locale = LocaleUtil.getLocale(localeCode);
		session.setAttribute(SESSION_LOCALE_ATTR, locale);
	}

	/**
	 * Returns current locale from session.
s	 */
	public static Locale getSessionLocale(HttpSession session) {
		Locale locale = (Locale) session.getAttribute(SESSION_LOCALE_ATTR);
		return locale == null ? MESSAGE_RESOLVER.getFallbackLocale() : locale;
	}

	// ---------------------------------------------------------------- delegates

	public static String findMessage(HttpServletRequest request, String key) {
		String bundleName = (String) request.getAttribute(REQUEST_BUNDLE_NAME_ATTR);
		Locale locale = (Locale) request.getSession().getAttribute(SESSION_LOCALE_ATTR);
		return MESSAGE_RESOLVER.findMessage(bundleName, locale, key);
	}

	public static String findMessage(String bundleName, HttpServletRequest request, String key) {
		Locale locale = (Locale) request.getSession().getAttribute(SESSION_LOCALE_ATTR);
		return MESSAGE_RESOLVER.findMessage(bundleName, locale, key);
	}

	public static String findMessage(HttpServletRequest request, Locale locale, String key) {
		String bundleName = (String) request.getAttribute(REQUEST_BUNDLE_NAME_ATTR);
		return MESSAGE_RESOLVER.findMessage(bundleName, locale, key);
	}

	public static String findDefaultMessage(HttpServletRequest request, String key) {
		Locale locale = (Locale) request.getSession().getAttribute(SESSION_LOCALE_ATTR);
		return MESSAGE_RESOLVER.findDefaultMessage(locale, key);
	}

	public String findMessage(String bundleName, Locale locale, String key) {
		return MESSAGE_RESOLVER.findMessage(bundleName, locale, key);
	}

	public static String findDefaultMessage(Locale locale, String key) {
		return MESSAGE_RESOLVER.findDefaultMessage(locale, key);
	}

	// ---------------------------------------------------------------- util

	/**
	 * Clears Tomcat cache.
	 */
	protected static void clearTomcatCache() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Class cl = loader.getClass();

		try {
			if ("org.apache.catalina.loader.WebappClassLoader".equals(cl.getName())) {
				clearMap(cl, loader, "resourceEntries");
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Class loader " + cl.getName() + " is not a Tomcat loader");
				}
			}
		} catch (Exception ex) {
			if (log.isWarnEnabled()) {
				log.warn("Unable to clear Tomcat cache", ex);
			}
		}
	}

	/**
	 * Clears resource bundle caches.
	 */
	protected static void clearResourceBundleCache() {
		try {
			clearMap(ResourceBundle.class, null, "cacheList");
		} catch (Exception ex) {
			log.warn("Unable to clear resource bundle cache", ex);
		}
	}

	private static void clearMap(Class mapClass, Object map, String fieldName) throws Exception {
		Field field = mapClass.getDeclaredField(fieldName);
		field.setAccessible(true);
		Object cache = field.get(map);
		synchronized (cache) {
			Class ccl = cache.getClass();
			Method clearMethod = ccl.getMethod("clear");
			clearMethod.invoke(cache);
		}
	}


}
