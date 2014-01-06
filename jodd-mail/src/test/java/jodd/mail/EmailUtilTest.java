// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.datetime.JDateTime;
import org.junit.Before;
import org.junit.Test;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class EmailUtilTest {

	protected String testDataRoot;

	@Before
	public void setUp() throws Exception {
		if (testDataRoot != null) {
			return;
		}
		URL data = EmailUtilTest.class.getResource("test");
		testDataRoot = data.getFile();
	}


	@Test
	public void testExtractContentType() {
		String contentType = "multipart/mixed;";
		assertEquals("multipart/mixed", EmailUtil.extractMimeType(contentType));
		assertNull(EmailUtil.extractEncoding(contentType));

		contentType = "multipart/mixed; boundary=-----";
		assertEquals("multipart/mixed", EmailUtil.extractMimeType(contentType));
		assertNull(EmailUtil.extractEncoding(contentType));

		contentType = "text/html;\n\tcharset=\"us-ascii\"";
		assertEquals("text/html", EmailUtil.extractMimeType(contentType));
		assertEquals("us-ascii", EmailUtil.extractEncoding(contentType));

		contentType = "TEXT/PLAIN; charset=US-ASCII; name=example.eml";
		assertEquals("TEXT/PLAIN", EmailUtil.extractMimeType(contentType));
		assertEquals("US-ASCII", EmailUtil.extractEncoding(contentType));
	}

	@Test
	public void testParseEML() throws FileNotFoundException, MessagingException {
		File emlFile = new File(testDataRoot, "example.eml");

		ReceivedEmail email = EmailUtil.parseEML(emlFile);

		assertEquals("Example <from@example.com>", email.getFrom());
		assertEquals("to@example.com", email.getTo()[0]);
		assertEquals("test!", email.getSubject());

		// the time is specified in GMT zone
		JDateTime jdt = new JDateTime(2010, 3, 27, 12, 11, 21, 0);
		jdt.changeTimeZone(TimeZone.getTimeZone("GMT"), TimeZone.getDefault());

		// compare
		assertEquals(jdt.convertToDate(), email.getSentDate());

		Map<String, String> headers = email.getAllHeaders();
		assertEquals("1.0", headers.get("MIME-Version"));

		List<EmailMessage> messages = email.getAllMessages();
		assertEquals(2, messages.size());

		EmailMessage msg1 = messages.get(0);
		assertEquals("Test", msg1.getContent().trim());
		assertEquals("text/plain", msg1.getMimeType());
		assertEquals("us-ascii", msg1.getEncoding());

		EmailMessage msg2 = messages.get(1);
		assertTrue(msg2.getContent().contains("Test<o:p>"));
		assertEquals("text/html", msg2.getMimeType());
		assertEquals("us-ascii", msg2.getEncoding());

		List<EmailAttachment> attachments = email.getAttachments();
		assertNull(attachments);

		List<ReceivedEmail> attachedMessages = email.getAttachedMessages();
		assertNotNull(attachedMessages);
		assertEquals(1, attachedMessages.size());

		email = attachedMessages.get(0);

		// attached message

		assertEquals("Example <from@example.com>", email.getFrom());
		assertEquals("to@example.com", email.getTo()[0]);
		assertEquals("test", email.getSubject());

		jdt = new JDateTime(2010, 3, 27, 12, 9, 46, 0);
		jdt.changeTimeZone(TimeZone.getTimeZone("GMT"), TimeZone.getDefault());
		assertEquals(jdt.convertToDate(), email.getSentDate());

		headers = email.getAllHeaders();
		assertEquals("1.0", headers.get("MIME-Version"));

		messages = email.getAllMessages();
		assertEquals(2, messages.size());

		msg1 = messages.get(0);
		assertEquals("test", msg1.getContent().trim());
		assertEquals("text/plain", msg1.getMimeType());
		assertEquals("us-ascii", msg1.getEncoding());

		msg2 = messages.get(1);
		assertTrue(msg2.getContent().contains("test</TITLE>"));
		assertEquals("text/html", msg2.getMimeType());
		assertEquals("us-ascii", msg2.getEncoding());

		attachments = email.getAttachments();
		assertNull(attachments);

		attachedMessages = email.getAttachedMessages();
		assertNull(attachedMessages);
	}

	@Test
	public void testParseEMLCyrilic() throws FileNotFoundException, MessagingException, UnsupportedEncodingException {
		File emlFile = new File(testDataRoot, "cyrilic.eml");

		ReceivedEmail email = EmailUtil.parseEML(emlFile);

		assertEquals("Tijana <tijan@gmail.com>", email.getFrom());
		assertEquals("testapp1@esolut.ions", email.getTo()[0]);
		assertEquals("testtest", email.getSubject());

		List<EmailMessage> messages = email.getAllMessages();

		assertEquals(2, messages.size());

		assertEquals("text/plain", messages.get(0).getMimeType());
		assertEquals("", messages.get(0).getContent().trim());

		assertEquals("text/html", messages.get(1).getMimeType());
		assertEquals("<div dir=\"ltr\"><br></div>", messages.get(1).getContent().trim());

		List<EmailAttachment> attachments = email.getAttachments();

		assertEquals(1, attachments.size());

		EmailAttachment att = attachments.get(0);

		assertEquals("Copy of РЕКРЕАТИВНА ЕСТЕТСКА ГИМНАСТИКА-флајер - 4.docx", att.getName());
	}

	@Test
	public void testSimpleEML() throws FileNotFoundException, MessagingException {
		File emlFile = new File(testDataRoot, "simple.eml");

		ReceivedEmail email = EmailUtil.parseEML(emlFile);

		assertEquals("sender@emailhost.com", email.getFrom());
		assertEquals("recipient@emailhost.com", email.getTo()[0]);
		assertEquals("Email subject", email.getSubject());

		List<EmailMessage> messages = email.getAllMessages();

		assertEquals(1, messages.size());

		assertEquals("text/html", messages.get(0).getMimeType());
		assertEquals("<p><strong>Project Name: Some Project and the body continues...</p>", messages.get(0).getContent().trim());

		List<EmailAttachment> attachments = email.getAttachments();

		assertEquals(2, attachments.size());

		EmailAttachment att = attachments.get(0);
		assertEquals("AM22831 Cover Sheet.pdf", att.getName());

		att = attachments.get(1);
		assertEquals("AM22831 Manufacturing Status.xls", att.getName());
	}

	@Test
	public void testSimpleNullEML() throws FileNotFoundException, MessagingException {
		File emlFile = new File(testDataRoot, "simple-null.eml");

		ReceivedEmail email = EmailUtil.parseEML(emlFile);

		assertNull(email.getFrom());
		assertEquals("recipient@emailhost.com", email.getTo()[0]);
		assertEquals("Email subject", email.getSubject());

		List<EmailMessage> messages = email.getAllMessages();

		assertEquals(1, messages.size());

		assertEquals("text/html", messages.get(0).getMimeType());
		assertEquals("<p><strong>Project Name: Some Project and the body continues...</p>", messages.get(0).getContent().trim());

		List<EmailAttachment> attachments = email.getAttachments();

		assertEquals(2, attachments.size());

		EmailAttachment att = attachments.get(0);
		assertNull(att.getName());

		att = attachments.get(1);
		assertNull(att.getName());
	}
}