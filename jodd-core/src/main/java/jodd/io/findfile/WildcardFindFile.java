// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.util.InExRuleMatcher;
import jodd.util.InExRules;

/**
 * {@link FindFile} that matches file names using <code>*</code>, <code>?</code>
 * and <code>**</code> wildcards.
 *
 * @see jodd.io.findfile.RegExpFindFile
 */
public class WildcardFindFile extends FindFile<WildcardFindFile> {

	@Override
	protected InExRules createRulesEngine() {
		return new InExRules<String, String>(InExRuleMatcher.WILDCARD_PATH_RULE_MATCHER);
	}

}