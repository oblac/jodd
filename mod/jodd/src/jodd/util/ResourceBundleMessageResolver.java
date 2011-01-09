// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.util.Locale;
import java.util.List;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.MissingResourceException;

/**
 * Resolves messages from resource bundles.
 */
public class ResourceBundleMessageResolver {

	protected Locale fallbackLocale = LocaleUtil.getLocale("en");
	protected String fallbackBundlename = "messages";
	protected List<String> defaultBundles = new LinkedList<String>();
	protected boolean cacheResourceBundles = true;

	public void addDefaultBundle(String bundleName) {
		defaultBundles.add(bundleName);
	}

	public void deleteAllDefaultBundles() {
		defaultBundles.clear();
	}

	// ---------------------------------------------------------------- messages

	/**
	 * Calculates indexedTextName (collection[*]) if applicable.
	 */
	private String calcIndexKey(String key) {
		String indexedKey = null;
		if (key.indexOf('[') != -1) {
			int i = -1;
			indexedKey = key;
			while ((i = indexedKey.indexOf('[', i + 1)) != -1) {
				int j = indexedKey.indexOf(']', i);
				String a = indexedKey.substring(0, i);
				String b = indexedKey.substring(j);
				indexedKey = a + "[*" + b;
			}
		}
		return indexedKey;
	}



	private String getMessage(String bundleName, Locale locale, String key, String indexedKey) {
		String msg = getMessage(bundleName, locale, key);
		if (msg != null) {
			return msg;
		}
		if (indexedKey != null) {
			msg = getMessage(bundleName, locale, indexedKey);
			if (msg != null) {
				return msg;
			}
		}
		return null;
	}

	/**
	 * Finds messages in the provided bundle. If message not found, all parent bundles will be
	 * examined until the root bundle. At the end, if still no success, all default
	 * bundles will be examined. Returns <code>null</code> if key is not found.
	 */
	public String findMessage(String bundleName, Locale locale, String key) {

		String indexedKey = calcIndexKey(key);

		// hierarchy
		String name = bundleName;
		while (true) {
			String msg = getMessage(name, locale, key, indexedKey);
			if (msg != null) {
				return msg;
			}

			if (bundleName == null || bundleName.length() == 0) {
				break;
			}
			int ndx = bundleName.lastIndexOf('.');
			if (ndx == -1) {
				bundleName = null;
				name = fallbackBundlename;
			} else {
				bundleName = bundleName.substring(0, ndx);
				name = bundleName + '.' + fallbackBundlename;
			}
		}

		// default bundles
		for (String bname : defaultBundles) {
			String msg = getMessage(bname, locale, key, indexedKey);
			if (msg != null) {
				return msg;
			}
		}

		return null;
	}

	/**
	 * Finds message in default bundles only, starting from fallback bundlename.
	 */
	public String findDefaultMessage(Locale locale, String key) {
		String indexedKey = calcIndexKey(key);

		String msg = getMessage(fallbackBundlename, locale, key, indexedKey);
		if (msg != null) {
			return msg;
		}

		for (String bname : defaultBundles) {
			msg = getMessage(bname, locale, key, indexedKey);
			if (msg != null) {
				return msg;
			}
		}
		return null;
	}



	/**
	 * Gets the message from the named resource bundle. Performs the failback only when
	 * bundle name or locale are not specified (i.e. are <code>null</code>).
	 */
	public String getMessage(String bundleName, Locale locale, String key) {
		ResourceBundle bundle = findResourceBundle(bundleName, locale);
		if (bundle == null) {
			return null;
		}

/*		//jdk6:
		if (bundle.containsKey(key) == false) {
			return null;
		}
*/
		try {
			return bundle.getString(key);
		} catch (MissingResourceException mrex) {
			return null;
		}
	}

	// ---------------------------------------------------------------- resource bundles

	protected final Set<String> misses = new HashSet<String>();
	protected final Map<String, ResourceBundle> notmisses = new HashMap<String, ResourceBundle>();

	/**
	 * Finds resource bundle by it's name. Missed and founded resource bundles are cached for
	 * better performances. Returns <code>null</code> if resource bundle is missing.
	 */
	public ResourceBundle findResourceBundle(String bundleName, Locale locale) {
		if (bundleName == null) {
			bundleName = fallbackBundlename;
		}
		if (locale == null) {
			locale = fallbackLocale;
		}
		if (cacheResourceBundles == false) {
			try {
				return getBundle(bundleName, locale, Thread.currentThread().getContextClassLoader());
			} catch (MissingResourceException mrex) {
				return null;
			}
		}
		String key = bundleName + '_' + LocaleUtil.resolveLocaleCode(locale);
		try {
			if (misses.contains(key) == false) {
				ResourceBundle bundle = notmisses.get(key);
				if (bundle == null) {
					bundle = getBundle(bundleName, locale, Thread.currentThread().getContextClassLoader());
					notmisses.put(key, bundle);
				}
				return bundle;
			}
		} catch (MissingResourceException mrex) {
			misses.add(key);
		}
		return null;
	}

	/**
	 * Returns specified bundle. Invoked every time if cache is disabled.
	 * Input arguments are always valid.
	 */
	protected ResourceBundle getBundle(String bundleName, Locale locale, ClassLoader classLoader) {
		return ResourceBundle.getBundle(bundleName, locale, classLoader);
	}


	// ---------------------------------------------------------------- accessors

	public String getFallbackBundlename() {
		return fallbackBundlename;
	}

	public void setFallbackBundlename(String fallbackBundlename) {
		this.fallbackBundlename = fallbackBundlename;
	}

	public Locale getFallbackLocale() {
		return fallbackLocale;
	}

	public void setFallbackLocale(Locale fallbackLocale) {
		this.fallbackLocale = fallbackLocale;
	}

	public void setFallbackLocale(String localeCode) {
		this.fallbackLocale = LocaleUtil.getLocale(localeCode);
	}

	public boolean isCacheResourceBundles() {
		return cacheResourceBundles;
	}

	public void setCacheResourceBundles(boolean cacheResourceBundles) {
		this.cacheResourceBundles = cacheResourceBundles;
	}
}
