// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

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

	protected final HashMap<String, PropsValue> properties;					// base properties

	protected final HashMap<String, Map<String, PropsValue>> profiles;		// profile properties

	protected final StringTemplateParser stringTemplateParser;

	/**
	 * If set, duplicate props will be appended to the end, separated by comma.
	 */
	protected boolean appendDuplicateProps;

	public PropsData() {
		this(new HashMap<String, PropsValue>(), new HashMap<String, Map<String, PropsValue>>());
	}

	protected PropsData(HashMap<String, PropsValue> properties, HashMap<String, Map<String, PropsValue>> profiles) {
		this.properties = properties;
		this.profiles = profiles;

		this.stringTemplateParser = new StringTemplateParser();
		stringTemplateParser.setResolveEscapes(false);
		stringTemplateParser.setReplaceMissingKey(false);
	}

	@Override
	public PropsData clone() {
		HashMap<String, PropsValue> newBase = new HashMap<String, PropsValue>();
		HashMap<String, Map<String, PropsValue>> newProfiles = new HashMap<String, Map<String, PropsValue>>();

		newBase.putAll(properties);
		for (Map.Entry<String, Map<String, PropsValue>> entry : profiles.entrySet()) {
			Map<String, PropsValue> map = new HashMap<String, PropsValue>(entry.getValue().size());
			map.putAll(entry.getValue());
			newProfiles.put(entry.getKey(), map);
		}

		PropsData pd = new PropsData(newBase, newProfiles);
		pd.appendDuplicateProps = appendDuplicateProps;

		return pd;
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Puts key-value pair into the map, with respect of appending duplicate properties
	 */
	protected void put(Map<String, PropsValue> map, String key, String value) {
		if (appendDuplicateProps) {
			PropsValue pv = map.get(key);
			if (pv != null) {
				value = pv.value + APPEND_SEPARATOR + value;
			}
		}
		map.put(key, new PropsValue(value));
	}

	// ---------------------------------------------------------------- properties

	/**
	 * Counts base properties.
	 */
	public int countBaseProperties() {
		return properties.size();
	}

	/**
	 * Adds base property.
	 */
	public void putBaseProperty(String key, String value) {
		put(properties, key, value);
	}

	/**
	 * Returns base property or <code>null</code> if it doesn't exist.
	 */
	public PropsValue getBaseProperty(String key) {
		return properties.get(key);
	}

	// ---------------------------------------------------------------- profiles

	/**
	 * Counts profile properties. Note: this method is not
	 * that easy on execution.
	 */
	public int countProfileProperties() {
		HashSet<String> profileKeys = new HashSet<String>();

		for (Map<String, PropsValue> map : profiles.values()) {
			for (String key : map.keySet()) {
				if (properties.containsKey(key) == false) {
					profileKeys.add(key);
				}
			}
		}

		return profileKeys.size();
	}

	/**
	 * Adds profile property.
	 */
	public void putProfileProperty(String key, String value, String profile) {
		Map<String, PropsValue> map = profiles.get(profile);
		if (map == null) {
			map = new HashMap<String, PropsValue>();
			profiles.put(profile, map);
		}
		put(map, key, value);
	}

	/**
	 * Returns profile property.
	 */
	public PropsValue getProfileProperty(String profile, String key) {
		Map<String, PropsValue> profileMap = profiles.get(profile);
		if (profileMap == null) {
			return null;
		}
		return profileMap.get(key);
	}


	// ---------------------------------------------------------------- lookup

	/**
	 * Lookup props value through profiles and base properties.
	 */
	protected String lookupValue(String key, String... profiles) {
		if (profiles != null) {
			nextprofile:
			for (String profile : profiles) {
				while (true) {
					Map<String, PropsValue> profileMap = this.profiles.get(profile);
					if (profileMap == null) {
						continue nextprofile;
					}
					PropsValue value = profileMap.get(key);
					if (value != null) {
						return value.getValue();
					}
					int ndx = profile.lastIndexOf('.');
					if (ndx == -1) {
						break;
					}
					profile = profile.substring(0, ndx);
				}
			}
		}
		PropsValue value = getBaseProperty(key);
		return value == null ?  null : value.getValue();
	}


	// ---------------------------------------------------------------- resolve

	/**
	 * Resolves all macros in this props set.
	 */
	public void resolveMacros() {
		int loopCount = 0;

		while (loopCount++ < MAX_INNER_MACROS) {
			boolean replaced = resolveMacros(this.properties, null);

			for (Map.Entry<String, Map<String, PropsValue>> entry : profiles.entrySet()) {
				String profile = entry.getKey();
				replaced = resolveMacros(entry.getValue(), profile) || replaced;
			}

			if (replaced == false) {
				break;
			}
		}
	}

	protected boolean resolveMacros(Map<String, PropsValue> map, final String profile) {
		boolean replaced = false;

		StringTemplateParser.MacroResolver macroResolver = new StringTemplateParser.MacroResolver() {
			public String resolve(String macroName) {
				return lookupValue(macroName, profile);
			}
		};

		for (Map.Entry<String, PropsValue> entry : map.entrySet()) {
			PropsValue pv = entry.getValue();

			String newValue = stringTemplateParser.parse(pv.value, macroResolver);

			if (newValue.equals(pv.value) == false) {
				pv.resolved = newValue;
				replaced = true;
			} else {
				pv.resolved = null;
			}
		}
		return replaced;
	}

	// ---------------------------------------------------------------- extract

	/**
	 * Extract props to target map.
	 */
	public void extract(Map target, String[] profiles, String[] wildcardPatterns) {
		if (profiles != null) {
			for (String profile : profiles) {
				while (true) {
					Map<String, PropsValue> map  = this.profiles.get(profile);
					if (map != null) {
						extractMap(target, map, wildcardPatterns);
					}

					int ndx = profile.indexOf('.');
					if (ndx == -1) {
						break;
					}
					profile = profile.substring(0, ndx);
				}
			}
		}
		extractMap(target, this.properties, wildcardPatterns);
	}

	@SuppressWarnings("unchecked")
	protected void extractMap(Map target, Map<String, PropsValue> map, String[] wildcardPatterns) {
		for (Map.Entry<String, PropsValue> entry : map.entrySet()) {
			String key = entry.getKey();

			if (wildcardPatterns != null) {
				if (Wildcard.matchOne(key, wildcardPatterns) == -1) {
					continue;
				}
			}

			if (!target.containsKey(key)) {
				target.put(key, entry.getValue().getValue());
			}
		}
	}

}
