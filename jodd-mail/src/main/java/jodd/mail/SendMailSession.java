// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.util.StringPool;

import javax.activation.DataHandler;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.MessagingException;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates email sending session. Prepares and sends message(s).
 */
public class SendMailSession {

	private static final String ALTERNATIVE = "alternative";
	private static final String RELATED = "related";
	private static final String CHARSET = ";charset=";
	private static final String INLINE = "inline";
	
	protected final Session mailSession;
	protected final Transport mailTransport;

	static {
		JoddMail.mailSystem.defineJavaMailSystemProperties();
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
	 * Prepares message and sends it.
	 */
	public void sendMail(Email mail) {
		Message msg;
		try {
			msg = createMessage(mail, mailSession);
		} catch (MessagingException mex) {
			throw new MailException("Failed to prepare email: " + mail, mex);
		}
		try {
			mailTransport.sendMessage(msg, msg.getAllRecipients());
		} catch (MessagingException mex) {
			throw new MailException("Failed to send email: " + mail, mex);
		}
	}

	/**
	 * Closes session.
	 */
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
	protected Message createMessage(Email email, Session session) throws MessagingException {
		Message msg = new MimeMessage(session);

		msg.setFrom(EmailUtil.string2Address(email.getFrom()));

		// to
		int totalTo = email.getTo().length;
		InternetAddress[] address = new InternetAddress[totalTo];
		for (int i = 0; i < totalTo; i++) {
			address[i] = EmailUtil.string2Address(email.getTo()[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, address);

		// replyTo
		if (email.getReplyTo() != null) {
			int totalReplyTo = email.getReplyTo().length;
			address = new InternetAddress[totalReplyTo];
			for (int i = 0; i < totalReplyTo; i++) {
				address[i] = EmailUtil.string2Address(email.getReplyTo()[i]);
			}
			msg.setReplyTo(address);
		}

		// cc
		if (email.getCc() != null) {
			int totalCc = email.getCc().length;
			address = new InternetAddress[totalCc];
			for (int i = 0; i < totalCc; i++) {
				address[i] = EmailUtil.string2Address(email.getCc()[i]);
			}
			msg.setRecipients(Message.RecipientType.CC, address);
		}

		// bcc
		if (email.getBcc() != null) {
			int totalBcc = email.getBcc().length;
			address = new InternetAddress[totalBcc];
			for (int i = 0; i < totalBcc; i++) {
				address[i] = EmailUtil.string2Address(email.getBcc()[i]);
			}
			msg.setRecipients(Message.RecipientType.BCC, address);
		}

		// subject & date
		msg.setSubject(email.getSubject());
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
		List<EmailMessage> messages = email.getAllMessages();
		List<EmailAttachment> attachments = email.getAttachments();
		int totalMessages = messages.size();

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
				} else {
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
		if (attachment.isInline()) {
			attBodyPart.setContentID(StringPool.LEFT_CHEV + attachment.getContentId() + StringPool.RIGHT_CHEV);
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
					embeddedAttachments = new ArrayList<EmailAttachment>();
				}

				embeddedAttachments.add(emailAttachment);

				iterator.remove();
			}
		}

		return embeddedAttachments;
	}

}