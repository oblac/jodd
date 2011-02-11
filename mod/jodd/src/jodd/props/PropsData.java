// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import jodd.bean.BeanTemplateResolver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

/**
 * Props data storage for base and profile properties.
 * Properties can be lookuped and modified only through this
 * class.
 */
public class PropsData implements Cloneable {

	private static final int MAX_INNER_MACROS = 100;

	protected final HashMap<String, PropsValue> properties;					// base properties

	protected final HashMap<String, Map<String, PropsValue>> profiles;		// profile properties

	public PropsData() {
		this.properties = new HashMap<String, PropsValue>();
		this.profiles = new HashMap<String, Map<String, PropsValue>>();
	}

	protected PropsData(HashMap<String, PropsValue> properties, HashMap<String, Map<String, PropsValue>> profiles) {
		this.properties = properties;
		this.profiles = profiles;
	}

	@Override
	public PropsData clone() {
		HashMap<String, PropsValue>  newProperties = new HashMap<String, PropsValue>();
		HashMap<String, Map<String, PropsValue>> newProfiles = new HashMap<String, Map<String, PropsValue>>();

		newProperties.putAll(properties);
		for (Map.Entry<String, Map<String, PropsValue>> entry : profiles.entrySet()) {
			Map<String, PropsValue> map = new HashMap<String, PropsValue>(entry.getValue().size());
			map.putAll(entry.getValue());
			newProfiles.put(entry.getKey(), map);
		}

		return new PropsData(newProperties, newProfiles);
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
		properties.put(key, new PropsValue(value));
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
		map.put(key, new PropsValue(value));
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
		BeanTemplateResolver resolver = new BeanTemplateResolver() {
			public Object resolve(String name) {
				return lookupValue(name, profile);
			}
		};
		for (Map.Entry<String, PropsValue> entry : map.entrySet()) {
			PropsValue pv = entry.getValue();
			if (pv.resolveValue(resolver)) {
				replaced = true;
			}
		}
		return replaced;
	}

	// ---------------------------------------------------------------- extract

	/**
	 * Extract props as <code>Properties</code>.
	 */
	public Properties extractProperties(String... profiles) {
		Properties properties = new Properties();
		if (profiles != null) {
			for (String profile : profiles) {
				while (true) {
					Map<String, PropsValue> map  = this.profiles.get(profile);
					if (map != null) {
						extract(properties, map);
					}

					int ndx = profile.indexOf('.');
					if (ndx == -1) {
						break;
					}
					profile = profile.substring(0, ndx);
				}
			}
		}
		extract(properties, this.properties);
		return properties;
	}

	protected void extract(Properties properties, Map<String, PropsValue> map) {
		for (Map.Entry<String, PropsValue> entry : map.entrySet()) {
			String key = entry.getKey();
			String existingValue = properties.getProperty(key);
			if (existingValue == null) {
				properties.setProperty(key, entry.getValue().getValue());
			}
		}
	}

}
