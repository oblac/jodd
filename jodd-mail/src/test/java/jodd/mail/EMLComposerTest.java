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

import jdk.nashorn.internal.runtime.events.RecompilationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EMLComposerTest {

	private static final String HELLO = "Hello";

	@Test
	void testWriteSimpleEmail() {
		final Email email = Email.create().from("Joe@example.com").to("Pig@example.com").textMessage(HELLO);

		final String eml = EMLComposer.create().compose(email);

		assertTrue(eml.contains("From: Joe@example.com\r\n"));
		assertTrue(eml.contains("To: Pig@example.com\r\n"));
		assertTrue(eml.contains(HELLO));
	}

	@Test
	void testWriteSimpleReceivedEmail() throws FileNotFoundException, MessagingException {
		final URL data = EMLComposerTest.class.getResource("test");
		final File emlFile = new File(data.getFile(), "simple.eml");

		ReceivedEmail email = EMLParser.create().parse(emlFile);

		final String eml = EMLComposer.create().compose(email);

		assertTrue(eml.contains("From: sender@emailhost.com\r\n"));
		assertTrue(eml.contains("To: recipient@emailhost.com\r\n"));
	}
}
