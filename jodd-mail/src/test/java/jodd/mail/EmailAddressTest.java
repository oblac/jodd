// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EmailAddressTest {

	@Test
	public void testEmailAddress() {
		EmailAddress emailAddress = new EmailAddress("igor@jodd.org");

		assertEquals(null, emailAddress.getPersonalName());
		assertEquals("igor", emailAddress.getLocalPart());
		assertEquals("jodd.org", emailAddress.getDomain());
		assertTrue(emailAddress.isValid());

		emailAddress = new EmailAddress("Vladimir <djs@gmail.com>");

		assertEquals("Vladimir", emailAddress.getPersonalName());
		assertEquals("djs", emailAddress.getLocalPart());
		assertEquals("gmail.com", emailAddress.getDomain());

		assertTrue(emailAddress.isValid());
	}

	@Test
	public void testValidEmails() {
		assertTrue(new EmailAddress("bob @example.com").isValid());
		assertTrue(new EmailAddress("\"bob\"  @  example.com").isValid());
		assertTrue(new EmailAddress("\"bob\" (hi) @  example.com").isValid());
		assertTrue(new EmailAddress("name.surname@example.com").isValid());

		assertTrue(new EmailAddress("devnull@onyxbits.de").isValid());
		assertTrue(new EmailAddress("< devnull @ onyxbits.de >").isValid());
		assertTrue(new EmailAddress("<devnull@onyxbits.de>").isValid());
		assertFalse(new EmailAddress("Patrick devnull@onyxbits.de").isValid());
		assertTrue(new EmailAddress("Patrick <devnull@onyxbits.de>").isValid());
		assertTrue(new EmailAddress("Patrickdevnull@onyxbits.de").isValid());
		assertFalse(new EmailAddress("\"Patrick Ahlbrecht\" devnull@onyxbits.de").isValid());
		assertTrue(new EmailAddress("\"Patrick Ahlbrecht\" <devnull@onyxbits.de>").isValid());
		assertTrue(new EmailAddress("Patrick Ahlbrecht <devnull@onyxbits.de>").isValid());

		assertFalse(new EmailAddress("Kayaks.org <kayaks@kayaks.org>").isValid());
		assertTrue(new EmailAddress("\"Kayaks.org\" <kayaks@kayaks.org>").isValid());

		assertFalse(new EmailAddress("[Kayaks] <kayaks@kayaks.org>").isValid());
		assertTrue(new EmailAddress("\"[Kayaks]\" <kayaks@kayaks.org>").isValid());
	}

	@Test
	public void testReturnPath() {
		assertTrue(new EmailAddress("\"[Kayaks]\" <kayaks@kayaks.org>").isValid());
		assertFalse(new EmailAddress("\"[Kayaks]\" <kayaks@kayaks.org>").isValidReturnPath());

		assertTrue(new EmailAddress("<kayaks@kayaks.org>").isValid());
		assertTrue(new EmailAddress("<kayaks@kayaks.org>").isValidReturnPath());
	}

	@Test
	public void testCommentAsName() {
		EmailAddress emailAddress = new EmailAddress("<bob@example.com> (Bob Smith)");
		assertEquals("Bob Smith", emailAddress.getPersonalName());

		emailAddress = new EmailAddress("\"bob smith\" <bob@example.com> (Bobby)");
		assertEquals("bob smith", emailAddress.getPersonalName());

		emailAddress = new EmailAddress("<bob@example.com> (Bobby)");
		assertEquals("Bobby", emailAddress.getPersonalName());

		emailAddress = new EmailAddress("bob@example.com (Bobby)");
		assertEquals("Bobby", emailAddress.getPersonalName());

		emailAddress = new EmailAddress("bob@example.com (Bob) (Smith)");
		assertEquals("Bob", emailAddress.getPersonalName());
	}
}