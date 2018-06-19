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

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilterGreenIMAPTest {

	private static final String GREEN_MAIL_COM = "green@mail.com";
	private static final String GREEN = "green";
	private static final String PWD = "pwd";
	private static final String LOCALHOST = "localhost";

	@Test
	void testEnvelopes_FilterByMessageId() {
		final GreenMail greenMail = new GreenMail(ServerSetupTest.ALL);
		greenMail.setUser(GREEN_MAIL_COM, GREEN, PWD);
		greenMail.start();

		final SmtpServer smtpServer = MailServer.create()
			.host(LOCALHOST)
			.port(3025)
			.buildSmtpMailServer();

		// prepare emails
		{
			final SendMailSession session = smtpServer.createSession();

			session.open();

			int count = 100;
			while (count-- > 0) {
				// create Email
				final Email sentEmail = Email.create()
					.subject("Mail : " + count)
					.from("Jodd", "jodd@use.me")
					.to(GREEN_MAIL_COM)
					.textMessage("Hello " + count)
					.attachment(EmailAttachment.with().content(new byte[]{(byte) count}));

				session.sendMail(sentEmail);
			}

			session.close();
		}

		final ReceivedEmail[] receivedEmails;

		// read envelopes
		{
			final ImapServer imapServer = MailServer.create()
				.host(LOCALHOST)
				.port(3143)
				.auth(GREEN, PWD)
				.buildImapMailServer();

			final ReceiveMailSession session = imapServer.createSession();

			session.open();

			receivedEmails = session.receiveEnvelopes();

			session.close();
		}

		assertEquals(100, receivedEmails.length);

		for (final ReceivedEmail receivedEmail : receivedEmails) {
			assertTrue(receivedEmail.attachedMessages().isEmpty());
		}

		final ReceivedEmail[] receivedEmails2;

		// filter by messageNumber()
		{
			final ImapServer imapServer = MailServer.create()
				.host(LOCALHOST)
				.port(3143)
				.auth(GREEN, PWD)
				.buildImapMailServer();

			final ReceiveMailSession session = imapServer.createSession();

			session.open();

			receivedEmails2 = session.receive().filter(EmailFilter.filter().messageId(receivedEmails[0].messageId())).get();

			session.close();
		}

		assertEquals(1, receivedEmails2.length);

		greenMail.stop();
	}
}
