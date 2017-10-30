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

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates email sending session. Prepares and sends message(s).
 */
public class SendMailSession implements AutoCloseable {

	private static final String ALTERNATIVE = "alternative";
	private static final String RELATED = "related";
	private static final String CHARSET = ";charset=";
	private static final String INLINE = "inline";
	
	protected final Session mailSession;
	protected final Transport mailTransport;

	static {
		EmailUtil.setupSystemMail();
	}

	/**
	 * Creates new mail session.
	 */
	public SendMailSession(Session session, Transport transport) {
		this.mailSession = session;
		this.mailTransport = transport;
	}

	/**
	 * Opens mail session.
	 */
	public void open() {
		try {
			mailTransport.connect();
		} catch (MessagingException msex) {
			throw new MailException("Failed to connect", msex);
		}
	}

	/**
	 * Returns {@code true} if mail session is still connected.
	 */
	public boolean isConnected() {
		return mailTransport.isConnected();
	}

	/**
	 * Prepares message and sends it.
	 * Returns Message ID of sent email.
	 */
	public String sendMail(Email mail) {
		MimeMessage msg;
		try {
			msg = createMessage(mail, mailSession);
		} catch (MessagingException mex) {
			throw new MailException("Failed to prepare email: " + mail, mex);
		}
		try {
			mailTransport.sendMessage(msg, msg.getAllRecipients());

			return msg.getMessageID();
		} catch (MessagingException mex) {
			throw new MailException("Failed to send email: " + mail, mex);
		}
	}

	/**
	 * Closes session.
	 */
	@Override
	public void close() {
		try {
			mailTransport.close();
		} catch (MessagingException mex) {
			throw new MailException("Failed to close session", mex);
		}
	}

	
	// ---------------------------------------------------------------- adapter

	/**
	 * Creates new JavaX message from {@link Email email}.
	 */
	protected MimeMessage createMessage(Email email, Session session) throws MessagingException {
		MimeMessage msg = new MimeMessage(session);

		msg.setFrom(email.getFrom().toInternetAddress());

		// to
		int totalTo = email.getTo().length;
		InternetAddress[] address = new InternetAddress[totalTo];
		for (int i = 0; i < totalTo; i++) {
			address[i] = email.getTo()[i].toInternetAddress();
		}
		msg.setRecipients(Message.RecipientType.TO, address);

		// replyTo
		if (email.getReplyTo() != null) {
			int totalReplyTo = email.getReplyTo().length;
			address = new InternetAddress[totalReplyTo];
			for (int i = 0; i < totalReplyTo; i++) {
				address[i] = email.getReplyTo()[i].toInternetAddress();
			}
			msg.setReplyTo(address);
		}

		// cc
		if (email.getCc() != null) {
			int totalCc = email.getCc().length;
			address = new InternetAddress[totalCc];
			for (int i = 0; i < totalCc; i++) {
				address[i] = email.getCc()[i].toInternetAddress();
			}
			msg.setRecipients(Message.RecipientType.CC, address);
		}

		// bcc
		if (email.getBcc() != null) {
			int totalBcc = email.getBcc().length;
			address = new InternetAddress[totalBcc];
			for (int i = 0; i < totalBcc; i++) {
				address[i] = email.getBcc()[i].toInternetAddress();
			}
			msg.setRecipients(Message.RecipientType.BCC, address);
		}

		// subject & date

		if (email.getSubjectEncoding() != null) {
			msg.setSubject(email.getSubject(), email.getSubjectEncoding());
		} else {
			msg.setSubject(email.getSubject());
		}


		Date date = email.getSentDate();
		if (date == null) {
			date = new Date();
		}
		msg.setSentDate(date);

		// headers
		Map<String, String> headers = email.getAllHeaders();
		if (headers != null) {
			for (Map.Entry<String, String> stringStringEntry : headers.entrySet()) {
				String value = stringStringEntry.getValue();
				msg.setHeader(stringStringEntry.getKey(), value);
			}
		}

		// message data and attachments
		final List<EmailMessage> messages = email.getAllMessages();
		final List<EmailAttachment> attachments =
			email.getAttachments() == null ? null : new ArrayList<>(email.getAttachments());
		final int totalMessages = messages.size();

		if ((attachments == null) && (totalMessages == 1)) {
			// special case: no attachments and just one content
			EmailMessage emailMessage = messages.get(0);

			msg.setContent(emailMessage.getContent(), emailMessage.getMimeType() + CHARSET + emailMessage.getEncoding());

		} else {
			Multipart multipart = new MimeMultipart();
			Multipart msgMultipart = multipart;

			if (totalMessages > 1) {
				MimeBodyPart bodyPart = new MimeBodyPart();
				msgMultipart = new MimeMultipart(ALTERNATIVE);
				bodyPart.setContent(msgMultipart);
				multipart.addBodyPart(bodyPart);
			}

			for (EmailMessage emailMessage : messages) {
				// detect embedded attachments
				List<EmailAttachment> embeddedAttachments = filterEmbeddedAttachments(attachments, emailMessage);

				MimeBodyPart bodyPart = new MimeBodyPart();

				if (embeddedAttachments == null) {
					// no embedded attachments, just add message
					bodyPart.setContent(emailMessage.getContent(), emailMessage.getMimeType() + CHARSET + emailMessage.getEncoding());
				}
				else {
					// embedded attachments detected, join them as related
					MimeMultipart relatedMultipart = new MimeMultipart(RELATED);

					MimeBodyPart messageData = new MimeBodyPart();

					messageData.setContent(emailMessage.getContent(), emailMessage.getMimeType() + CHARSET + emailMessage.getEncoding());

					relatedMultipart.addBodyPart(messageData);

					for (EmailAttachment att : embeddedAttachments) {
						MimeBodyPart attBodyPart = createAttachmentBodyPart(att);
						relatedMultipart.addBodyPart(attBodyPart);
					}

					bodyPart.setContent(relatedMultipart);
				}

				msgMultipart.addBodyPart(bodyPart);

			}

			if (attachments != null) {
				// attach remaining attachments
				for (EmailAttachment att : attachments) {
					MimeBodyPart attBodyPart = createAttachmentBodyPart(att);
					multipart.addBodyPart(attBodyPart);
				}
			}

			msg.setContent(multipart);
		}
		return msg;
	}

	/**
	 * Creates attachment body part. Handles regular and inline attachments.
	 */
	protected MimeBodyPart createAttachmentBodyPart(EmailAttachment attachment) throws MessagingException {
		MimeBodyPart attBodyPart = new MimeBodyPart();

		String attachmentName = attachment.getEncodedName();
		if (attachmentName != null) {
			attBodyPart.setFileName(attachmentName);
		}

		attBodyPart.setDataHandler(new DataHandler(attachment.getDataSource()));

		if (attachment.getContentId() != null) {
			attBodyPart.setContentID(StringPool.LEFT_CHEV + attachment.getContentId() + StringPool.RIGHT_CHEV);
		}
		if (attachment.isInline()) {
			attBodyPart.setDisposition(INLINE);
		}

		return attBodyPart;
	}

	/**
	 * Filters out the list of embedded attachments for given message. If none found, returns <code>null</code>.
	 */
	protected List<EmailAttachment> filterEmbeddedAttachments(List<EmailAttachment> attachments, EmailMessage emailMessage) {
		if (attachments == null) {
			return null;
		}

		List<EmailAttachment> embeddedAttachments = null;

		Iterator<EmailAttachment> iterator = attachments.iterator();

		while (iterator.hasNext()) {
			EmailAttachment emailAttachment = iterator.next();

			if (emailAttachment.isEmbeddedInto(emailMessage)) {

				if (embeddedAttachments == null) {
					embeddedAttachments = new ArrayList<>();
				}

				embeddedAttachments.add(emailAttachment);

				iterator.remove();
			}
		}

		return embeddedAttachments;
	}

}