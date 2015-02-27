// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Rule matcher.
 */
public interface InExRuleMatcher<T, R> {

	/**
	 * {@link jodd.util.Wildcard#match(String, String) Wilcard} rule matcher.
	 */
	public static final InExRuleMatcher<String, String> WILDCARD_RULE_MATCHER = new InExRuleMatcher<String, String>() {
		public boolean accept(String value, String rule, boolean include) {
			return Wildcard.match(value, rule);
		}
	};
	/**
	 * {@link jodd.util.Wildcard#matchPath(String, String)  Wilcard path} rule matcher.
	 */
	public static final InExRuleMatcher<String, String> WILDCARD_PATH_RULE_MATCHER = new InExRuleMatcher<String, String>() {
		public boolean accept(String value, String rule, boolean include) {
			return Wildcard.matchPath(value, rule);
		}
	};

	/**
	 * Match the value against the rule.
	 */
	boolean accept(T value, R rule, boolean include);

}