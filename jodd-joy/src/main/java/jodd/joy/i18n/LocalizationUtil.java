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

import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.util.ResourceBundleMessageResolver;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.ResourceBundle;

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
		public ResourceBundle getBundle(final String bundleName, final Locale locale, final ClassLoader classLoader) {
			if (!isCacheResourceBundles()) {
				//ResourceBundle.clearCache(classLoader);
				clearResourceBundleCache();
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
	public static void setRequestBundleName(final ServletRequest request, final String bundleName) {
		if (log.isDebugEnabled()) {
			log.debug("Bundle name for this request: " + bundleName);
		}
		request.setAttribute(REQUEST_BUNDLE_NAME_ATTR, bundleName);
	}

	/**
	 * Saves Locale to HTTP session.
	 */
	public static void setSessionLocale(final HttpSession session, final String localeCode) {
		if (log.isDebugEnabled()) {
			log.debug("Locale stored to session: " + localeCode);
		}
		Locale locale = Locale.forLanguageTag(localeCode);
		session.setAttribute(SESSION_LOCALE_ATTR, locale);
	}

	/**
	 * Returns current locale from session.
s	 */
	public static Locale getSessionLocale(final HttpSession session) {
		Locale locale = (Locale) session.getAttribute(SESSION_LOCALE_ATTR);
		return locale == null ? MESSAGE_RESOLVER.getFallbackLocale() : locale;
	}

	// ---------------------------------------------------------------- delegates

	public static String findMessage(final HttpServletRequest request, final String key) {
		String bundleName = (String) request.getAttribute(REQUEST_BUNDLE_NAME_ATTR);
		Locale locale = (Locale) request.getSession().getAttribute(SESSION_LOCALE_ATTR);
		return MESSAGE_RESOLVER.findMessage(bundleName, locale, key);
	}

	public static String findMessage(final String bundleName, final HttpServletRequest request, final String key) {
		Locale locale = (Locale) request.getSession().getAttribute(SESSION_LOCALE_ATTR);
		return MESSAGE_RESOLVER.findMessage(bundleName, locale, key);
	}

	public static String findMessage(final HttpServletRequest request, final Locale locale, final String key) {
		String bundleName = (String) request.getAttribute(REQUEST_BUNDLE_NAME_ATTR);
		return MESSAGE_RESOLVER.findMessage(bundleName, locale, key);
	}

	public static String findDefaultMessage(final HttpServletRequest request, final String key) {
		Locale locale = (Locale) request.getSession().getAttribute(SESSION_LOCALE_ATTR);
		return MESSAGE_RESOLVER.findDefaultMessage(locale, key);
	}

	public String findMessage(final String bundleName, final Locale locale, final String key) {
		return MESSAGE_RESOLVER.findMessage(bundleName, locale, key);
	}

	public static String findDefaultMessage(final Locale locale, final String key) {
		return MESSAGE_RESOLVER.findDefaultMessage(locale, key);
	}

	// ---------------------------------------------------------------- util

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

	private static void clearMap(final Class mapClass, final Object map, final String fieldName) throws Exception {
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
