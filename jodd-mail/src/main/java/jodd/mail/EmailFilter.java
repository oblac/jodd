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

package jodd.mail;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.HeaderTerm;
import javax.mail.search.MessageIDTerm;
import javax.mail.search.MessageNumberTerm;
import javax.mail.search.NotTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;
import javax.mail.search.SizeTerm;
import javax.mail.search.SubjectTerm;

/**
 * <code>EmailFilter</code> helps in building boolean queries of search terms.
 * There are two ways how it can be used, both can be combined.
 * <p>
 * First way is constructing boolean expression using <i>groups</i>.
 * Just use {@link #and(EmailFilter...)}, {@link #or(EmailFilter...)}
 * and {@link #not(EmailFilter)} methods that takes any number
 * of filters that will be joined with chosen boolean operator.
 * <p>
 * Second way is more fluent. It may not be used to express some
 * complex queries, but for every-day use it would be enough.
 * Use methods {@link #and()} and {@link #or()} to define
 * how <b>all next</b> terms will be joined. Method {@link #not()}
 * marks the <b>one next</b> term to be added as NOT term.
 */
public class EmailFilter {

	boolean operatorAnd = true;
	boolean nextIsNot;

	/**
	 * Creates new Email filter.
	 */
	public static EmailFilter filter() {
		return new EmailFilter();
	}

	protected SearchTerm searchTerm;

	/**
	 * Defines filter for SUBJECT field.
	 */
	public EmailFilter subject(String subject) {
		SearchTerm subjectTerm = new SubjectTerm(subject);
		concat(subjectTerm);
		return this;
	}

	/**
	 * Defines filter for message id.
	 */
	public EmailFilter messageId(String messageId) {
		SearchTerm msgIdTerm = new MessageIDTerm(messageId);
		concat(msgIdTerm);
		return this;
	}

	/**
	 * Defines message number filter.
	 */
	public EmailFilter messageNumber(int messageNumber) {
		SearchTerm msgIdTerm = new MessageNumberTerm(messageNumber);
		concat(msgIdTerm);
		return this;
	}

	/**
	 * Defines filter for message id.
	 */
	public EmailFilter messageId(int messageId) {
		return messageId(String.valueOf(messageId));
	}

	/**
	 * Defines filter for FROM field.
	 */
	public EmailFilter from(String fromAddress) {
		SearchTerm fromTerm = new FromStringTerm(fromAddress);
		concat(fromTerm);
		return this;
	}

	/**
	 * Defines filter for TO field.
	 */
	public EmailFilter to(String toAddress) {
		SearchTerm toTerm = new RecipientStringTerm(Message.RecipientType.TO, toAddress);
		concat(toTerm);
		return this;
	}

	/**
	 * Defines filter for CC field.
	 */
	public EmailFilter cc(String ccAddress) {
		SearchTerm toTerm = new RecipientStringTerm(Message.RecipientType.CC, ccAddress);
		concat(toTerm);
		return this;
	}

	/**
	 * Defines filter for BCC field.
	 */
	public EmailFilter bcc(String bccAddress) {
		SearchTerm toTerm = new RecipientStringTerm(Message.RecipientType.BCC, bccAddress);
		concat(toTerm);
		return this;
	}

	/**
	 * Defines filter for many flags at once.
	 */
	public EmailFilter flags(Flags flags, boolean value) {
		SearchTerm flagTerm = new FlagTerm(flags, value);
		concat(flagTerm);
		return this;
	}

	/**
	 * Defines filter for single flag.
	 */
	public EmailFilter flag(Flags.Flag flag, boolean value) {
		Flags flags = new Flags();
		flags.add(flag);
		return flags(flags, value);
	}

	/**
	 * Defines filter for received date.
	 */
	public EmailFilter receivedDate(Operator operator, long milliseconds) {
		SearchTerm term = new ReceivedDateTerm(operator.value, new java.util.Date(milliseconds));
		concat(term);
		return this;
	}
	/**
	 * Defines filter for sent date.
	 */
	public EmailFilter sentDate(Operator operator, long milliseconds) {
		SearchTerm term = new SentDateTerm(operator.value, new java.util.Date(milliseconds));
		concat(term);
		return this;
	}

	/**
	 * Defines filter on a message body.
	 * All parts of the message that are of MIME type "text/*" are searched.
	 */
	public EmailFilter text(String pattern) {
		SearchTerm term = new BodyTerm(pattern);
		concat(term);
		return this;
	}

	/**
	 * Defines filter for header.
	 */
	public EmailFilter header(String headerName, String pattern) {
		SearchTerm term = new HeaderTerm(headerName, pattern);
		concat(term);
		return this;
	}

	/**
	 * Defines filter for message size.
	 */
	public EmailFilter size(Operator comparison, int size) {
		SearchTerm term = new SizeTerm(comparison.value, size);
		concat(term);
		return this;
	}

	/**
	 * Comparison operator.
	 */
	public enum Operator {
		EQ(javax.mail.search.ComparisonTerm.EQ),
		GE(javax.mail.search.ComparisonTerm.GE),
		GT(javax.mail.search.ComparisonTerm.GT),
		LE(javax.mail.search.ComparisonTerm.LE),
		LT(javax.mail.search.ComparisonTerm.LT),
		NE(javax.mail.search.ComparisonTerm.NE);

		private final int value;

		Operator(int value) {
			this.value = value;
		}
	}

	// ---------------------------------------------------------------- boolean

	/**
	 * Changes concatenation mode to AND.
	 */
	public EmailFilter and() {
		this.operatorAnd = true;
		return this;
	}
	/**
	 * Changes concatenation mode to OR.
	 */
	public EmailFilter or() {
		this.operatorAnd = false;
		return this;
	}

	/**
	 * Marks next condition to be NOT.
	 */
	public EmailFilter not() {
		this.nextIsNot = true;
		return this;
	}

	/**
	 * Defines AND group of filters.
	 */
	public EmailFilter and(EmailFilter... emailFilters) {
		SearchTerm[] searchTerms = new SearchTerm[emailFilters.length];

		for (int i = 0; i < emailFilters.length; i++) {
			searchTerms[i] = emailFilters[i].searchTerm;
		}

		concat(new AndTerm(searchTerms));
		return this;
	}

	/**
	 * Defines OR group of filters.
	 */
	public EmailFilter or(EmailFilter... emailFilters) {
		SearchTerm[] searchTerms = new SearchTerm[emailFilters.length];

		for (int i = 0; i < emailFilters.length; i++) {
			searchTerms[i] = emailFilters[i].searchTerm;
		}

		concat(new OrTerm(searchTerms));
		return this;
	}

	/**
	 * Appends single filter as NOT.
	 */
	public EmailFilter not(EmailFilter emailFilter) {
		SearchTerm searchTerm = new NotTerm(emailFilter.searchTerm);
		concat(searchTerm);
		return this;
	}

	// ---------------------------------------------------------------- concat

	/**
	 * Concatenates last search term with new one.
	 */
	protected void concat(SearchTerm searchTerm) {
		if (nextIsNot) {
			searchTerm = new NotTerm(searchTerm);
			nextIsNot = false;
		}
		if (operatorAnd) {
			and(searchTerm);
		} else {
			or(searchTerm);
		}
	}

	protected void and(SearchTerm searchTerm) {
		if (this.searchTerm == null) {
			this.searchTerm = searchTerm;
			return;
		}

		this.searchTerm = new AndTerm(this.searchTerm, searchTerm);
	}

	protected void or(SearchTerm searchTerm) {
		if (this.searchTerm == null) {
			this.searchTerm = searchTerm;
			return;
		}

		this.searchTerm = new OrTerm(this.searchTerm, searchTerm);
	}

	// ---------------------------------------------------------------- term

	/**
	 * Returns search term.
	 */
	public SearchTerm getSearchTerm() {
		return searchTerm;
	}

}