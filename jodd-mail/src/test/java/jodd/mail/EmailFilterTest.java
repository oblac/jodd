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

public class EmailFilterTest {

	@Test
	public void testAnd1() {
		EmailFilter emailFilter =
			filter()
				.from("from");

		SearchTerm expected = new FromStringTerm("from");
		assertEquals(expected, emailFilter.searchTerm);
	}

	@Test
	public void testAnd2() {
		EmailFilter emailFilter =
			filter()
				.from("from")
				.to("to");

		SearchTerm expected =
				new AndTerm(
					new FromStringTerm("from"),
					new RecipientStringTerm(Message.RecipientType.TO, "to")
				);
		assertEquals(expected, emailFilter.searchTerm);
	}

	@Test
	public void testOr2() {
		EmailFilter emailFilter =
			filter().or(
					filter().from("from"),
					filter().to("to")

			);

		SearchTerm expected =
				new OrTerm(
					new FromStringTerm("from"),
					new RecipientStringTerm(Message.RecipientType.TO, "to")
				);

		assertEquals(expected, emailFilter.searchTerm);
	}

	@Test
	public void testOr2Alt() {
		EmailFilter emailFilter =
			filter().or()
					.from("from")
					.to("to");

		SearchTerm expected =
				new OrTerm(
					new FromStringTerm("from"),
					new RecipientStringTerm(Message.RecipientType.TO, "to")
				);

		assertEquals(expected, emailFilter.searchTerm);
	}

	@Test
	public void testAndOrNot() {
		EmailFilter emailFilter =
					filter()
						.from("from")
						.to("to")
						.or()
						.not()
						.subject("subject")
						.from("from2");

		SearchTerm expected =
				new OrTerm(
					new OrTerm(
						new AndTerm(
								new FromStringTerm("from"),
								new RecipientStringTerm(Message.RecipientType.TO, "to")
						),
						new NotTerm(
								new SubjectTerm("subject")
						)
					),
					new FromStringTerm("from2")
				);

		assertEquals(expected, emailFilter.searchTerm);
	}

	@Test
	public void testAndOrNotAlt() {
		EmailFilter emailFilter =
					filter()
						.or(
							filter().and(
								filter().from("from"),
								filter().to("to")
							),
							filter().not(filter().subject("subject")),
							filter().from("from2")
						);

		SearchTerm expected =
				new OrTerm(
					new SearchTerm[] {
						new AndTerm(
								new FromStringTerm("from"),
								new RecipientStringTerm(Message.RecipientType.TO, "to")
						),
						new NotTerm(
								new SubjectTerm("subject")
						),
						new FromStringTerm("from2")
					}
				);


		assertEquals(expected, emailFilter.searchTerm);
	}

	@Test
	public void testReceivedDate() {
		EmailFilter emailFilter = EmailFilter.filter()
			.receivedDate(EmailFilter.Operator.EQ, 1000)
			.sentDate(EmailFilter.Operator.GT, 2000);

		SearchTerm expected =
			new AndTerm(
				new ReceivedDateTerm(3, new Date(1000)),
				new SentDateTerm(5, new Date(2000))
			);

		assertEquals(expected, emailFilter.searchTerm);
	}

}
