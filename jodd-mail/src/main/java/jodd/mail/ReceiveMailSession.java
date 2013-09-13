// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.JoddCore;
import jodd.JoddMail;
import jodd.io.FastByteArrayOutputStream;
import jodd.io.StreamUtil;
import jodd.util.CharUtil;
import jodd.util.StringPool;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimePart;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;

/**
 * Encapsulates email receiving session. Prepares and receives message(s).
 */
public class ReceiveMailSession {

	protected static final String DEFAULT_FOLDER = "INBOX";
	protected static final String STR_CHARSET = "charset=";

	protected final Session session;
	protected final Store store;

	static {
		JoddMail.mailSystem.defineJavaMailSystemProperties();
	}

	/**
	 * Creates new mail session.
	 */
	public ReceiveMailSession(Session session, Store store) {
		this.session = session;
		this.store = store;
	}

	protected Folder folder;


	/**
	 * Opens session.
	 */
	public void open() {
		try {
			store.connect();
		} catch (MessagingException msex) {
			throw new MailException("Unable to open session", msex);
		}
	}

	// ---------------------------------------------------------------- folders

	/**
	 * Returns list of all folders. You can use these names in
	 * {@link #useFolder(String)} method.
	 */
	public String[] getAllFolders() {
		Folder[] folders;
		try {
			folders = store.getDefaultFolder().list( "*" );
		} catch (MessagingException msex) {
			throw new MailException("Unable to connect to folder", msex);
		}
		String[] folderNames = new String[folders.length];

		for (int i = 0; i < folders.length; i++) {
			Folder folder = folders[i];
			folderNames[i] = folder.getFullName();
		}
		return folderNames;
	}

	/**
	 * Opens new folder and closes previously opened folder.
	 */
	public void useFolder(String folderName) {
		closeFolderIfOpened();
		try {
			folder = store.getFolder(folderName);
		} catch (MessagingException msex) {
			throw new MailException("Unable to connect to folder: " + folderName, msex);
		}
		try {
			folder.open(Folder.READ_WRITE);
		} catch (MessagingException ignore) {
			try {
				folder.open(Folder.READ_ONLY);
			} catch (MessagingException msex) {
				throw new MailException("Unable to open folder: " + folderName, msex);
			}
		}
	}

	/**
	 * Opens default folder: INBOX.
	 */
	public void useDefaultFolder() {
		closeFolderIfOpened();
		useFolder(DEFAULT_FOLDER);
	}

	/**
	 * Returns number of messages.
	 */
	public int getMessageCount() {
		if (folder == null) {
			useDefaultFolder();
		}
		try {
			return folder.getMessageCount();
		} catch (MessagingException mex) {
			throw new MailException("Unable to read number of messages", mex);
		}
	}

	/**
	 * Returns the number of new messages. Not available for the POP3. 
	 */
	public int getNewMessageCount() {
		if (folder == null) {
			useDefaultFolder();
		}
		try {
			return folder.getNewMessageCount();
		} catch (MessagingException mex) {
			throw new MailException("Unable to read number of new messages", mex);
		}
	}

	// ---------------------------------------------------------------- receive emails

	/**
	 * Receives all emails. Messages are not modified. However, servers
	 * do may set SEEN flag anyway, so we force messages to remain
	 * unseen.
	 */
	public ReceivedEmail[] receiveEmail() {
		return receive(null, null);
	}
	/**
	 * Receives all emails that matches given {@link EmailFilter filter}.
	 * Messages are not modified. However, servers do may set SEEN flag anyway,
	 * so we force messages to remain unseen.
	 */
	public ReceivedEmail[] receiveEmail(EmailFilter emailFilter) {
		return receive(emailFilter, null);
	}

	/**
	 * Receives all emails and mark all messages as 'seen' (ie 'read').
	 */
	public ReceivedEmail[] receiveEmailAndMarkSeen() {
		return receiveEmailAndMarkSeen(null);
	}
	/**
	 * Receives all emails that matches given {@link EmailFilter filter}
	 * and mark them as 'seen' (ie 'read').
	 */
	public ReceivedEmail[] receiveEmailAndMarkSeen(EmailFilter emailFilter) {
		Flags flags = new Flags();
		flags.add(Flags.Flag.SEEN);
		return receive(emailFilter, flags);
	}

	/**
	 * Receives all emails and mark all messages as 'seen' and 'deleted'.
	 */
	public ReceivedEmail[] receiveEmailAndDelete() {
		return receiveEmailAndDelete(null);
	}

	/**
	 * Receives all emails that matches given {@link EmailFilter filter} and
	 * mark all messages as 'seen' and 'deleted'.
	 */
	public ReceivedEmail[] receiveEmailAndDelete(EmailFilter emailFilter) {
		Flags flags = new Flags();
		flags.add(Flags.Flag.SEEN);
		flags.add(Flags.Flag.DELETED);
		return receive(emailFilter, flags);
	}

	/**
	 * Receives all emails that matches given {@link EmailFilter filter}
	 * and set given flags. Both filter and flags to set are optional.
	 * If flags to set is not provided, it forces 'seen' flag to be unset.
	 */
	public ReceivedEmail[] receive(EmailFilter filter, Flags flagsToSet) {
		if (folder == null) {
			useDefaultFolder();
		}

		Message[] messages;

		// todo add FetchProfile option for just headers

		try {
			if (filter == null) {
				messages = folder.getMessages();
			} else {
				messages = folder.search(filter.getSearchTerm());
			}

			if (messages.length == 0) {
				return null;
			}

			// process messages

			ReceivedEmail[] emails = new ReceivedEmail[messages.length];

			for (int i = 0; i < messages.length; i++) {
				Message msg = messages[i];

				if (flagsToSet != null) {
					msg.setFlags(flagsToSet, true);
				}
				emails[i] = message2Email(msg);

				if (flagsToSet == null && emails[i].isSeen() == false) {
					msg.setFlag(Flags.Flag.SEEN, false);
				}
			}

			return emails;
		}catch (MessagingException msex) {
			throw new MailException("Unable to fetch messages", msex);
		} catch (IOException ioex) {
			throw new MailException("Unable to read message content", ioex);
		}
	}


	/**
	 * Convert java <code>Message</code> to a {@link ReceivedEmail}.
	 */
	@SuppressWarnings({"unchecked"})
	protected ReceivedEmail message2Email(Message msg) throws MessagingException, IOException {
		ReceivedEmail email = new ReceivedEmail();

		// flags
		email.setFlags(msg.getFlags());

		// msg no
		email.setMessageNumber(msg.getMessageNumber());

		// standard stuff
		email.setFrom(msg.getFrom()[0].toString());
		email.setTo(address2String(msg.getRecipients(Message.RecipientType.TO)));
		email.setCc(address2String(msg.getRecipients(Message.RecipientType.CC)));
		email.setBcc(address2String(msg.getRecipients(Message.RecipientType.BCC)));
		email.setSubject(msg.getSubject());
		Date recvDate = msg.getReceivedDate();
		if (recvDate == null) {
			recvDate = new Date();
		}
		email.setReceiveDate(recvDate);
		email.setSentDate(msg.getSentDate());

		// copy headers
		Enumeration<Header> headers = msg.getAllHeaders();
		while (headers.hasMoreElements()) {
			Header header = headers.nextElement();
			email.setHeader(header.getName(), header.getValue());
		}

		// content
		processPart(email, msg);

		return email;
	}


	/**
	 * Process single part of received message. All parts are simple added to the message, i.e. hierarchy is not saved.
	 */
	protected void processPart(ReceivedEmail email, Part part) throws IOException, MessagingException {
		Object content = part.getContent();

		if (content instanceof String) {
			String stringContent = (String) content;

			String disposition = part.getDisposition();
			if (disposition != null && disposition.equals(Part.ATTACHMENT)) {
				String mimeType = extractMimeType(part.getContentType());
				String fileName = part.getFileName();
				String contentId = (part instanceof MimePart) ? ((MimePart)part).getContentID() : null;

				email.addAttachment(fileName, mimeType, contentId, stringContent.getBytes(JoddCore.encoding));
			} else {
				String contentType = part.getContentType();
				email.addMessage(stringContent, extractMimeType(contentType), extractEncoding(contentType));
			}
		} else if (content instanceof Multipart) {
			Multipart mp = (Multipart) content;
			int count = mp.getCount();
			for (int i = 0; i < count; i++) {
				Part innerPart = mp.getBodyPart(i);
				processPart(email, innerPart);
			}
		} else if (content instanceof InputStream) {
			String fileName = part.getFileName();
			String contentId = (part instanceof MimePart) ? ((MimePart)part).getContentID() : null;
			String mimeType = extractMimeType(part.getContentType());

			InputStream is = (InputStream) content;
			FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream();
			StreamUtil.copy(is, fbaos);

			email.addAttachment(fileName, mimeType, contentId, fbaos.toByteArray());
		}
	}

	/**
	 * Extracts mime type from parts content type.
	 */
	protected String extractMimeType(String contentType) {
		int ndx = contentType.indexOf(';');
		String mime;
		if (ndx != -1) {
			mime = contentType.substring(0, ndx);
		} else {
			mime = contentType;
		}
		return mime;
	}

	/**
	 * Parses content type for encoding.
	 */
	protected String extractEncoding(String contentType) {
		int ndx = contentType.indexOf(';');
		String charset = ndx != -1 ? contentType.substring(ndx + 1) : StringPool.EMPTY;
		String encoding = null;

		ndx = charset.indexOf(STR_CHARSET);
		if (ndx != -1) {
			ndx += STR_CHARSET.length();
			int len = charset.length();

			if (charset.charAt(ndx) == '"') {
				ndx++;
			}
			int start = ndx;

			while (ndx < len) {
				char c = charset.charAt(ndx);
				if ((c == '"') || (CharUtil.isWhitespace(c) == true)) {
					break;
				}
				ndx++;
			}
			encoding = charset.substring(start, ndx);
		}
		return encoding;
	}

	/**
	 * Converts mail address to string.
	 */
	protected String[] address2String(Address[] addresses) {
		if (addresses == null) {
			return null;
		}
		if (addresses.length == 0) {
			return null;
		}
		String[] res = new String[addresses.length];
		for (int i = 0; i < addresses.length; i++) {
			Address address = addresses[i];
			res[i] = address.toString();
		}
		return res;
	}


	// ---------------------------------------------------------------- close

	/**
	 * Closes folder if opened and expunge deleted messages.
	 */
	protected void closeFolderIfOpened() {
		if (folder != null) {
			try {
				folder.close(true);
			} catch (MessagingException ignore) {
			}
		}
	}

	/**
	 * Closes session.
	 */
	public void close() {
		closeFolderIfOpened();
		try {
			store.close();
		} catch (MessagingException mex) {
			throw new MailException("Unable to close session", mex);
		}
	}

}
