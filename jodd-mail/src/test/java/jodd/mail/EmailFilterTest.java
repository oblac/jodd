// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import org.junit.Test;

import javax.mail.Message;
import javax.mail.search.AndTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.NotTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import static jodd.mail.EmailFilter.filter;
import static org.junit.Assert.assertEquals;

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


}