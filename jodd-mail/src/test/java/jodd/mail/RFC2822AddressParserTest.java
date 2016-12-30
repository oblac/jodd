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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RFC2822AddressParserTest {

	@Test
	public void testEmailAddress() {
		RFC2822AddressParser RFC2822AddressParser = new RFC2822AddressParser("igor@jodd.org");

		assertEquals(null, RFC2822AddressParser.getPersonalName());
		assertEquals("igor", RFC2822AddressParser.getLocalPart());
		assertEquals("jodd.org", RFC2822AddressParser.getDomain());
		assertTrue(RFC2822AddressParser.isValid());

		RFC2822AddressParser = new RFC2822AddressParser("Vladimir <djs@gmail.com>");

		assertEquals("Vladimir", RFC2822AddressParser.getPersonalName());
		assertEquals("djs", RFC2822AddressParser.getLocalPart());
		assertEquals("gmail.com", RFC2822AddressParser.getDomain());

		assertTrue(RFC2822AddressParser.isValid());
	}

	@Test
	public void testValidEmails() {
		assertTrue(new RFC2822AddressParser("bob @example.com").isValid());
		assertTrue(new RFC2822AddressParser("\"bob\"  @  example.com").isValid());
		assertTrue(new RFC2822AddressParser("\"bob\" (hi) @  example.com").isValid());
		assertTrue(new RFC2822AddressParser("name.surname@example.com").isValid());

		assertTrue(new RFC2822AddressParser("devnull@onyxbits.de").isValid());
		assertTrue(new RFC2822AddressParser("< devnull @ onyxbits.de >").isValid());
		assertTrue(new RFC2822AddressParser("<devnull@onyxbits.de>").isValid());
		assertFalse(new RFC2822AddressParser("Patrick devnull@onyxbits.de").isValid());
		assertTrue(new RFC2822AddressParser("Patrick <devnull@onyxbits.de>").isValid());
		assertTrue(new RFC2822AddressParser("Patrickdevnull@onyxbits.de").isValid());
		assertFalse(new RFC2822AddressParser("\"Patrick Ahlbrecht\" devnull@onyxbits.de").isValid());
		assertTrue(new RFC2822AddressParser("\"Patrick Ahlbrecht\" <devnull@onyxbits.de>").isValid());
		assertTrue(new RFC2822AddressParser("Patrick Ahlbrecht <devnull@onyxbits.de>").isValid());

		assertFalse(new RFC2822AddressParser("Kayaks.org <kayaks@kayaks.org>").isValid());
		assertTrue(new RFC2822AddressParser("\"Kayaks.org\" <kayaks@kayaks.org>").isValid());

		assertFalse(new RFC2822AddressParser("[Kayaks] <kayaks@kayaks.org>").isValid());
		assertTrue(new RFC2822AddressParser("\"[Kayaks]\" <kayaks@kayaks.org>").isValid());
	}

	@Test
	public void testReturnPath() {
		assertTrue(new RFC2822AddressParser("\"[Kayaks]\" <kayaks@kayaks.org>").isValid());
		assertFalse(new RFC2822AddressParser("\"[Kayaks]\" <kayaks@kayaks.org>").isValidReturnPath());

		assertTrue(new RFC2822AddressParser("<kayaks@kayaks.org>").isValid());
		assertTrue(new RFC2822AddressParser("<kayaks@kayaks.org>").isValidReturnPath());
	}

	@Test
	public void testCommentAsName() {
		RFC2822AddressParser RFC2822AddressParser = new RFC2822AddressParser("<bob@example.com> (Bob Smith)");
		assertEquals("Bob Smith", RFC2822AddressParser.getPersonalName());

		RFC2822AddressParser = new RFC2822AddressParser("\"bob smith\" <bob@example.com> (Bobby)");
		assertEquals("bob smith", RFC2822AddressParser.getPersonalName());

		RFC2822AddressParser = new RFC2822AddressParser("<bob@example.com> (Bobby)");
		assertEquals("Bobby", RFC2822AddressParser.getPersonalName());

		RFC2822AddressParser = new RFC2822AddressParser("bob@example.com (Bobby)");
		assertEquals("Bobby", RFC2822AddressParser.getPersonalName());

		RFC2822AddressParser = new RFC2822AddressParser("bob@example.com (Bob) (Smith)");
		assertEquals("Bob", RFC2822AddressParser.getPersonalName());
	}

	@Test
	public void testValidEmails2() {
		assertEmail("me@example.com", true);
		assertEmail("a.nonymous@example.com", true);
		assertEmail("name+tag@example.com", true);
		//assertEmail("!#$%&'+-/=.?^`{|}~@[1.0.0.127]", true);
		//assertEmail("!#$%&'+-/=.?^`{|}~@[IPv6:0123:4567:89AB:CDEF:0123:4567:89AB:CDEF]", true);
		assertEmail("me(this is a comment)@example.com", true); // comments are discouraged but not prohibited by RFC2822.
		//assertEmail("me.example@com", true);
		assertEmail("309d4696df38ff12c023600e3bc2bd4b@fakedomain.com", true);
		assertEmail("ewiuhdghiufduhdvjhbajbkerwukhgjhvxbhvbsejskuadukfhgskjebf@gmail.net", true);

		assertEmail("NotAnEmail", false);
		assertEmail("me@", false);
		assertEmail("@example.com", false);
		assertEmail(".me@example.com", false);
		assertEmail("me@example..com", false);
		assertEmail("me\\@example.com", false);
	}

	private static void assertEmail(String emailaddress, boolean expected) {
		final boolean isValid = new RFC2822AddressParser(emailaddress).isValid();
		if (isValid != expected) {
			throw new IllegalArgumentException(String.format("%s (expected: %s, but was: %s)", emailaddress, expected, isValid));
		}
	}
}