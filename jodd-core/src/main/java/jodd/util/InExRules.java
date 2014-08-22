// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.util.ArrayList;
import java.util.List;

/**
 * One-class rule engine for includes/excludes logic. It can be used when
 * set of objects has to filtered using includes and excludes rules.
 * For example, when filtering files by file name etc.
 * <p>
 * The logic is following:
 * <ul>
 *     <li>by default, all values are included</li>
 *     <li>if there is excluded rule defined, they will be processed</li>
 *     <li>if value is excluded, than the included rules are processed</li>
 *     <li>if excluded rule is <i>important</i>, it will immediately apply, without processing included rules</li>
 *     <li>if excluded rules are not set and there is at least one included rule, implicit exclusion of all entries is assumed</li>
 * </ul>
 * <p>
 * All Jodd classes that filters something uses this class to unify the
 * behavior across the library.
 */
public class InExRules<T, R> implements InExRuleMatcher<T, R> {

	protected List<Rule<R>> rules;
	protected final InExRuleMatcher<T, R> inExRuleMatcher;
	protected int includesCount;
	protected int excludesCount;

	/**
	 * Creates default instance.
	 */
	public InExRules() {
		this.inExRuleMatcher = this;
	}

	/**
	 * Creates instance that uses provided matcher.
	 */
	public InExRules(InExRuleMatcher<T, R> inExRuleMatcher) {
		this.inExRuleMatcher = inExRuleMatcher;
	}

	/**
	 * Rule definition.
	 */
	public static class Rule<R> {
		public final R value;
		public final boolean include;
		public final boolean important;

		public Rule(R value, boolean include, boolean important) {
			this.value = value;
			this.include = include;
			this.important = important;
		}
	}

	/**
	 * Resets all rules.
	 */
	public void reset() {
		if (rules != null) {
			rules.clear();
		}
		includesCount = excludesCount = 0;
	}

	/**
	 * Adds include rule.
	 */
	public void include(R rule) {
		addRule(rule, true, false);
	}

	/**
	 * Adds exclude rule.
	 */
	public void exclude(R rule) {
		addRule(rule, false, false);
	}
	/**
	 * Adds important exclude rule. Important exclude rules are terminal,
	 * i.e. include rules will not be processed if important exclude rule
	 * matches.
	 */
	public void exclude(R rule, boolean important) {
		addRule(rule, false, important);
	}

	/**
	 * Adds a rule.
	 */
	protected void addRule(R rule, boolean include, boolean important) {
		if (rules == null) {
			rules = new ArrayList<Rule<R>>();
		}

		if (include) {
			includesCount++;
		} else {
			excludesCount++;
		}

		rules.add(new Rule<R>(rule, include, important));
	}

	/**
	 * Matches value against set of rules.
	 */
	public boolean match(T value) {
		if (rules == null) {
			return true;
		}

		boolean include = true;

		// first process excludes
		if (excludesCount > 0) {
			for (Rule<R> element : rules) {
				if (element.include) {
					continue;
				}

				if (element.important) {
					// all important elements must be processed
					if (inExRuleMatcher.accept(value, element.value, false)) {
						return false;
					}
				}
				else {
					if (inExRuleMatcher.accept(value, element.value, false)) {
						include = false;
					}
				}
			}
		}

		// then process includes
		if (includesCount > 0) {
			if (excludesCount == 0) {
				// special case - when only includes are added
				include = false;
			}
			if (!include) {
				for (Rule<R> element : rules) {
					if (!element.include) {
						continue;
					}

					if (inExRuleMatcher.accept(value, element.value, true)) {
						include = true;
						break;
					}
				}
			}
		}

		return include;
	}


	/**
	 * Matches value against single rule. By default performs <code>equals</code> on value
	 * against the rule.
	 */
	public boolean accept(T value, R rule, boolean include) {
		return value.equals(rule);
	}

}