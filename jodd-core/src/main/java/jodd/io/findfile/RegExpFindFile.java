// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.util.InExRules;

import java.util.regex.Pattern;

/**
 * Simple {@link FindFile} that matches file names with regular expression pattern.
 * @see jodd.io.findfile.WildcardFindFile
 */
public class RegExpFindFile extends FindFile<RegExpFindFile> {

	@Override
	protected InExRules createRulesEngine() {
		return new InExRules<String, Object>() {

			@Override
			protected void addRule(Object rule, boolean include, boolean important) {
				Pattern pattern = Pattern.compile((String) rule);
				super.addRule(pattern, include, important);
			}

			@Override
			public boolean accept(String path, Object pattern, boolean include) {
				return ((Pattern) pattern).matcher(path).matches();
			}
		};
	}

}
