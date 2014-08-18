// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import jodd.util.StringPool;
import jodd.util.StringTemplateParser;
import jodd.util.Wildcard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Props data storage for base and profile properties.
 * Properties can be lookuped and modified only through this
 * class.
 */
public class PropsData implements Cloneable {

	private static final int MAX_INNER_MACROS = 100;
	private static final String APPEND_SEPARATOR = ",";

	protected final HashMap<String, PropsEntry> baseProperties;
	protected final HashMap<String, Map<String, PropsEntry>> profileProperties;

	protected PropsEntry first;
	protected PropsEntry last;

	/**
	 * If set, duplicate props will be appended to the end, separated by comma.
	 */
	protected boolean appendDuplicateProps;

	/**
	 * When set, missing macros will be replaces with an empty string.
	 */
	protected boolean ignoreMissingMacros;

	/**
	 * When set, empty properties will be skipped.
	 */
	protected boolean skipEmptyProps = true;

	public PropsData() {
		this(new HashMap<String, PropsEntry>(), new HashMap<String, Map<String, PropsEntry>>());
	}

	protected PropsData(final HashMap<String, PropsEntry> properties, final HashMap<String, Map<String, PropsEntry>> profiles) {
		this.baseProperties = properties;
		this.profileProperties = profiles;
	}

	@Override
	public PropsData clone() {
		final HashMap<String, PropsEntry> newBase = new HashMap<String, PropsEntry>();
		final HashMap<String, Map<String, PropsEntry>> newProfiles = new HashMap<String, Map<String, PropsEntry>>();

		newBase.putAll(baseProperties);
		for (final Map.Entry<String, Map<String, PropsEntry>> entry : profileProperties.entrySet()) {
			final Map<String, PropsEntry> map = new HashMap<String, PropsEntry>(entry.getValue().size());
			map.putAll(entry.getValue());
			newProfiles.put(entry.getKey(), map);
		}

		final PropsData pd = new PropsData(newBase, newProfiles);
		pd.appendDuplicateProps = appendDuplicateProps;
		pd.ignoreMissingMacros = ignoreMissingMacros;
		pd.skipEmptyProps = skipEmptyProps;
		return pd;
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Puts key-value pair into the map, with respect of appending duplicate properties
	 */
	protected void put(final String profile, final Map<String, PropsEntry> map, final String key, final String value, final boolean append) {
		String realValue = value;
		if (append || appendDuplicateProps) {
			PropsEntry pv = map.get(key);
			if (pv != null) {
				realValue = pv.value + APPEND_SEPARATOR + realValue;
			}
		}
		PropsEntry propsEntry = new PropsEntry(key, realValue, profile, this);

		// update position pointers
		if (first == null) {
			first = propsEntry;
		} else {
			last.next = propsEntry;
		}
		last = propsEntry;

		// add to the map
		map.put(key, propsEntry);
	}

	// ---------------------------------------------------------------- properties

	/**
	 * Counts base properties.
	 */
	public int countBaseProperties() {
		return baseProperties.size();
	}

	/**
	 * Adds base property.
	 */
	public void putBaseProperty(final String key, final String value, final boolean append) {
		put(null, baseProperties, key, value, append);
	}

	/**
	 * Returns base property or <code>null</code> if it doesn't exist.
	 */
	public PropsEntry getBaseProperty(final String key) {
		return baseProperties.get(key);
	}

	// ---------------------------------------------------------------- profiles

	/**
	 * Counts profile properties. Note: this method is not
	 * that easy on execution.
	 */
	public int countProfileProperties() {
		final HashSet<String> profileKeys = new HashSet<String>();

		for (final Map<String, PropsEntry> map : profileProperties.values()) {
			for (final String key : map.keySet()) {
				if (!baseProperties.containsKey(key)) {
					profileKeys.add(key);
				}
			}
		}
		return profileKeys.size();
	}

	/**
	 * Adds profile property.
	 */
	public void putProfileProperty(final String key, final String value, final String profile, final boolean append) {
		Map<String, PropsEntry> map = profileProperties.get(profile);
		if (map == null) {
			map = new HashMap<String, PropsEntry>();
			profileProperties.put(profile, map);
		}
		put(profile, map, key, value, append);
	}

	/**
	 * Returns profile property.
	 */
	public PropsEntry getProfileProperty(final String profile, final String key) {
		final Map<String, PropsEntry> profileMap = profileProperties.get(profile);
		if (profileMap == null) {
			return null;
		}
		return profileMap.get(key);
	}


	// ---------------------------------------------------------------- lookup

	/**
	 * Lookup props value through profiles and base properties.
	 */
	protected String lookupValue(final String key, final String... profiles) {
		if (profiles != null) {
			for (String profile : profiles) {
				if (profile == null) {
					continue;
				}
				while (true) {
					final Map<String, PropsEntry> profileMap = this.profileProperties.get(profile);
					if (profileMap != null) {
						final PropsEntry value = profileMap.get(key);

						if (value != null) {
							return value.getValue(profiles);
						}
					}

					// go back with profile
					final int ndx = profile.lastIndexOf('.');
					if (ndx == -1) {
						break;
					}
					profile = profile.substring(0, ndx);
				}
			}
		}
		final PropsEntry value = getBaseProperty(key);

		if (value == null) {
			return null;
		}

		return value.getValue(profiles);
	}

	// ---------------------------------------------------------------- resolve

	/**
	 * Resolves all macros in this props set. Called on property lookup.
	 */
	public String resolveMacros(String value, final String... profiles) {
		// create string template parser that will be used internally
		StringTemplateParser stringTemplateParser = new StringTemplateParser();
		stringTemplateParser.setResolveEscapes(false);

		if (!ignoreMissingMacros) {
			stringTemplateParser.setReplaceMissingKey(false);
		} else {
			stringTemplateParser.setReplaceMissingKey(true);
			stringTemplateParser.setMissingKeyReplacement(StringPool.EMPTY);
		}

		final StringTemplateParser.MacroResolver macroResolver = new StringTemplateParser.MacroResolver() {
			public String resolve(String macroName) {
				return lookupValue(macroName, profiles);
			}
		};

		// start parsing
		int loopCount = 0;

		while (loopCount++ < MAX_INNER_MACROS) {
			final String newValue = stringTemplateParser.parse(value, macroResolver);

			if (newValue.equals(value)) {
				break;
			}

			if (skipEmptyProps) {
				if (newValue.length() == 0) {
					return null;
				}
			}

			value = newValue;
		}

		return value;
	}

	// ---------------------------------------------------------------- extract

	/**
	 * Extract props to target map.
	 */
	public void extract(final Map target, final String[] profiles, final String[] wildcardPatterns) {
		if (profiles != null) {
			for (String profile : profiles) {
				while (true) {
					final Map<String, PropsEntry> map = this.profileProperties.get(profile);
					if (map != null) {
						extractMap(target, map, profiles, wildcardPatterns);
					}

					final int ndx = profile.indexOf('.');
					if (ndx == -1) {
						break;
					}
					profile = profile.substring(0, ndx);
				}
			}
		}
		extractMap(target, this.baseProperties, profiles, wildcardPatterns);
	}

	@SuppressWarnings("unchecked")
	protected void extractMap(final Map target, final Map<String, PropsEntry> map, final String[] profiles, final String[] wildcardPatterns) {
		for (Map.Entry<String, PropsEntry> entry : map.entrySet()) {
			final String key = entry.getKey();

			if (wildcardPatterns != null) {
				if (Wildcard.matchOne(key, wildcardPatterns) == -1) {
					continue;
				}
			}

			if (!target.containsKey(key)) {
				target.put(key, entry.getValue().getValue(profiles));
			}
		}
	}

}