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

import jodd.util.StringPool;
import jodd.net.MimeTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EMLParserTest {

	protected String testDataRoot;

	@BeforeEach
	void setUp() {
		if (testDataRoot != null) {
			return;
		}
		final URL data = EMLParserTest.class.getResource("test");
		testDataRoot = data.getFile();
	}

	@Test
	void testParseEML() throws FileNotFoundException, MessagingException {
		final File emlFile = new File(testDataRoot, "example.eml");

		ReceivedEmail email = EMLParser.create().parse(emlFile);

		assertEquals("Example <from@example.com>", email.from().toString());
		assertEquals("to@example.com", email.to()[0].toString());
		assertEquals("test!", email.subject());

		// the time is specified in GMT zone
		LocalDateTime jdt = LocalDateTime.of(2010, 3, 27, 12, 11, 21, 0);
		Instant instant = jdt.atZone(ZoneId.of("GMT")).toInstant();

		// compare
		assertEquals(Date.from(instant), email.sentDate());

		Map<String, String> headers = email.headers();
		assertEquals("1.0", headers.get("MIME-Version"));

		List<EmailMessage> messages = email.messages();
		assertEquals(2, messages.size());

		EmailMessage msg1 = messages.get(0);
		assertEquals("Test", msg1.getContent().trim());
		assertEquals(MimeTypes.MIME_TEXT_PLAIN, msg1.getMimeType());
		assertEquals(StringPool.US_ASCII.toLowerCase(), msg1.getEncoding());

		EmailMessage msg2 = messages.get(1);
		assertTrue(msg2.getContent().contains("Test<o:p>"));
		assertEquals(MimeTypes.MIME_TEXT_HTML, msg2.getMimeType());
		assertEquals(StringPool.US_ASCII.toLowerCase(), msg2.getEncoding());

		List<EmailAttachment<? extends DataSource>> attachments = email.attachments();
		assertNotNull(attachments);
		assertTrue(attachments.isEmpty());

		List<ReceivedEmail> attachedMessages = email.attachedMessages();
		assertNotNull(attachedMessages);
		assertEquals(1, attachedMessages.size());

		email = attachedMessages.get(0);

		// attached message

		assertEquals("Example <from@example.com>", email.from().toString());
		assertEquals("to@example.com", email.to()[0].toString());
		assertEquals("test", email.subject());

//		jdt = new JDateTime(2010, 3, 27, 12, 9, 46, 0);
//		jdt.changeTimeZone(TimeZone.getTimeZone("GMT"), TimeZone.getDefault());
//		assertEquals(jdt.convertToDate(), email.sentDate());

		headers = email.headers();
		assertEquals("1.0", headers.get("MIME-Version"));

		messages = email.messages();
		assertEquals(2, messages.size());

		msg1 = messages.get(0);
		assertEquals("test", msg1.getContent().trim());
		assertEquals(MimeTypes.MIME_TEXT_PLAIN, msg1.getMimeType());
		assertEquals(StringPool.US_ASCII.toLowerCase(), msg1.getEncoding());

		msg2 = messages.get(1);
		assertTrue(msg2.getContent().contains("test</TITLE>"));
		assertEquals(MimeTypes.MIME_TEXT_HTML, msg2.getMimeType());
		assertEquals(StringPool.US_ASCII.toLowerCase(), msg2.getEncoding());

		attachments = email.attachments();
		assertNotNull(attachments);
		assertTrue(attachments.isEmpty());

		attachedMessages = email.attachedMessages();
		assertNotNull(attachedMessages);
		assertTrue(attachedMessages.isEmpty());
	}

	@Test
	void testParseEMLCyrilic() throws FileNotFoundException, MessagingException {
		final File emlFile = new File(testDataRoot, "cyrilic.eml");

		final ReceivedEmail email = EMLParser.create().parse(emlFile);

		assertEquals("Tijana <tijan@gmail.com>", email.from().toString());
		assertEquals("testapp1@esolut.ions", email.to()[0].toString());
		assertEquals("testtest", email.subject());

		final List<EmailMessage> messages = email.messages();

		assertEquals(2, messages.size());

		assertEquals(MimeTypes.MIME_TEXT_PLAIN, messages.get(0).getMimeType());
		assertEquals("", messages.get(0).getContent().trim());

		assertEquals(MimeTypes.MIME_TEXT_HTML, messages.get(1).getMimeType());
		assertEquals("<div dir=\"ltr\"><br></div>", messages.get(1).getContent().trim());

		final List<EmailAttachment<? extends DataSource>> attachments = email.attachments();

		assertEquals(1, attachments.size());

		final EmailAttachment att = attachments.get(0);

		assertEquals("Copy of РЕКРЕАТИВНА ЕСТЕТСКА ГИМНАСТИКА-флајер - 4.docx", att.getName());
	}

	@Test
	void testSimpleEML() throws FileNotFoundException, MessagingException {
		final File emlFile = new File(testDataRoot, "simple.eml");

		final ReceivedEmail email = EMLParser.create().parse(emlFile);

		assertEquals("sender@emailhost.com", email.from().toString());
		assertEquals("recipient@emailhost.com", email.to()[0].toString());
		assertEquals("Email subject", email.subject());

		final List<EmailMessage> messages = email.messages();

		assertEquals(1, messages.size());

		assertEquals(MimeTypes.MIME_TEXT_HTML, messages.get(0).getMimeType());
		assertEquals("<p><strong>Project Name: Some Project and the body continues...</p>", messages.get(0).getContent().trim());

		final List<EmailAttachment<? extends DataSource>> attachments = email.attachments();

		assertEquals(2, attachments.size());

		EmailAttachment att = attachments.get(0);
		assertEquals("AM22831 Cover Sheet.pdf", att.getName());

		att = attachments.get(1);
		assertEquals("AM22831 Manufacturing Status.xls", att.getName());
	}

	@Test
	void testSimpleNullEML() throws FileNotFoundException, MessagingException {
		final File emlFile = new File(testDataRoot, "simple-null.eml");

		final ReceivedEmail email = EMLParser.create().parse(emlFile);

		assertNull(email.from());
		assertEquals("recipient@emailhost.com", email.to()[0].toString());
		assertEquals("Email subject", email.subject());

		final List<EmailMessage> messages = email.messages();

		assertEquals(1, messages.size());

		assertEquals(MimeTypes.MIME_TEXT_HTML, messages.get(0).getMimeType());
		assertEquals("<p><strong>Project Name: Some Project and the body continues...</p>", messages.get(0).getContent().trim());

		final List<EmailAttachment<? extends DataSource>> attachments = email.attachments();

		assertEquals(2, attachments.size());

		EmailAttachment att = attachments.get(0);
		assertEquals("no-name.pdf", att.getName());

		att = attachments.get(1);
		assertEquals("no-name.excel", att.getName());
	}
}
