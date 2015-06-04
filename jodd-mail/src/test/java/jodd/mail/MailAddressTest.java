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