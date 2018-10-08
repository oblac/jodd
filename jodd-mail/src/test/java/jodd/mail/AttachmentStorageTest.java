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
import jodd.io.FileUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AttachmentStorageTest {

	private static final String GREEN_MAIL_COM = "green@mail.com";
	private static final String GREEN = "green";
	private static final String PWD = "pwd";
	private static final String LOCALHOST = "localhost";

	GreenMail greenMail;

	@BeforeEach
	void startGreenMailInstance() {
		greenMail = new GreenMail(ServerSetupTest.ALL);;
		greenMail.setUser(GREEN_MAIL_COM, GREEN, PWD);
		greenMail.start();
	}

	@AfterEach
	void stopGreenMailInstance() {
		if (greenMail != null) {
			greenMail.stop();
		}
	}

	@Test
	@EnabledOnOs(value = {OS.AIX, OS.LINUX, OS.MAC, OS.SOLARIS})
	void testAttachmentStorage() throws Exception {
		// storing files with its message-id as file name fails on windows because value of the message-id consists of brackets
		//		file names with brackets are not valid on window hosts
		// see https://tools.ietf.org/html/rfc5322#section-3.6.4

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
					.textMessage("Hello Hello " + count)
					.attachment(EmailAttachment
						.with()
						.name("a.jpg")
						.setContentIdFromNameIfMissing()
						.content(new byte[]{'X', 'Z', 'X'}));

				session.sendMail(sentEmail);

				assertEquals(3, sentEmail.attachments().get(0).toByteArray().length);
			}

			session.close();
		}

		ReceivedEmail[] receivedEmails;

		// read attachments
		{
			final ImapServer imapServer = MailServer.create()
				.host(LOCALHOST)
				.port(3143)
				.auth(GREEN, PWD)
				.buildImapMailServer();

			final ReceiveMailSession session = imapServer.createSession();

			session.open();

			receivedEmails = session.receiveEmail();

			session.close();
		}

		for (ReceivedEmail receivedEmail : receivedEmails) {
			List<EmailAttachment<?>> attachmentList = receivedEmail.attachments();
			assertEquals(1, attachmentList.size());
			EmailAttachment att = attachmentList.get(0);

			assertEquals(3, att.toByteArray().length);
		}


		final File attFolder = FileUtil.createTempDirectory("jodd", "tt");

		// read and store attachments
		{
			final ImapServer imapServer = MailServer.create()
				.host(LOCALHOST)
				.port(3143)
				.auth(GREEN, PWD)
				.storeAttachmentsIn(attFolder)
				.buildImapMailServer();

			final ReceiveMailSession session = imapServer.createSession();

			session.open();

			receivedEmails = session.receiveEmail();

			session.close();
		}

		assertEquals(100, receivedEmails.length);
		File[] allFiles = attFolder.listFiles();
		for (File f : allFiles) {
			byte[] bytes = FileUtil.readBytes(f);
			assertArrayEquals(new byte[] {'X', 'Z', 'X'}, bytes);
		}
		assertEquals(100, allFiles.length);

		for (ReceivedEmail receivedEmail : receivedEmails) {
			List<EmailAttachment<?>> attachmentList = receivedEmail.attachments();
			assertEquals(1, attachmentList.size());
			EmailAttachment att = attachmentList.get(0);

			byte[] bytes = att.toByteArray();
			assertArrayEquals(new byte[] {'X', 'Z', 'X'}, bytes);
		}

		FileUtil.deleteDir(attFolder);
	}

}
