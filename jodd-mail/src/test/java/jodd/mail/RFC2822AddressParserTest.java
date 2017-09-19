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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RFC2822AddressParserTest {

	@Test
	public void testEmailAddress() {
		RFC2822AddressParser.ParsedAddress address = new RFC2822AddressParser().parse("igor@jodd.org");

		assertEquals(null, address.getPersonalName());
		assertEquals("igor", address.getLocalPart());
		assertEquals("jodd.org", address.getDomain());
		assertTrue(address.isValid());

		address = new RFC2822AddressParser().parse("Vladimir <djs@gmail.com>");

		assertEquals("Vladimir", address.getPersonalName());
		assertEquals("djs", address.getLocalPart());
		assertEquals("gmail.com", address.getDomain());

		assertTrue(address.isValid());
	}

	@Test
	public void testValidEmails() {
		assertTrue(new RFC2822AddressParser().parse("bob @example.com").isValid());
		assertTrue(new RFC2822AddressParser().parse("\"bob\"  @  example.com").isValid());
		assertTrue(new RFC2822AddressParser().parse("\"bob\" (hi) @  example.com").isValid());
		assertTrue(new RFC2822AddressParser().parse("name.surname@example.com").isValid());

		assertTrue(new RFC2822AddressParser().parse("devnull@onyxbits.de").isValid());
		assertTrue(new RFC2822AddressParser().parse("< devnull @ onyxbits.de >").isValid());
		assertTrue(new RFC2822AddressParser().parse("<devnull@onyxbits.de>").isValid());
		assertFalse(new RFC2822AddressParser().parse("Patrick devnull@onyxbits.de").isValid());
		assertTrue(new RFC2822AddressParser().parse("Patrick <devnull@onyxbits.de>").isValid());
		assertTrue(new RFC2822AddressParser().parse("Patrickdevnull@onyxbits.de").isValid());
		assertFalse(new RFC2822AddressParser().parse("\"Patrick Ahlbrecht\" devnull@onyxbits.de").isValid());
		assertTrue(new RFC2822AddressParser().parse("\"Patrick Ahlbrecht\" <devnull@onyxbits.de>").isValid());
		assertTrue(new RFC2822AddressParser().parse("Patrick Ahlbrecht <devnull@onyxbits.de>").isValid());

		assertFalse(new RFC2822AddressParser().parse("Kayaks.org <kayaks@kayaks.org>").isValid());
		assertTrue(new RFC2822AddressParser().parse("\"Kayaks.org\" <kayaks@kayaks.org>").isValid());

		assertFalse(new RFC2822AddressParser().parse("[Kayaks] <kayaks@kayaks.org>").isValid());
		assertTrue(new RFC2822AddressParser().parse("\"[Kayaks]\" <kayaks@kayaks.org>").isValid());
	}

	@Test
	public void testReturnPath() {
		assertTrue(new RFC2822AddressParser().parse("\"[Kayaks]\" <kayaks@kayaks.org>").isValid());
		assertFalse(new RFC2822AddressParser().parse("\"[Kayaks]\" <kayaks@kayaks.org>").isValidReturnPath());

		assertTrue(new RFC2822AddressParser().parse("<kayaks@kayaks.org>").isValid());
		assertTrue(new RFC2822AddressParser().parse("<kayaks@kayaks.org>").isValidReturnPath());
	}

	@Test
	public void testCommentAsName() {
		RFC2822AddressParser.ParsedAddress address = new RFC2822AddressParser().parse("<bob@example.com> (Bob Smith)");
		assertEquals("Bob Smith", address.getPersonalName());

		address = new RFC2822AddressParser().parse("\"bob smith\" <bob@example.com> (Bobby)");
		assertEquals("bob smith", address.getPersonalName());

		address = new RFC2822AddressParser().parse("<bob@example.com> (Bobby)");
		assertEquals("Bobby", address.getPersonalName());

		address = new RFC2822AddressParser().parse("bob@example.com (Bobby)");
		assertEquals("Bobby", address.getPersonalName());

		address = new RFC2822AddressParser().parse("bob@example.com (Bob) (Smith)");
		assertEquals("Bob", address.getPersonalName());
	}

	@Test
	public void testValidEmails2() {
		assertTrue(new RFC2822AddressParser().parse("me@example.com").isValid());
		assertTrue(new RFC2822AddressParser().parse("a.nonymous@example.com").isValid());
		assertTrue(new RFC2822AddressParser().parse("name+tag@example.com").isValid());
		assertTrue(RFC2822AddressParser.STRICT.parse("!#$%&'+-/=.?^`{|}~@[1.0.0.127]").isValid());
		assertFalse(RFC2822AddressParser.LOOSE.parse("!#$%&'+-/=.?^`{|}~@[IPv6:0123:4567:89AB:CDEF:0123:4567:89AB:CDEF]").isValid());
		assertTrue(RFC2822AddressParser.STRICT.parse("!#$%&'+-/=.?^`{|}~@[IPv6:0123:4567:89AB:CDEF:0123:4567:89AB:CDEF]").isValid());
		assertTrue(new RFC2822AddressParser().parse("me(this is a comment)@example.com").isValid());
		assertFalse(RFC2822AddressParser.LOOSE.parse("me.example@com").isValid());
		assertTrue(RFC2822AddressParser.STRICT.parse("me.example@com").isValid());
		assertTrue(new RFC2822AddressParser().parse("309d4696df38ff12c023600e3bc2bd4b@fakedomain.com").isValid());
		assertTrue(new RFC2822AddressParser().parse("ewiuhdghiufduhdvjhbajbkerwukhgjhvxbhvbsejskuadukfhgskjebf@gmail.net").isValid());

		assertFalse(new RFC2822AddressParser().parse("NotAnEmail").isValid());
		assertFalse(new RFC2822AddressParser().parse("me@").isValid());
		assertFalse(new RFC2822AddressParser().parse("@example.com").isValid());
		assertFalse(new RFC2822AddressParser().parse(".me@example.com").isValid());
		assertFalse(new RFC2822AddressParser().parse("me@example..com").isValid());
		assertFalse(new RFC2822AddressParser().parse("me\\@example.com").isValid());
	}

}
