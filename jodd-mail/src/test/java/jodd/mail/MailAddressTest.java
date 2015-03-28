// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import org.junit.Test;

import javax.mail.internet.AddressException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MailAddressTest {

	@Test
	public void testMailFromString() {
		MailAddress mailAddress = new MailAddress("admin@jodd.com");
		assertNull(mailAddress.getPersonalName());
		assertEquals("admin@jodd.com", mailAddress.getEmail());
		assertEquals("admin@jodd.com", mailAddress.toString());

		mailAddress = new MailAddress("Jenny Doe <admin@jodd.com>");
		assertEquals("Jenny Doe", mailAddress.getPersonalName());
		assertEquals("admin@jodd.com", mailAddress.getEmail());
		assertEquals("Jenny Doe <admin@jodd.com>", mailAddress.toString());

		mailAddress = new MailAddress("Jenny Doe ", "admin@jodd.com");
		assertEquals("Jenny Doe ", mailAddress.getPersonalName());
		assertEquals("admin@jodd.com", mailAddress.getEmail());
		assertEquals("Jenny Doe  <admin@jodd.com>", mailAddress.toString());
	}

	@Test
	public void testMailFromEmailAddress() {
		MailAddress mailAddress = new MailAddress(new EmailAddress("admin@jodd.com"));
		assertNull(mailAddress.getPersonalName());
		assertEquals("admin@jodd.com", mailAddress.getEmail());

		mailAddress = new MailAddress(new EmailAddress("Jenny Doe <admin@jodd.com>"));
		assertEquals("Jenny Doe", mailAddress.getPersonalName());
		assertEquals("admin@jodd.com", mailAddress.getEmail());

		MailAddress mailAddress2 = new MailAddress(mailAddress.toEmailAddress());
		assertEquals("Jenny Doe", mailAddress2.getPersonalName());
		assertEquals("admin@jodd.com", mailAddress2.getEmail());
	}

	@Test
	public void testMailFromInternetAddress() throws AddressException {
		MailAddress mailAddress = new MailAddress(new EmailAddress("Jenny Doe <admin@jodd.com>"));
		MailAddress mailAddress2 = new MailAddress(mailAddress.toInternetAddress());

		assertEquals("Jenny Doe", mailAddress2.getPersonalName());
		assertEquals("admin@jodd.com", mailAddress2.getEmail());
	}

	@Test
	public void testIssue211() {
		String testAddress = "Some One<someone@yahoo.com>";
		MailAddress addr = new MailAddress(testAddress);

		assertEquals("Some One <someone@yahoo.com>", addr.toString());
	}
}