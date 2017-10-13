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

package jodd.props;

import jodd.util.StringPool;
import jodd.util.StringTemplateParser;
import jodd.util.StringUtil;
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
		final HashMap<String, PropsEntry> newBase = new HashMap<>();
		final HashMap<String, Map<String, PropsEntry>> newProfiles = new HashMap<>();

		newBase.putAll(baseProperties);
		for (final Map.Entry<String, Map<String, PropsEntry>> entry : profileProperties.entrySet()) {
			final Map<String, PropsEntry> map = new HashMap<>(entry.getValue().size());
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
		final HashSet<String> profileKeys = new HashSet<>();

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
			map = new HashMap<>();
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
				String[] lookupProfiles = profiles;

				int leftIndex = macroName.indexOf('<');
				if (leftIndex != -1) {
					int rightIndex = macroName.indexOf('>');

					String profiles = macroName.substring(leftIndex + 1, rightIndex);
					macroName = macroName.substring(0, leftIndex).concat(macroName.substring(rightIndex + 1));

					lookupProfiles = StringUtil.splitc(profiles, ',');

					StringUtil.trimAll(lookupProfiles);
				}

				return lookupValue(macroName, lookupProfiles);
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
	 * Extracts props to target map. This is all-in-one method, that does many things at once.
	 */
	public Map extract(Map target, final String[] profiles, final String[] wildcardPatterns, String prefix) {
		if (target == null) {
			target = new HashMap();
		}

		// make sure prefix ends with a dot
		if (prefix != null) {
			if (!StringUtil.endsWithChar(prefix, '.')) {
				prefix += StringPool.DOT;
			}
		}

		if (profiles != null) {
			for (String profile : profiles) {
				while (true) {
					final Map<String, PropsEntry> map = this.profileProperties.get(profile);
					if (map != null) {
						extractMap(target, map, profiles, wildcardPatterns, prefix);
					}

					final int ndx = profile.indexOf('.');
					if (ndx == -1) {
						break;
					}
					profile = profile.substring(0, ndx);
				}
			}
		}

		extractMap(target, this.baseProperties, profiles, wildcardPatterns, prefix);

		return target;
	}

	@SuppressWarnings("unchecked")
	protected void extractMap(
			final Map target,
			final Map<String, PropsEntry> map,
			final String[] profiles,
			final String[] wildcardPatterns,
			final String prefix
			) {

		for (Map.Entry<String, PropsEntry> entry : map.entrySet()) {
			String key = entry.getKey();

			if (wildcardPatterns != null) {
				if (Wildcard.matchOne(key, wildcardPatterns) == -1) {
					continue;
				}
			}

			// shorten the key
			if (prefix != null) {
				if (!key.startsWith(prefix)) {
					continue;
				}
				key = key.substring(prefix.length());
			}

			// only append if target DOES NOT contain the key
			if (!target.containsKey(key)) {
				target.put(key, entry.getValue().getValue(profiles));
			}
		}
	}

}