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

import jodd.io.StreamUtil;
import jodd.util.net.MimeTypes;
import org.junit.jupiter.api.Test;

import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SendMailTest {

  private static final String TO_EXAMPLE_COM = "to@example.com";
  private static final String FROM_EXAMPLE_COM = "from@example.com";
  private static final String SUB = "sub";
  private static final String HELLO = "Hello!";
  private static final String TO1_EXAMPLE_COM = "to1@example.com";
  private static final String CC1_EXAMPLE_COM = "cc1@example.com";
  private static final String BCC2_EXAMPLE_COM = "bcc2@example.com";

  private static final String FILE_ZIP = "file.zip";
  private static final String APPLICATION_ZIP = "application/zip";
  private static final String IMAGE_PNG = "image/png";
  private static final byte[] BYTES_1_7 = {1, 2, 3, 4, 5, 6, 7};
  private static final byte[] BYTES_11_15 = {11, 12, 13, 14, 15};
  private static final String C_PNG = "c.png";

  @Test
  void testFromToBccCc() throws MessagingException {
    final Email email = Email.create()
        .setFrom(FROM_EXAMPLE_COM)
        .addTo(TO1_EXAMPLE_COM).addTo("Major Tom", "to2@example.com")
        .addCc(CC1_EXAMPLE_COM).addCc("Major Carson", "cc2@example.com")
        .addBcc("Major Ben", "bcc1@example.com").addBcc(BCC2_EXAMPLE_COM);

    final Message message = createMessage(email);

    assertEquals(1, message.getFrom().length);
    assertEquals(FROM_EXAMPLE_COM, message.getFrom()[0].toString());

    assertEquals(6, message.getAllRecipients().length);

    assertEquals(2, message.getRecipients(RecipientType.TO).length);
    assertEquals(TO1_EXAMPLE_COM, message.getRecipients(RecipientType.TO)[0].toString());
    assertEquals("Major Tom <to2@example.com>", message.getRecipients(RecipientType.TO)[1].toString());

    assertEquals(2, message.getRecipients(RecipientType.CC).length);
    assertEquals(CC1_EXAMPLE_COM, message.getRecipients(RecipientType.CC)[0].toString());
    assertEquals("Major Carson <cc2@example.com>", message.getRecipients(RecipientType.CC)[1].toString());

    assertEquals(2, message.getRecipients(RecipientType.BCC).length);
    assertEquals("Major Ben <bcc1@example.com>", message.getRecipients(RecipientType.BCC)[0].toString());
    assertEquals(BCC2_EXAMPLE_COM, message.getRecipients(RecipientType.BCC)[1].toString());
  }

  @Test
  void testSimpleText() throws MessagingException, IOException {
    final Email email = Email.create()
        .setFrom(FROM_EXAMPLE_COM)
        .addTo(TO_EXAMPLE_COM)
        .setSubject(SUB)
        .addText(HELLO);

    final Message message = createMessage(email);

    final String content = (String) message.getContent();

    assertEquals(HELLO, content);
    assertTrue(message.getDataHandler().getContentType().contains("text/plain"));
  }

  @Test
  void testSimpleTextWithCyrilic() throws MessagingException, IOException {
    final Email email = Email.create()
        .setFrom("Тијана Милановић <t@gmail.com>")
        .addTo("Јодд <i@jodd.com>")
        .setSubject("Здраво!")
        .addText("шта радиш?");

    final Message message = createMessage(email);

    final String content = (String) message.getContent();

    assertEquals("шта радиш?", content);
    assertTrue(message.getDataHandler().getContentType().contains("text/plain"));

    assertEquals("=?UTF-8?B?0KLQuNGY0LDQvdCwINCc0LjQu9Cw0L3QvtCy0LjRmw==?= <t@gmail.com>", message.getFrom()[0].toString());
    assertEquals("=?UTF-8?B?0IjQvtC00LQ=?= <i@jodd.com>", message.getRecipients(RecipientType.TO)[0].toString());
  }

  @Test
  void testTextHtml() throws MessagingException, IOException {
    final Email email = Email.create()
        .setFrom(FROM_EXAMPLE_COM)
        .addTo(TO_EXAMPLE_COM)
        .setSubject(SUB)
        .addText(HELLO)
        .addHtml("<html><body><h1>Hey!</h1></body></html>");

    final Message message = createMessage(email);

    assertEquals(1, message.getFrom().length);
    assertEquals(FROM_EXAMPLE_COM, message.getFrom()[0].toString());

    assertEquals(1, message.getRecipients(RecipientType.TO).length);
    assertEquals(TO_EXAMPLE_COM, message.getRecipients(RecipientType.TO)[0].toString());

    assertEquals(SUB, message.getSubject());

    // wrapper
    final MimeMultipart multipart = (MimeMultipart) message.getContent();
    assertEquals(1, multipart.getCount());
    assertTrue(multipart.getContentType().contains("multipart/mixed"));

    // inner content
    final MimeBodyPart mimeBodyPart = (MimeBodyPart) multipart.getBodyPart(0);
    final MimeMultipart mimeMultipart = (MimeMultipart) mimeBodyPart.getContent();
    assertEquals(2, mimeMultipart.getCount());
    assertTrue(mimeMultipart.getContentType().contains("multipart/alternative"));

    MimeBodyPart bodyPart = (MimeBodyPart) mimeMultipart.getBodyPart(0);
    assertEquals(HELLO, bodyPart.getContent());
    assertTrue(bodyPart.getDataHandler().getContentType().contains(MimeTypes.MIME_TEXT_PLAIN));

    bodyPart = (MimeBodyPart) mimeMultipart.getBodyPart(1);
    assertEquals("<html><body><h1>Hey!</h1></body></html>", bodyPart.getContent());
    assertTrue(bodyPart.getDataHandler().getContentType().contains(MimeTypes.MIME_TEXT_HTML));
  }

  @Test
  void testTextHtmlEmbedAttach1() throws MessagingException, IOException {
    final Email email = Email.create()
        .setFrom(FROM_EXAMPLE_COM)
        .addTo(TO_EXAMPLE_COM)
        .setSubject(SUB)
        .addText(HELLO)
        .addHtml("<html><body><h1>Hey!</h1><img src='cid:c.png'></body></html>")
        .embedAttachment(EmailAttachment.builder().setName(C_PNG).setContent(BYTES_1_7))
        .addAttachment(EmailAttachment.builder().setName(FILE_ZIP).setContent(BYTES_11_15));

    assertEmail(email);
  }

  @Test
  void testTextHtmlEmbedAttach2() throws MessagingException, IOException {
    final Email email = new Email();

    email.setFrom(FROM_EXAMPLE_COM);
    email.addTo(TO_EXAMPLE_COM);
    email.setSubject(SUB);

    final EmailMessage testMessage = new EmailMessage(HELLO, MimeTypes.MIME_TEXT_PLAIN);
    email.addMessage(testMessage);

    final EmailMessage htmlMessage = new EmailMessage(
        "<html><body><h1>Hey!</h1><img src='cid:c.png'></body></html>",
        MimeTypes.MIME_TEXT_HTML);
    email.addMessage(htmlMessage);

    final EmailAttachment<ByteArrayDataSource> embeddedAttachment = EmailAttachment.builder()
        .setContent(BYTES_1_7, IMAGE_PNG)
        .setName(C_PNG)
        .setContentId(C_PNG)
        .setInline(true)
        .buildByteArrayDataSource();

    embeddedAttachment.setEmbeddedMessage(htmlMessage);
    email.embedAttachment(embeddedAttachment);

    final EmailAttachmentBuilder attachmentBuilder = EmailAttachment.builder()
        .setContent(BYTES_11_15, APPLICATION_ZIP)
        .setName(FILE_ZIP)
        .setContentId(FILE_ZIP);
    email.addAttachment(attachmentBuilder);

    assertEmail(email);
  }

  // ---------------------------------------------------------------- util

  private void assertEmail(final Email email) throws MessagingException, IOException {
    final Message message = createMessage(email);

    assertEquals(1, message.getFrom().length);
    assertEquals(FROM_EXAMPLE_COM, message.getFrom()[0].toString());

    assertEquals(1, message.getRecipients(RecipientType.TO).length);
    assertEquals(TO_EXAMPLE_COM, message.getRecipients(RecipientType.TO)[0].toString());


    assertEquals(SUB, message.getSubject());

    // wrapper
    final MimeMultipart multipart = (MimeMultipart) message.getContent();
    assertEquals(2, multipart.getCount());

    // inner content #1
    MimeBodyPart mimeBodyPart = (MimeBodyPart) multipart.getBodyPart(0);
    final MimeMultipart mimeMultipart = (MimeMultipart) mimeBodyPart.getContent();
    assertEquals(2, mimeMultipart.getCount());

    MimeBodyPart bodyPart = (MimeBodyPart) mimeMultipart.getBodyPart(0);
    assertEquals(HELLO, bodyPart.getContent());

    // html message
    bodyPart = (MimeBodyPart) mimeMultipart.getBodyPart(1);
    final MimeMultipart htmlMessage = (MimeMultipart) bodyPart.getContent();
    assertTrue(htmlMessage.getContentType().contains("multipart/related"));
    assertEquals(2, htmlMessage.getCount());

    // html - text
    MimeBodyPart htmlMimeBodyPart = (MimeBodyPart) htmlMessage.getBodyPart(0);
    assertEquals("<html><body><h1>Hey!</h1><img src='cid:c.png'></body></html>", htmlMimeBodyPart.getContent());
    assertTrue(htmlMimeBodyPart.getDataHandler().getContentType().contains(MimeTypes.MIME_TEXT_HTML));

    // html - embedded
    htmlMimeBodyPart = (MimeBodyPart) htmlMessage.getBodyPart(1);
    DataSource dataSource = htmlMimeBodyPart.getDataHandler().getDataSource();
    assertEquals(IMAGE_PNG, dataSource.getContentType());
    assertArrayEquals(BYTES_1_7, read(dataSource));

    // inner content #2
    mimeBodyPart = (MimeBodyPart) multipart.getBodyPart(1);
    dataSource = mimeBodyPart.getDataHandler().getDataSource();
    assertEquals(APPLICATION_ZIP, dataSource.getContentType());
    assertArrayEquals(BYTES_11_15, read(dataSource));
  }

  private Message createMessage(final Email email) throws MessagingException {
    final SendMailSession testSendMailSession = new SendMailSession(null, null);
    return testSendMailSession.createMessage(email);
  }

  private byte[] read(final DataSource dataSource) throws IOException {
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    StreamUtil.copy(dataSource.getInputStream(), os);
    return os.toByteArray();
  }
}
