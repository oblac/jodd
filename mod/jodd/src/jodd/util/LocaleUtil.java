// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import java.text.NumberFormat;

/**
 * Locale utilities.
 */
public class LocaleUtil {

	/**
	 * Holds all per-Locale data.
	 */
	static class LocaleData {
		LocaleData(Locale locale) {
			this.locale = locale;
		}
		final Locale locale;
		DateFormatSymbolsEx dateFormatSymbols;
		NumberFormat numberFormat;
	}


	// ---------------------------------------------------------------- locale cache

	protected static Map<String, LocaleData> locales = new HashMap<String, LocaleData>();

	/**
	 * Lookups for locale data and creates new if it doesn't exist.
	 */
	protected static LocaleData lookupLocaleData(String code) {
		LocaleData localeData = locales.get(code);
		if (localeData == null) {
			String[] data = decodeLocaleCode(code);
			localeData = new LocaleData(new Locale(data[0], data[1], data[2]));
			locales.put(code, localeData);
		}
		return localeData;
	}

	protected static LocaleData lookupLocaleData(Locale locale) {
		return lookupLocaleData(resolveLocaleCode(locale));
	}


	// ---------------------------------------------------------------- get cached locales

	/**
	 * Returns Locale from cache.
	 */
	public static Locale getLocale(String language, String country, String variant) {
		LocaleData localeData = lookupLocaleData(resolveLocaleCode(language, country, variant));
		return localeData.locale;
	}

	/**
	 * Returns Locale from cache.
	 */
	public static Locale getLocale(String language, String country) {
		return getLocale(language, country, null);
	}

	/**
	 * Returns Locale from cache where Locale may be specified also using language code.
	 * Converts a locale string like "en", "en_US" or "en_US_win" to <b>new</b> Java locale object.
	 */
	public static Locale getLocale(String languageCode) {
		LocaleData localeData = lookupLocaleData(languageCode);
		return localeData.locale;
	}

	// ---------------------------------------------------------------- convert

	/**
	 * Transforms locale data to locale code. <code>null</code> values are allowed.
	 */
	public static String resolveLocaleCode(String lang, String country, String variant) {
		StringBuilder code = new StringBuilder(lang);
		if (StringUtil.isEmpty(country) == false) {
			code.append('_').append(country);
			if (StringUtil.isEmpty(variant) == false) {
				code.append('_').append(variant);
			}
		}
		return code.toString();
	}

	/**
	 * Resolves locale code from locale.
	 */
	public static String resolveLocaleCode(Locale locale) {
		return resolveLocaleCode(locale.getLanguage(), locale.getCountry(), locale.getVariant());
	}

	/**
	 * Decodes locale code in string array that can be used for <code>Locale</code> constructor.
	 */
	public static String[] decodeLocaleCode(String localeCode) {
		String result[] = new String[3];
		String[] data = StringUtil.splitc(localeCode, '_');
		result[0] = data[0];
		result[1] = result[2] = StringPool.EMPTY;
		if (data.length >= 2) {
			result[1] = data[1];
			if (data.length >= 3) {
				result[2] = data[2];
			}
		}
		return result;
	}

	// ---------------------------------------------------------------- locale elements

	/**
	 * Returns <code>DateFormatSymbols</code> instance for specified locale.
	 */
	public static DateFormatSymbolsEx getDateFormatSymbols(Locale locale) {
		LocaleData localeData = lookupLocaleData(locale);
		DateFormatSymbolsEx dfs = localeData.dateFormatSymbols;
		if (dfs == null) {
			dfs = new DateFormatSymbolsEx(locale);
			localeData.dateFormatSymbols = dfs;
		}
		return dfs;
	}

	/**
	 * Returns <code>NumberFormat</code> instance for specified locale.
	 */
	public static NumberFormat getNumberFormat(Locale locale) {
		LocaleData localeData = lookupLocaleData(locale);
		NumberFormat nf = localeData.numberFormat;
		if (nf == null) {
			nf = NumberFormat.getInstance(locale);
			localeData.numberFormat = nf;
		}
		return nf;
	}

}
