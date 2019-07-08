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

import org.junit.jupiter.api.Test;

import javax.mail.Message;
import javax.mail.search.AndTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.NotTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;
import javax.mail.search.SubjectTerm;
import java.util.Date;

import static jodd.mail.EmailFilter.filter;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailFilterTest {

	private static final String FROM = "from";
	private static final String TO = "to";
	private static final String SUBJECT = "subject";
	private static final String FROM_2 = "from2";

	@Test
	void testAnd1() {
		final EmailFilter emailFilter =
			filter()
				.from(FROM);

		final SearchTerm expected = new FromStringTerm(FROM);
		assertEquals(expected, emailFilter.searchTerm);
	}

	@Test
	void testAnd2() {
		final EmailFilter emailFilter =
			filter()
				.from(FROM)
				.to(TO);

		final SearchTerm expected =
			new AndTerm(
				new FromStringTerm(FROM),
				new RecipientStringTerm(Message.RecipientType.TO, TO)
			);
		assertEquals(expected, emailFilter.searchTerm);
	}

	@Test
	void testOr2() {
		final EmailFilter emailFilter =
			filter().or(
				filter().from(FROM),
				filter().to(TO)

			);

		final SearchTerm expected =
			new OrTerm(
				new FromStringTerm(FROM),
				new RecipientStringTerm(Message.RecipientType.TO, TO)
			);

		assertEquals(expected, emailFilter.searchTerm);
	}

	@Test
	void testOr2Alt() {
		final EmailFilter emailFilter =
			filter().or()
				.from(FROM)
				.to(TO);

		final SearchTerm expected =
			new OrTerm(
				new FromStringTerm(FROM),
				new RecipientStringTerm(Message.RecipientType.TO, TO)
			);

		assertEquals(expected, emailFilter.searchTerm);
	}

	@Test
	void testAndOrNot() {
		final EmailFilter emailFilter =
			filter()
				.from(FROM)
				.to(TO)
				.or()
				.not()
				.subject(SUBJECT)
				.from(FROM_2);

		final SearchTerm expected =
			new OrTerm(
				new OrTerm(
					new AndTerm(
						new FromStringTerm(FROM),
						new RecipientStringTerm(Message.RecipientType.TO, TO)
					),
					new NotTerm(
						new SubjectTerm(SUBJECT)
					)
				),
				new FromStringTerm(FROM_2)
			);

		assertEquals(expected, emailFilter.searchTerm);
	}

	@Test
	void testAndOrNotAlt() {
		final EmailFilter emailFilter =
			filter()
				.or(
					filter().and(
						filter().from(FROM),
						filter().to(TO)
					),
					filter().not(filter().subject(SUBJECT)),
					filter().from(FROM_2)
				);

		final SearchTerm expected =
			new OrTerm(
				new SearchTerm[]{
					new AndTerm(
						new FromStringTerm(FROM),
						new RecipientStringTerm(Message.RecipientType.TO, TO)
					),
					new NotTerm(
						new SubjectTerm(SUBJECT)
					),
					new FromStringTerm(FROM_2)
				}
			);


		assertEquals(expected, emailFilter.searchTerm);
	}

	@Test
	void testReceivedDate() {
		final EmailFilter emailFilter = EmailFilter.filter()
			.receivedDate(EmailFilter.Operator.EQ, 1524575533757L)
			.sentDate(EmailFilter.Operator.GT, 1524575533757L);

		final SearchTerm expected =
			new AndTerm(
				new ReceivedDateTerm(3, new Date(1524575533757L)),
				new SentDateTerm(5, new Date(1524575533757L))
			);

		assertEquals(expected, emailFilter.searchTerm);
	}

}
