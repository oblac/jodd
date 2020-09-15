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

package jodd.inex;

import java.util.ArrayList;
import java.util.List;

/**
 * A single-class rule engine for includes/excludes filtering logic. It can be used when
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
 * <p>
 * About generics: rule engine examine Values (V). Rules are defined as Definitions (D).
 * They are stored internally as R, that is used with Values.
 */
@Deprecated
public class InExRules<V, D, R> implements InExRuleMatcher<V, R> {

	public InExRules<String, String, String> create() {
		return new InExRules<>();
	}

	protected List<Rule<R>> rules;
	protected final InExRuleMatcher<V, R> inExRuleMatcher;
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
	public InExRules(final InExRuleMatcher<V, R> inExRuleMatcher) {
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
		return !rules.isEmpty();
	}

	/**
	 * Rule definition.
	 */
	public static class Rule<R> {
		public final R value;
		public final boolean include;

		public Rule(final R value, final boolean include) {
			this.value = value;
			this.include = include;
		}

		@Override
		public String toString() {
			return (include ? "+" : "-") + value.toString();
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			final Rule rule = (Rule) o;

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
	public R getRule(final int index) {
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
	 * Returns <code>true</code> if blacklist mode is set.
	 */
	public boolean isBlacklist() {
		return blacklist;
	}

	/**
	 * Enables <i>whitelist</i> mode - everything is <b>excluded</b> by default,
	 * and user set explicit includes.
	 */
	public void whitelist() {
		blacklist = false;
	}

	/**
	 * Returns <code>true</code> if whitelist mode is set.
	 */
	public boolean isWhitelist() {
		return !blacklist;
	}

	/**
	 * Sets blacklist or whitelist mode depending on rules. Smart mode
	 * determines the following:
	 * <ul>
	 *     <li>If there are only include rules, then the {@link #whitelist() whitelist} mode is set.</li>
	 *     <li>If there are only excluded rules, then the {@link #blacklist() blacklist} mode is set.</li>
	 *     <li>In any other case (both type of rules exist or no rules are set), then mode is not changed.</li>
	 * </ul>
	 * Should be called <b>after</b> all the rules are set, before matching starts.
	 */
	public void detectMode() {
		if (excludesCount == 0 && includesCount > 0) {
			whitelist();
		}
		else if (excludesCount > 0 && includesCount == 0) {
			blacklist();
		}
	}

	/**
	 * Adds include rule.
	 */
	public void include(final D rule) {
		addRule(rule, true);
	}

	/**
	 * Adds exclude rule.
	 */
	public void exclude(final D rule) {
		addRule(rule, false);
	}

	/**
	 * Adds a rule. Duplicates are not allowed and will be ignored.
	 */
	protected void addRule(final D ruleDefinition, final boolean include) {
		if (rules == null) {
			rules = new ArrayList<>();
		}

		if (include) {
			includesCount++;
		} else {
			excludesCount++;
		}

		final Rule<R> newRule = new Rule<>(makeRule(ruleDefinition), include);

		if (rules.contains(newRule)) {
			return;
		}

		rules.add(newRule);
	}

	protected R makeRule(final D rule) {
		return (R) rule;
	}

	/**
	 * Matches value against the set of rules using current white/black list mode.
	 */
	public boolean match(final V value) {
		return match(value, blacklist);
	}
	/**
	 * Matches value against the set of rules using provided white/black list mode.
	 */
	public boolean match(final V value, final boolean blacklist) {
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
	public boolean apply(final V value, final boolean flag) {
		return apply(value, blacklist, flag);
	}

	/**
	 * Applies rules on given flag. Flag is only changed if at least one rule
	 * matched. Otherwise, the same value is returned. This way you can
	 * chain several rules and have the rule engine change the flag
	 * only when a rule is matched.
	 */
	public boolean apply(final V value, final boolean blacklist, boolean flag) {
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
	protected boolean processIncludes(final V value, boolean include) {
		if (includesCount > 0) {
			if (!include) {
				for (final Rule<R> rule : rules) {
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
	protected boolean processExcludes(final V value, boolean include) {
		if (excludesCount > 0) {
			if (include) {
				for (final Rule<R> rule : rules) {
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
	@Override
	public boolean accept(final V value, final R rule, final boolean include) {
		return value.equals(rule);
	}

}
