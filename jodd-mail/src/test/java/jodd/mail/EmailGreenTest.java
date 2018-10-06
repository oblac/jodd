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
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.activation.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmailGreenTest {

	private static final String JODD_USE_ME = "Jodd <jodd@use.me>";
	private static final String GREEN_MAIL_COM = "green@mail.com";
	private static final String ZERO = "zero";
	private static final String ONE = "one";
	private static final String TWO = "two";
	private static final String THREE = "three";
	private static final String LOCALHOST = "localhost";
	private static final String GREEN = "green";
	private static final String PWD = "pwd";
	private static final String CID_1 = "CID1";
	private static final byte[] BYTES_0_1_0 = {0, 1, 0};
	private static final byte[] BYTES_1_2_3 = {1, 2, 3};
	private static final byte[] BYTES_4_5_6 = {4, 5, 6};
	private static final byte[] BYTES_7_8_9 = {7, 8, 9};
	private static final byte[] BYTES_10_11_12 = {10, 11, 12};
	private static final String NO_NAME_STREAM = "<no-name>.octet-stream";

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
	void testInlineAttachmentAfterSending() {
		// create Email
		final Email sentEmail = Email.create()
			.from("Jodd", "jodd@use.me")
			.to(GREEN_MAIL_COM)
			.textMessage("Hello")
			.htmlMessage("Hi!")

			.attachment(EmailAttachment.with()
				.name(ZERO)
				.content(BYTES_7_8_9)
				.inline(false))

			.attachment(EmailAttachment.with()
				.name(ONE)
				.content(BYTES_4_5_6)
				.inline(false))

			.attachment(EmailAttachment.with()
				.name(TWO)
				.content(BYTES_1_2_3)
				.contentId(CID_1))

			.embeddedAttachment(EmailAttachment.with()
				.content(BYTES_0_1_0)
				.inline(true))

			// https://github.com/oblac/jodd/issues/546
			.embeddedAttachment(EmailAttachment.with()
				.content(BYTES_10_11_12)
				.name(THREE)
				.contentId(CID_1));
		// send
		{
			final SmtpServer smtpServer = MailServer.create()
					.host(LOCALHOST)
					.port(3025)
					.buildSmtpMailServer();

			final SendMailSession session = smtpServer.createSession();
			session.open();

			session.sendMail(sentEmail);

			session.close();
		}

		// receive
		final ReceivedEmail[] receivedEmails;

		{
			final Pop3Server popServer = MailServer.create()
				.host(LOCALHOST)
				.port(3110)
				.auth(GREEN, PWD)
				.buildPop3MailServer();
			final ReceiveMailSession session = popServer.createSession();
			session.open();
			receivedEmails = session.receiveEmail();
			session.close();
		}

		assertEquals(1, receivedEmails.length);
		final ReceivedEmail receivedEmail = receivedEmails[0];

		checkFrom(sentEmail);
		checkFrom(receivedEmail);

		checkTo(sentEmail);
		checkTo(receivedEmail);

		checkAttachments(sentEmail.attachments(), receivedEmail.attachments());
	}

	private void checkFrom(final CommonEmail email) {
		assertEquals(JODD_USE_ME, email.from().toString());
	}

	private void checkTo(final CommonEmail email) {
		assertEquals(GREEN_MAIL_COM, email.to()[0].toString());
	}

	private void checkAttachments(final List<EmailAttachment<? extends DataSource>> sentAttachments, final List<EmailAttachment<? extends DataSource>> receivedAttachments) {
		checkSize(sentAttachments);
		checkSize(receivedAttachments);

		String name = ZERO;
		byte[] data = BYTES_7_8_9;
		checkAttachmentInfo(sentAttachments, 0, receivedAttachments, 2, name, data, false, false);

		name = ONE;
		data = BYTES_4_5_6;
		checkAttachmentInfo(sentAttachments, 1, receivedAttachments, 3, name, data, false, false);

		name = TWO;
		data = BYTES_1_2_3;
		checkAttachmentInfo(sentAttachments, 2, receivedAttachments, 4, name, data, false, false);
		//assertEquals(CID_1, sentAttachments.get(ndx).getContentId());
		//assertEquals("<CID1>", receivedAttachments.get(ndx).getContentId());

		// These are null because used storeAttachment method instead of embedAttachment method.
		assertNull(sentAttachments.get(2).getContentId());
		assertNull(receivedAttachments.get(4).getContentId());

		/**/
		name = null;
		data = BYTES_0_1_0;
		checkAttachmentInfo(sentAttachments, 3, receivedAttachments, 0, name, data, true, true);

		name = THREE;
		data = BYTES_10_11_12;
		checkAttachmentInfo(sentAttachments, 4, receivedAttachments, 1, name, data, true, true);
	}

	private void checkSize(final List<EmailAttachment<? extends DataSource>> attachments) {
		assertEquals(5, attachments.size());
	}

	private void checkAttachmentInfo(final List<EmailAttachment<? extends DataSource>> sentAttachments, final int sentIndex, final List<EmailAttachment<? extends DataSource>> receivedAttachments, final int receivedIndex, final String name, final byte[] data, final boolean isEmbedded, final boolean isInline) {
		final EmailAttachment<? extends DataSource> sentAttachment = sentAttachments.get(sentIndex);
		final EmailAttachment<? extends DataSource> receivedAttachment = receivedAttachments.get(receivedIndex);

		checkName(name, sentAttachment, receivedAttachment);
		checkData(data, sentAttachment, receivedAttachment);
		checkEmbedded(isEmbedded, sentAttachment, receivedAttachment);
		checkInline(isInline, sentAttachment, receivedAttachment);
	}

	private void checkName(final String name, final EmailAttachment<? extends DataSource> sentAttachment, final EmailAttachment<? extends DataSource> receivedAttachment) {
		checkName(name, sentAttachment);
		checkName(name, receivedAttachment);
	}

	private void checkName(String name, final EmailAttachment<? extends DataSource> attachment) {
		final String attachmentName = attachment.getName();
		if (name == null && attachmentName != null && attachmentName.equals(NO_NAME_STREAM)) {
			name = NO_NAME_STREAM;
		}
		assertEquals(name, attachmentName);
	}

	private void checkData(final byte[] data, final EmailAttachment<? extends DataSource> sentAttachment, final EmailAttachment<? extends DataSource> receivedAttachment) {
		checkData(data, sentAttachment);
		checkData(data, receivedAttachment);
	}

	private void checkData(final byte[] data, final EmailAttachment<? extends DataSource> attachment) {
		assertArrayEquals(data, attachment.toByteArray());
	}

	private void checkEmbedded(final boolean isEmbedded, final EmailAttachment<? extends DataSource> sentAttachment, final EmailAttachment<? extends DataSource> receivedAttachment) {
		checkEmbedded(isEmbedded, sentAttachment);
		checkEmbedded(isEmbedded, receivedAttachment);
	}

	private void checkEmbedded(final boolean isEmbedded, final EmailAttachment<? extends DataSource> attachment) {
		assertEquals(isEmbedded, attachment.isEmbedded());
	}

	private void checkInline(final boolean isInline, final EmailAttachment<? extends DataSource> sentAttachment, final EmailAttachment<? extends DataSource> receivedAttachment) {
		checkInline(isInline, sentAttachment);
		checkInline(isInline, receivedAttachment);
	}

	private void checkInline(final boolean isInline, final EmailAttachment<? extends DataSource> attachment) {
		assertEquals(isInline, attachment.isInline());
	}
}
