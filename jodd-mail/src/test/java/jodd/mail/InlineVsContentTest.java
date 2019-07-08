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

import jodd.io.FileNameUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

@Disabled("Real email sending required")
class InlineVsContentTest {

	public static final String PNG = FileNameUtil.resolveHome("~/prj/oblac/jodd-site/content/jodd.png");
	private static final String HOST = "mail.joddframework.org";
	private static final String USERNAME = "t";
	private static final String PASSWORD = "t";

	@Test
	void testSendEmailWithVariousAttachaments() {
		final SmtpServer smtpServer =
			MailServer.create()
				.host(HOST)
				.auth(USERNAME, PASSWORD)
				.ssl(true)
				.buildSmtpMailServer();

		final SendMailSession session = smtpServer.createSession();
		session.open();

		final Email email = Email.create()
			.from("info@jodd.org")
			.to("igor.spasic@gmail.com")
			.subject("test-gmail")
			.textMessage("Hello!")
			.htmlMessage(
				"<html><META http-equiv=Content-Type content=\"text/html; charset=utf-8\">" +
					"<body><h1>Hey!</h1><img src='cid:jodd.png'>" +
					"<h2>Hay!</h2><img src='cid:jodd2.png'>" +
					"<h3></h3></body></html>")
			.embeddedAttachment(EmailAttachment.with().content(new File(PNG)).inline(false))
			.embeddedAttachment(EmailAttachment.with().content(new File(PNG)).contentId("jodd2.png").inline(true))
			.attachment(EmailAttachment.with().content(PNG));

		session.sendMail(email);
		session.close();
	}
}
