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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Resolves messages from resource bundles.
 */
public class ResourceBundleMessageResolver {

	protected Locale fallbackLocale = Locale.forLanguageTag("en");
	protected String fallbackBundlename = "messages";
	protected List<String> defaultBundles = new ArrayList<>();
	protected boolean cacheResourceBundles = true;

	public void addDefaultBundle(final String bundleName) {
		defaultBundles.add(bundleName);
	}

	public void deleteAllDefaultBundles() {
		defaultBundles.clear();
	}

	// ---------------------------------------------------------------- messages

	/**
	 * Calculates indexedTextName (collection[*]) if applicable.
	 */
	private String calcIndexKey(final String key) {
		String indexedKey = null;
		if (key.indexOf('[') != -1) {
			int i = -1;
			indexedKey = key;
			while ((i = indexedKey.indexOf('[', i + 1)) != -1) {
				final int j = indexedKey.indexOf(']', i);
				final String a = indexedKey.substring(0, i);
				final String b = indexedKey.substring(j);
				indexedKey = a + "[*" + b;
			}
		}
		return indexedKey;
	}



	private String getMessage(final String bundleName, final Locale locale, final String key, final String indexedKey) {
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
	public String findMessage(String bundleName, final Locale locale, final String key) {

		final String indexedKey = calcIndexKey(key);

		// hierarchy
		String name = bundleName;
		while (true) {
			final String msg = getMessage(name, locale, key, indexedKey);
			if (msg != null) {
				return msg;
			}

			if (bundleName == null || bundleName.length() == 0) {
				break;
			}
			final int ndx = bundleName.lastIndexOf('.');
			if (ndx == -1) {
				bundleName = null;
				name = fallbackBundlename;
			} else {
				bundleName = bundleName.substring(0, ndx);
				name = bundleName + '.' + fallbackBundlename;
			}
		}

		// default bundles
		for (final String bname : defaultBundles) {
			final String msg = getMessage(bname, locale, key, indexedKey);
			if (msg != null) {
				return msg;
			}
		}

		return null;
	}

	/**
	 * Finds message in default bundles only, starting from fallback bundlename.
	 */
	public String findDefaultMessage(final Locale locale, final String key) {
		final String indexedKey = calcIndexKey(key);

		String msg = getMessage(fallbackBundlename, locale, key, indexedKey);
		if (msg != null) {
			return msg;
		}

		for (final String bname : defaultBundles) {
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
	public String getMessage(final String bundleName, final Locale locale, final String key) {
		final ResourceBundle bundle = findResourceBundle(bundleName, locale);
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
		} catch (final MissingResourceException mrex) {
			return null;
		}
	}

	// ---------------------------------------------------------------- resource bundles

	protected final Set<String> misses = new HashSet<>();
	protected final Map<String, ResourceBundle> notmisses = new HashMap<>();

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
		if (!cacheResourceBundles) {
			try {
				return getBundle(bundleName, locale, ClassLoaderUtil.getDefaultClassLoader());
			} catch (final MissingResourceException ignore) {
				return null;
			}
		}
		final String key = bundleName + '_' + locale.toLanguageTag();
		try {
			if (!misses.contains(key)) {
				ResourceBundle bundle = notmisses.get(key);
				if (bundle == null) {
					bundle = getBundle(bundleName, locale, ClassLoaderUtil.getDefaultClassLoader());
					notmisses.put(key, bundle);
				}
				return bundle;
			}
		} catch (final MissingResourceException ignore) {
			misses.add(key);
		}
		return null;
	}

	/**
	 * Returns specified bundle. Invoked every time if cache is disabled.
	 * Input arguments are always valid.
	 */
	protected ResourceBundle getBundle(final String bundleName, final Locale locale, final ClassLoader classLoader) {
		return ResourceBundle.getBundle(bundleName, locale, classLoader);
	}


	// ---------------------------------------------------------------- accessors

	public String getFallbackBundlename() {
		return fallbackBundlename;
	}

	public void setFallbackBundlename(final String fallbackBundlename) {
		this.fallbackBundlename = fallbackBundlename;
	}

	public Locale getFallbackLocale() {
		return fallbackLocale;
	}

	public void setFallbackLocale(final Locale fallbackLocale) {
		this.fallbackLocale = fallbackLocale;
	}

	public void setFallbackLocale(final String localeCode) {
		this.fallbackLocale = Locale.forLanguageTag(localeCode);
	}

	public boolean isCacheResourceBundles() {
		return cacheResourceBundles;
	}

	public void setCacheResourceBundles(final boolean cacheResourceBundles) {
		this.cacheResourceBundles = cacheResourceBundles;
	}
}
