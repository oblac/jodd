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

import jodd.mail.fixture.GreenMailServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.Flags;

import static jodd.mail.EmailFilter.filter;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailGreenFilterTest {

	private GreenMailServer greenMail;

	@BeforeEach
	void startGreenMailInstance() {
		greenMail = new GreenMailServer().start();
	}

	@AfterEach
	void stopGreenMailInstance() {
		greenMail.stop();
	}


	@Test
	void testFilterEmails() {
		final Email email1 = Email.create()
			.from("Jodd", "jodd@use.me")
			.to(GreenMailServer.GREEN_MAIL_COM)
			.subject("Hello")
			.htmlMessage("Hi1!");

		final Email email2 = Email.create()
			.from("Jodd", "jodd@use.me")
			.to(GreenMailServer.GREEN_MAIL_COM)
			.subject("Hi")
			.htmlMessage("Hi2!");


		// send
		{
			final SmtpServer smtpServer = MailServer.create()
				.host(GreenMailServer.HOST)
				.port(GreenMailServer.SMTP_PORT)
				.buildSmtpMailServer();

			final SendMailSession session = smtpServer.createSession();
			session.open();

			session.sendMail(email1);
			session.sendMail(email2);

			session.close();
		}

		final ReceivedEmail[] receivedEmails;

		{
			final Pop3Server popServer = MailServer.create()
				.host(GreenMailServer.HOST)
				.port(GreenMailServer.POP3_PORT)
				.auth(GreenMailServer.USER, GreenMailServer.PASSWORD)
				.buildPop3MailServer();
			final ReceiveMailSession session = popServer.createSession();
			session.open();
			receivedEmails = session.receiveEmail(
				filter()
					.flag(Flags.Flag.SEEN, false)
					.subject("Hello")
			);
			session.close();
		}

		assertEquals(1, receivedEmails.length);
		assertEquals("Hello", receivedEmails[0].subject());
	}
}
