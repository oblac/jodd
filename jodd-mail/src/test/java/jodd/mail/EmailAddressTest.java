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

import javax.mail.internet.AddressException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmailAddressTest {

	private static final String ADMIN_JODD_COM = "admin@jodd.com";
	private static final String JENNY_DOE = "Jenny Doe";
	private static final String JENNY_DOE_SPACE = JENNY_DOE + " ";
	private static final String JENNY_DOE_ADMIN_JODD_COM = "Jenny Doe <admin@jodd.com>";

	@Test
	void testMailFromString() {
		EmailAddress mailAddress = EmailAddress.of(ADMIN_JODD_COM);
		assertNull(mailAddress.getPersonalName());
		assertEquals(ADMIN_JODD_COM, mailAddress.getEmail());
		assertEquals(ADMIN_JODD_COM, mailAddress.toString());

		mailAddress = EmailAddress.of(JENNY_DOE_ADMIN_JODD_COM);
		assertEquals(JENNY_DOE, mailAddress.getPersonalName());
		assertEquals(ADMIN_JODD_COM, mailAddress.getEmail());
		assertEquals(JENNY_DOE_ADMIN_JODD_COM, mailAddress.toString());

		mailAddress = EmailAddress.of(JENNY_DOE_SPACE, ADMIN_JODD_COM);
		assertEquals(JENNY_DOE_SPACE, mailAddress.getPersonalName());
		assertEquals(ADMIN_JODD_COM, mailAddress.getEmail());
		assertEquals("Jenny Doe  <admin@jodd.com>", mailAddress.toString());
	}

	@Test
	void testMailFromEmailAddress() {
		EmailAddress mailAddress = new RFC2822AddressParser().parseToEmailAddress(ADMIN_JODD_COM);
		assertNull(mailAddress.getPersonalName());
		assertEquals(ADMIN_JODD_COM, mailAddress.getEmail());

		mailAddress = new RFC2822AddressParser().parseToEmailAddress(JENNY_DOE_ADMIN_JODD_COM);
		assertEquals(JENNY_DOE, mailAddress.getPersonalName());
		assertEquals(ADMIN_JODD_COM, mailAddress.getEmail());

		final EmailAddress mailAddress2 = new RFC2822AddressParser().parseToEmailAddress(mailAddress.toString());
		assertEquals(JENNY_DOE, mailAddress2.getPersonalName());
		assertEquals(ADMIN_JODD_COM, mailAddress2.getEmail());
	}

	@Test
	void testMailFromInternetAddress() throws AddressException {
		final EmailAddress mailAddress = new RFC2822AddressParser().parseToEmailAddress(JENNY_DOE_ADMIN_JODD_COM);
		final EmailAddress mailAddress2 = EmailAddress.of(mailAddress.toInternetAddress());

		assertEquals(JENNY_DOE, mailAddress2.getPersonalName());
		assertEquals(ADMIN_JODD_COM, mailAddress2.getEmail());
	}

	@Test
	void testIssue211() {
		final String testAddress = "Some One<someone@yahoo.com>";
		final EmailAddress addr = EmailAddress.of(testAddress);

		assertEquals("Some One <someone@yahoo.com>", addr.toString());
	}
}
