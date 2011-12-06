// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.i18n;

import jodd.log.Log;
import jodd.util.ResourceBundleMessageResolver;
import jodd.util.LocaleUtil;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.ResourceBundle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Finds localized text.
 */
public class LocalizationUtil {

	private static final Log log = Log.getLogger(LocalizationUtil.class);

	public static final ResourceBundleMessageResolver MESSAGE_RESOLVER = new ResourceBundleMessageResolver() {
		@Override
		public ResourceBundle getBundle(String bundleName, Locale locale, ClassLoader classLoader) {
			if (isCacheResourceBundles() == false) {
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

	public static final String REQUEST_BUNDLE_NAME_ATTR = ResourceBundleMessageResolver.class.getName() + ".bundleName";
	public static final String SESSION_LOCALE_ATTR = ResourceBundleMessageResolver.class.getName() + ".locale";

	public static void setRequestBundleName(ServletRequest request, String bundleName) {
		if (log.isDebugEnabled()) {
			log.debug("Bundle name for this request: " + bundleName);
		}
		request.setAttribute(REQUEST_BUNDLE_NAME_ATTR, bundleName);
	}

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

	public String findMessage(String bundleName, Locale locale, String key) {
		return MESSAGE_RESOLVER.findMessage(bundleName, locale, key);
	}

	public static String findDefaultMessage(HttpServletRequest request, String key) {
		Locale locale = (Locale) request.getSession().getAttribute(SESSION_LOCALE_ATTR);
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
					log.debug("Class loader " + cl.getName() + " is not a Tomcat loader.");
				}
			}
		} catch (Exception ex) {
			if (log.isWarnEnabled()) {
				log.warn("Unable to clear Tomcat cache.", ex);
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
			log.warn("Unable to clear resource bundle cache.", ex);
		}
	}

	private static void clearMap(Class cl, Object obj, String name) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Field field = cl.getDeclaredField(name);
		field.setAccessible(true);
		Object cache = field.get(obj);
		synchronized (cache) {
			Class ccl = cache.getClass();
			Method clearMethod = ccl.getMethod("clear");
			clearMethod.invoke(cache);
		}
	}


}
