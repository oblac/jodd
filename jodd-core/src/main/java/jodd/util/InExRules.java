// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.util.ArrayList;
import java.util.List;

/**
 * One-class rule engine for includes/excludes logic. It can be used when
 * set of objects has to filtered using includes and excludes rules.
 * For example, when filtering files by file name etc.
 * <p>
 * Rule engine works in one of two modes:
 * <ul>
 *		<li><i>blacklist</i> - when any input is allowed by default and when you specify
 *			explicit excludes.
 *		</li>
 *		<li><i>whitelist</i> - when any input is disabled by default and when you specify
 *			explicit includes.
 *		</li>
 * </ul>
 * <p>
 * The logic of this rule engine depends on the current mode. In both cases,
 * always the inverse rules are considered first. For example, for <i>blacklist</i>
 * mode, engine first examine excludes, and then includes. This way you can
 * set any filter combination.
 * <p>
 * All Jodd classes that filters something uses this class to unify the
 * behavior across the Jodd library.
 */
public class InExRules<T, R> implements InExRuleMatcher<T, R> {

	protected List<Rule<R>> rules;
	protected final InExRuleMatcher<T, R> inExRuleMatcher;
	protected int includesCount;
	protected int excludesCount;
	protected boolean blacklist = true;

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
	 * Returns total number of all rules.
	 */
	public int totalRules() {
		if (rules == null) {
			return 0;
		}
		return rules.size();
	}

	/**
	 * Returns total number of include rules.
	 */
	public int totalIncludeRules() {
		return includesCount;
	}

	/**
	 * Returns total number of exclude rules.
	 */
	public int totalExcludeRules() {
		return excludesCount;
	}

	/**
	 * Returns <code>true</code> if rule engine has at least one rule set.
	 */
	public boolean hasRules() {
		if (rules == null) {
			return false;
		}
		return rules.size() > 0;
	}

	/**
	 * Rule definition.
	 */
	public static class Rule<R> {
		public final R value;
		public final boolean include;

		public Rule(R value, boolean include) {
			this.value = value;
			this.include = include;
		}

		@Override
		public String toString() {
			return (include ? "+" : "-") + value.toString();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			Rule rule = (Rule) o;

			if (include != rule.include) {
				return false;
			}
			if (!value.equals(rule.value)) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			int result = value.hashCode();
			result = 31 * result + (include ? 1 : 0);
			return result;
		}
	}

	/**
	 * Returns rule's value on given index.
	 */
	public R getRule(int index) {
		return rules.get(index).value;
	}

	/**
	 * Resets all the rules in this rule engine.
	 */
	public void reset() {
		if (rules != null) {
			rules.clear();
		}
		includesCount = excludesCount = 0;
		blacklist = true;
	}

	/**
	 * Enables <i>blacklist</i> mode - everything is <b>included</b> by default,
	 * and user sets explicit excludes.
	 */
	public void blacklist() {
		blacklist = true;
	}

	/**
	 * Enables <i>whitelist</i> mode - everything is <b>excluded</b> by default,
	 * and user set explicit includes.
	 */
	public void whitelist() {
		blacklist = false;
	}

	/**
	 * Adds include rule.
	 */
	public void include(R rule) {
		addRule(rule, true);
	}

	/**
	 * Adds exclude rule.
	 */
	public void exclude(R rule) {
		addRule(rule, false);
	}

	/**
	 * Adds a rule. Duplicates are not allowed and will be ignored.
	 */
	protected void addRule(R rule, boolean include) {
		if (rules == null) {
			rules = new ArrayList<Rule<R>>();
		}

		if (include) {
			includesCount++;
		} else {
			excludesCount++;
		}

		Rule<R> newRule = new Rule<R>(rule, include);

		if (rules.contains(newRule)) {
			return;
		}

		rules.add(newRule);
	}

	/**
	 * Matches value against the set of rules using current white/black list mode.
	 */
	public boolean match(T value) {
		return match(value, blacklist);
	}
	/**
	 * Matches value against the set of rules using provided white/black list mode.
	 */
	public boolean match(T value, boolean blacklist) {
		if (rules == null) {
			return blacklist;
		}

		boolean include = blacklist;

		if (include) {
			include = processExcludes(value, true);
			include = processIncludes(value, include);
		}
		else {
			include = processIncludes(value, false);
			include = processExcludes(value, include);
		}

		return include;
	}

	/**
	 * Applies rules on given flag using current black/white list mode.
	 * @see #apply(Object, boolean, boolean)
	 */
	public boolean apply(T value, boolean flag) {
		return apply(value, blacklist, flag);
	}

	/**
	 * Applies rules on given flag. Flag is only changed if at least one rule
	 * matched. Otherwise, the same value is returned. This way you can
	 * chain several rules and have the rule engine change the flag
	 * only when a rule is matched.
	 */
	public boolean apply(T value, final boolean blacklist, boolean flag) {
		if (rules == null) {
			return flag;
		}

		if (blacklist) {
			flag = processExcludes(value, flag);
			flag = processIncludes(value, flag);
		}
		else {
			flag = processIncludes(value, flag);
			flag = processExcludes(value, flag);
		}

		return flag;
	}

	/**
	 * Process includes rules.
	 */
	protected boolean processIncludes(T value, boolean include) {
		if (includesCount > 0) {
			if (!include) {
				for (Rule<R> rule : rules) {
					if (!rule.include) {
						continue;
					}

					if (inExRuleMatcher.accept(value, rule.value, true)) {
						include = true;
						break;
					}
				}
			}
		}
		return include;
	}

	/**
	 * Process excludes rules.
	 */
	protected boolean processExcludes(T value, boolean include) {
		if (excludesCount > 0) {
			if (include) {
				for (Rule<R> rule : rules) {
					if (rule.include) {
						continue;
					}

					if (inExRuleMatcher.accept(value, rule.value, false)) {
						include = false;
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