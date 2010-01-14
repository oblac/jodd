// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.NoSuchProviderException;
import javax.mail.MessagingException;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Map;

/**
 * Encapsulates email sending session. Prepares and sends message(s).
 */
public class SendMailSession {

	protected final Session mailSession;
	protected final Transport mailTransport;

	/**
	 * Creates new mail session.
	 */
	public SendMailSession(Session session) {
		mailSession = session;
		try {
			mailTransport = mailSession.getTransport();
		} catch (NoSuchProviderException nspex) {
			throw new EmailException(nspex);
		}
	}

	/**
	 * Opens mail session.
	 */
	public void open() {
		try {
			mailTransport.connect();
		} catch (MessagingException msex) {
			throw new EmailException("Unable to connect.", msex);
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
			throw new EmailException("Unable to prepare email message: " + mail, mex);
		}
		try {
			mailTransport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));
		} catch (MessagingException mex) {
			throw new EmailException("Unable to send email message: " + mail, mex);
		}
	}

	/**
	 * Closes session.
	 */
	public void close() {
		try {
			mailTransport.close();
		} catch (MessagingException mex) {
			throw new EmailException("Unable to close session. ", mex);
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
		Map<String, String> headers = email.getHeaders();
		if (headers != null) {
			for (Map.Entry<String, String> stringStringEntry : headers.entrySet()) {
				String value = stringStringEntry.getValue();
				msg.setHeader(stringStringEntry.getKey(), value);
			}
		}

		// message data and attachments
		String text = email.getText();
		String message = email.getMessage();
		MimeBodyPart[] attachments = email.getAttachments();
		boolean isMultipart = false;
		if (attachments != null) {
			isMultipart = true;
		} else if ((text != null) && (message != null)) {
			isMultipart = true;
		}


		if (isMultipart == false) {
			if (message != null) {
				msg.setContent(message, "text/html;charset=\"" + email.getEncoding() + '\"');
			} else {
				msg.setContent(text, "text/plain;charset=\"" + email.getEncoding() + '\"');
			}
		} else {
			Multipart multipart = new MimeMultipart();
			if (text != null) {
				MimeBodyPart messageData = new MimeBodyPart();
				messageData.setContent(text, "text/plain;charset=\"" + email.getEncoding() + '\"');
				multipart.addBodyPart(messageData);
			}
			if (message != null) {
				MimeBodyPart messageData = new MimeBodyPart();
				messageData.setContent(message, "text/html;charset=\"" + email.getEncoding() + '\"');
				multipart.addBodyPart(messageData);
			}
			if (attachments != null) {
				for (MimeBodyPart attachment : attachments) {
					multipart.addBodyPart(attachment);
				}
			}
			msg.setContent(multipart);
		}
		return msg;
	}

}
