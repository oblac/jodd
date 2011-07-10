// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

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
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

/**
 * Encapsulates email sending session. Prepares and sends message(s).
 */
public class SendMailSession {

	private static final String ALTERNATIVE = "alternative";
	private static final String CHARSET = ";charset=";
	private static final String INLINE = "inline";
	
	protected final Session mailSession;
	protected final Transport mailTransport;

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
			throw new MailException("Unable to connect.", msex);
		}
	}

	/**
	 * Prepares message and sends it.
	 */
	public void sendMail(Email mail) {
		Message msg;
		try {
			msg = adapt(mail, mailSession);
		} catch (MessagingException mex) {
			throw new MailException("Unable to prepare email message: " + mail, mex);
		}
		try {
			mailTransport.sendMessage(msg, msg.getAllRecipients());
		} catch (MessagingException mex) {
			throw new MailException("Unable to send email message: " + mail, mex);
		}
	}

	/**
	 * Closes session.
	 */
	public void close() {
		try {
			mailTransport.close();
		} catch (MessagingException mex) {
			throw new MailException("Unable to close session. ", mex);
		}
	}

	
	// ---------------------------------------------------------------- adapter

	/**
	 * Creates new message.
	 */
	protected Message adapt(Email email, Session session) throws MessagingException {
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(email.getFrom()));

		// to
		int totalTo = email.getTo().length;
		InternetAddress[] address = new InternetAddress[totalTo];
		for (int i = 0; i < totalTo; i++) {
			address[i] = new InternetAddress(email.getTo()[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, address);

		// replyTo
		if (email.getReplyTo() != null) {
			int totalReplyTo = email.getReplyTo().length;
			address = new InternetAddress[totalReplyTo];
			for (int i = 0; i < totalReplyTo; i++) {
				address[i] = new InternetAddress(email.getReplyTo()[i]);
			}
			msg.setReplyTo(address);
		}

		// cc
		if (email.getCc() != null) {
			int totalCc = email.getCc().length;
			address = new InternetAddress[totalCc];
			for (int i = 0; i < totalCc; i++) {
				address[i] = new InternetAddress(email.getCc()[i]);
			}
			msg.setRecipients(Message.RecipientType.CC, address);
		}

		// bcc
		if (email.getBcc() != null) {
			int totalBcc = email.getBcc().length;
			address = new InternetAddress[totalBcc];
			for (int i = 0; i < totalBcc; i++) {
				address[i] = new InternetAddress(email.getBcc()[i]);
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
		LinkedList<EmailMessage> messages = email.getAllMessages();
		LinkedList<EmailAttachment> attachments = email.getAttachments();
		int totalMessages = messages.size();

		if ((attachments == null) && (totalMessages == 1)) {
			EmailMessage emailMessage = messages.get(0);
			msg.setContent(emailMessage.getContent(), emailMessage.getMimeType() + CHARSET + emailMessage.getEncoding());
		} else {
			Multipart multipart = new MimeMultipart();
			Multipart msgMultipart = multipart;
			if (totalMessages > 1) {
				MimeBodyPart body = new MimeBodyPart();
				msgMultipart = new MimeMultipart(ALTERNATIVE);
				body.setContent(msgMultipart);
				multipart.addBodyPart(body);
			}
			for (EmailMessage emailMessage : messages) {
				MimeBodyPart messageData = new MimeBodyPart();
				messageData.setContent(emailMessage.getContent(), emailMessage.getMimeType() + CHARSET + emailMessage.getEncoding());
				msgMultipart.addBodyPart(messageData);
			}
			if (attachments != null) {
				for (EmailAttachment att : attachments) {
					MimeBodyPart attBodyPart = new MimeBodyPart();
					attBodyPart.setFileName(att.getName());
					attBodyPart.setDataHandler(new DataHandler(att.getDataSource()));
					if (att.isInline()) {
						attBodyPart.setContentID(att.getContentId());
						attBodyPart.setDisposition(INLINE);
					}
					multipart.addBodyPart(attBodyPart);
				}
			}
			msg.setContent(multipart);
		}
		return msg;
	}

}