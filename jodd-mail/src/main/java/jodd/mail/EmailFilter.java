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
import javax.mail.Flags.Flag;
import javax.mail.Header;
import javax.mail.Message.RecipientType;
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
import java.util.Date;

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

	/**
	 * The {@link SearchTerm} to be used.
	 */
	protected SearchTerm searchTerm;

	/**
	 * Defines filter for SUBJECT field.
	 *
	 * @param subject The SUBJECT.
	 * @return this
	 */
	public EmailFilter subject(final String subject) {
		final SearchTerm subjectTerm = new SubjectTerm(subject);
		concat(subjectTerm);
		return this;
	}

	/**
	 * Defines filter for message id.
	 *
	 * @param messageId The message ID.
	 * @return this
	 */
	public EmailFilter messageId(final String messageId) {
		final SearchTerm msgIdTerm = new MessageIDTerm(messageId);
		concat(msgIdTerm);
		return this;
	}

	/**
	 * Defines filteer for message number.
	 *
	 * @param messageNumber The message number.
	 * @return this
	 */
	public EmailFilter messageNumber(final int messageNumber) {
		final SearchTerm msgIdTerm = new MessageNumberTerm(messageNumber);
		concat(msgIdTerm);
		return this;
	}

	/**
	 * Defines filter for FROM field.
	 *
	 * @param fromAddress The FROM address
	 * @return this
	 */
	public EmailFilter from(final String fromAddress) {
		final SearchTerm fromTerm = new FromStringTerm(fromAddress);
		concat(fromTerm);
		return this;
	}

	/**
	 * Defines filter for TO field.
	 *
	 * @param toAddress The TO address.
	 * @return this
	 */
	public EmailFilter to(final String toAddress) {
		final SearchTerm toTerm = new RecipientStringTerm(RecipientType.TO, toAddress);
		concat(toTerm);
		return this;
	}

	/**
	 * Defines filter for CC field.
	 *
	 * @param ccAddress CC addreses.
	 * @return this
	 */
	public EmailFilter cc(final String ccAddress) {
		final SearchTerm toTerm = new RecipientStringTerm(RecipientType.CC, ccAddress);
		concat(toTerm);
		return this;
	}

	/**
	 * Defines filter for BCC field.
	 *
	 * @param bccAddress BCC address.
	 * @return this
	 */
	public EmailFilter bcc(final String bccAddress) {
		final SearchTerm toTerm = new RecipientStringTerm(RecipientType.BCC, bccAddress);
		concat(toTerm);
		return this;
	}

	/**
	 * Defines filter for many flags at once.
	 *
	 * @param flags The {@link Flags} to filter on.
	 * @param value The {@link Flag} setting to check for.
	 * @return this
	 */
	public EmailFilter flags(final Flags flags, final boolean value) {
		final SearchTerm flagTerm = new FlagTerm(flags, value);
		concat(flagTerm);
		return this;
	}

	/**
	 * Defines filter for single flag.
	 *
	 * @param flag  The flag to filter on.
	 * @param value The {@link Flag} setting to check for.
	 * @return this
	 */
	public EmailFilter flag(final Flag flag, final boolean value) {
		final Flags flags = new Flags();
		flags.add(flag);
		return flags(flags, value);
	}

	/**
	 * Defines filter for received date.
	 *
	 * @return this
	 */
	public EmailFilter receivedDate(final Operator operator, final long milliseconds) {
		final SearchTerm term = new ReceivedDateTerm(operator.value, new Date(milliseconds));
		concat(term);
		return this;
	}

	/**
	 * Defines filter for sent date.
	 *
	 * @param operator     {@link Operator} to use.
	 * @param milliseconds the milliseconds since January 1, 1970, 00:00:00 GMT.
	 * @return this
	 */
	public EmailFilter sentDate(final Operator operator, final long milliseconds) {
		final SearchTerm term = new SentDateTerm(operator.value, new Date(milliseconds));
		concat(term);
		return this;
	}

	/**
	 * Defines filter on a message body.
	 * All parts of the message that are of MIME type "text/*" are searched.
	 *
	 * @param pattern String pattern use in body.
	 * @return this
	 */
	public EmailFilter text(final String pattern) {
		final SearchTerm term = new BodyTerm(pattern);
		concat(term);
		return this;
	}

	/**
	 * Defines filter for {@link Header}.
	 *
	 * @param headerName The name of the {@link Header}.
	 * @param pattern    String pattern to use for headerName.
	 * @return this
	 */
	public EmailFilter header(final String headerName, final String pattern) {
		final SearchTerm term = new HeaderTerm(headerName, pattern);
		concat(term);
		return this;
	}

	/**
	 * Defines filter for message size.
	 *
	 * @param comparison {@link Operator}.
	 * @param size       size of message.
	 * @return this
	 */
	public EmailFilter size(final Operator comparison, final int size) {
		final SearchTerm term = new SizeTerm(comparison.value, size);
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

		Operator(final int value) {
			this.value = value;
		}
	}

	// ---------------------------------------------------------------- boolean

	/**
	 * Changes concatenation mode to AND.
	 *
	 * @return this
	 */
	public EmailFilter and() {
		this.operatorAnd = true;
		return this;
	}

	/**
	 * Changes concatenation mode to OR.
	 *
	 * @return this
	 */
	public EmailFilter or() {
		this.operatorAnd = false;
		return this;
	}

	/**
	 * Marks next condition to be NOT.
	 *
	 * @return this
	 */
	public EmailFilter not() {
		this.nextIsNot = true;
		return this;
	}

	/**
	 * Defines AND group of filters.
	 *
	 * @param emailFilters array of {@link EmailFilter}s to AND.
	 * @return this
	 */
	public EmailFilter and(final EmailFilter... emailFilters) {
		final SearchTerm[] searchTerms = new SearchTerm[emailFilters.length];

		for (int i = 0; i < emailFilters.length; i++) {
			searchTerms[i] = emailFilters[i].searchTerm;
		}

		concat(new AndTerm(searchTerms));
		return this;
	}

	/**
	 * Defines OR group of filters.
	 *
	 * @param emailFilters array of {@link EmailFilter}s to OR.
	 * @return this
	 */
	public EmailFilter or(final EmailFilter... emailFilters) {
		final SearchTerm[] searchTerms = new SearchTerm[emailFilters.length];

		for (int i = 0; i < emailFilters.length; i++) {
			searchTerms[i] = emailFilters[i].searchTerm;
		}

		concat(new OrTerm(searchTerms));
		return this;
	}

	/**
	 * Appends single filter as NOT.
	 *
	 * @param emailFilter {@link EmailFilter} to append.
	 * @return this
	 */
	public EmailFilter not(final EmailFilter emailFilter) {
		final SearchTerm searchTerm = new NotTerm(emailFilter.searchTerm);
		concat(searchTerm);
		return this;
	}

	// ---------------------------------------------------------------- concat

	/**
	 * Concatenates last search term with new one.
	 *
	 * @param searchTerm searchTerm {@link SearchTerm} concatenate.
	 * @see #and(SearchTerm)
	 * @see #or(SearchTerm)
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

	/**
	 * Sets {@link AndTerm} as searchTerm.
	 *
	 * @param searchTerm {@link SearchTerm} to set as AND.
	 */
	protected void and(final SearchTerm searchTerm) {
		if (this.searchTerm == null) {
			this.searchTerm = searchTerm;
			return;
		}

		this.searchTerm = new AndTerm(this.searchTerm, searchTerm);
	}

	/**
	 * Sets {@link OrTerm} searchTerm.
	 *
	 * @param searchTerm {@link SearchTerm} to set as OR.
	 */
	protected void or(final SearchTerm searchTerm) {
		if (this.searchTerm == null) {
			this.searchTerm = searchTerm;
			return;
		}

		this.searchTerm = new OrTerm(this.searchTerm, searchTerm);
	}

	// ---------------------------------------------------------------- term

	/**
	 * Returns search term.
	 *
	 * @return {@link SearchTerm}.
	 */
	public SearchTerm getSearchTerm() {
		return searchTerm;
	}

}