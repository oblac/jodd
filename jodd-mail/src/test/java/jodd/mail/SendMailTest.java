// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.io.StreamUtil;
import jodd.mail.att.ByteArrayAttachment;
import jodd.util.MimeTypes;
import org.junit.Test;

import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static jodd.mail.EmailAttachment.attachment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

public class SendMailTest {

	@Test
	public void testFromToBccCc() throws MessagingException, IOException {
		Email email = Email.create()
				.from("from")
				.to("to1", "to2")
				.cc("cc1", "cc2")
				.bcc("bcc1", "bcc2");

		Message message = createMessage(email);

		assertEquals(1, message.getFrom().length);
		assertEquals("from", message.getFrom()[0].toString());

		assertEquals(6, message.getAllRecipients().length);

		assertEquals(2, message.getRecipients(Message.RecipientType.TO).length);
		assertEquals("to1", message.getRecipients(Message.RecipientType.TO)[0].toString());
		assertEquals("to2", message.getRecipients(Message.RecipientType.TO)[1].toString());

		assertEquals(2, message.getRecipients(Message.RecipientType.CC).length);
		assertEquals("cc1", message.getRecipients(Message.RecipientType.CC)[0].toString());
		assertEquals("cc2", message.getRecipients(Message.RecipientType.CC)[1].toString());

		assertEquals(2, message.getRecipients(Message.RecipientType.BCC).length);
		assertEquals("bcc1", message.getRecipients(Message.RecipientType.BCC)[0].toString());
		assertEquals("bcc2", message.getRecipients(Message.RecipientType.BCC)[1].toString());
	}

	@Test
	public void testSimpleText() throws MessagingException, IOException {
		Email email = Email.create()
				.from("from")
				.to("to")
				.subject("sub")
				.addText("Hello!");

		Message message = createMessage(email);

		String content = (String) message.getContent();

		assertEquals("Hello!", content);
		assertTrue(message.getDataHandler().getContentType().contains("text/plain"));
	}

	@Test
	public void testSimpleTextWithCyrilic() throws MessagingException, IOException {
		Email email = Email.create()
				.from("Тијана Милановић <t@gmail.com>")
				.to("Јодд <i@jodd.com>")
				.subject("Здраво!")
				.addText("шта радиш?");

		Message message = createMessage(email);

		String content = (String) message.getContent();

		assertEquals("шта радиш?", content);
		assertTrue(message.getDataHandler().getContentType().contains("text/plain"));

		assertEquals("=?UTF-8?B?0KLQuNGY0LDQvdCwINCc0LjQu9Cw0L3QvtCy0LjRmw==?= <t@gmail.com>", message.getFrom()[0].toString());
		assertEquals("=?UTF-8?B?0IjQvtC00LQ=?= <i@jodd.com>", message.getRecipients(Message.RecipientType.TO)[0].toString());
	}

	@Test
	public void testTextHtml() throws MessagingException, IOException {
		Email email = Email.create()
				.from("from")
				.to("to")
				.subject("sub")
				.addText("Hello!")
				.addHtml("<html><body><h1>Hey!</h1></body></html>");

		Message message = createMessage(email);

		assertEquals(1, message.getFrom().length);
		assertEquals("from", message.getFrom()[0].toString());

		assertEquals(1, message.getRecipients(Message.RecipientType.TO).length);
		assertEquals("to", message.getRecipients(Message.RecipientType.TO)[0].toString());

		assertEquals("sub", message.getSubject());

		// wrapper
		MimeMultipart multipart = (MimeMultipart) message.getContent();
		assertEquals(1, multipart.getCount());
		assertTrue(multipart.getContentType().contains("multipart/mixed"));

		// inner content
		MimeBodyPart mimeBodyPart = (MimeBodyPart) multipart.getBodyPart(0);
		MimeMultipart mimeMultipart = (MimeMultipart) mimeBodyPart.getContent();
		assertEquals(2, mimeMultipart.getCount());
		assertTrue(mimeMultipart.getContentType().contains("multipart/alternative"));

		MimeBodyPart bodyPart = (MimeBodyPart) mimeMultipart.getBodyPart(0);
		assertEquals("Hello!", bodyPart.getContent());
		assertTrue(bodyPart.getDataHandler().getContentType().contains("text/plain"));

		bodyPart = (MimeBodyPart) mimeMultipart.getBodyPart(1);
		assertEquals("<html><body><h1>Hey!</h1></body></html>", bodyPart.getContent());
		assertTrue(bodyPart.getDataHandler().getContentType().contains("text/html"));
	}

	@Test
	public void testTextHtmlEmbedAttach1() throws MessagingException, IOException {
		Email email = Email.create()
				.from("from")
				.to("to")
				.subject("sub")
				.addText("Hello!")
				.addHtml("<html><body><h1>Hey!</h1><img src='cid:c.png'></body></html>")
				.embed(attachment().setName("c.png").bytes(new byte[] {1, 2, 3, 4, 5, 6, 7}))
				.attach(attachment().setName("file.zip").bytes(new byte[] {11, 12, 13, 14, 15}));

		assertEmail(email);
	}

	@Test
	public void testTextHtmlEmbedAttach2() throws MessagingException, IOException {
		Email email = new Email();

		email.setFrom("from");
		email.setTo("to");
		email.setSubject("sub");

		EmailMessage testMessage = new EmailMessage("Hello!", MimeTypes.MIME_TEXT_PLAIN);
		email.addMessage(testMessage);

		EmailMessage htmlMessage = new EmailMessage(
				"<html><body><h1>Hey!</h1><img src='cid:c.png'></body></html>", MimeTypes.MIME_TEXT_HTML);
		email.addMessage(htmlMessage);

		EmailAttachment embeddedAttachment = new ByteArrayAttachment(new byte[]{1,2,3,4,5,6,7}, "image/png", "c.png", "c.png");
		embeddedAttachment.setEmbeddedMessage(htmlMessage);
		email.attach(embeddedAttachment);

		EmailAttachment attachment = new ByteArrayAttachment(new byte[]{11,12,13,14,15}, "application/zip", "file.zip", "file.zip");
		email.attach(attachment);

		assertEmail(email);
	}

	// ---------------------------------------------------------------- util

	private void assertEmail(Email email) throws MessagingException, IOException {
		Message message = createMessage(email);

		assertEquals(1, message.getFrom().length);
		assertEquals("from", message.getFrom()[0].toString());

		assertEquals(1, message.getRecipients(Message.RecipientType.TO).length);
		assertEquals("to", message.getRecipients(Message.RecipientType.TO)[0].toString());

		assertEquals("sub", message.getSubject());

		// wrapper
		MimeMultipart multipart = (MimeMultipart) message.getContent();
		assertEquals(2, multipart.getCount());

		// inner content #1
		MimeBodyPart mimeBodyPart = (MimeBodyPart) multipart.getBodyPart(0);
		MimeMultipart mimeMultipart = (MimeMultipart) mimeBodyPart.getContent();
		assertEquals(2, mimeMultipart.getCount());

		MimeBodyPart bodyPart = (MimeBodyPart) mimeMultipart.getBodyPart(0);
		assertEquals("Hello!", bodyPart.getContent());

		// html message
		bodyPart = (MimeBodyPart) mimeMultipart.getBodyPart(1);
		MimeMultipart htmlMessage = (MimeMultipart) bodyPart.getContent();
		assertTrue(htmlMessage.getContentType().contains("multipart/related"));
		assertEquals(2, htmlMessage.getCount());

		// html - text
		MimeBodyPart htmlMimeBodyPart = (MimeBodyPart) htmlMessage.getBodyPart(0);
		assertEquals("<html><body><h1>Hey!</h1><img src='cid:c.png'></body></html>", htmlMimeBodyPart.getContent());
		assertTrue(htmlMimeBodyPart.getDataHandler().getContentType().contains("text/html"));

		// html - embedded
		htmlMimeBodyPart = (MimeBodyPart) htmlMessage.getBodyPart(1);
		DataSource dataSource = htmlMimeBodyPart.getDataHandler().getDataSource();
		assertEquals("image/png", dataSource.getContentType());
		assertArrayEquals(new byte[] {1,2,3,4,5,6,7}, read(dataSource));

		// inner content #2
		mimeBodyPart = (MimeBodyPart) multipart.getBodyPart(1);
		dataSource = mimeBodyPart.getDataHandler().getDataSource();
		assertEquals("application/zip", dataSource.getContentType());
		assertArrayEquals(new byte[] {11,12,13,14,15}, read(dataSource));
	}

	private Message createMessage(Email email) throws MessagingException {
		SendMailSession testSendMailSession = new SendMailSession(null, null);
		return testSendMailSession.createMessage(email, null);
	}

	private byte[] read(DataSource dataSource) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StreamUtil.copy(dataSource.getInputStream(), baos);
		return baos.toByteArray();
	}

}