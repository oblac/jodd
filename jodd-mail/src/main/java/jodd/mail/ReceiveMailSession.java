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

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

/**
 * Encapsulates email receiving session. Prepares and receives message(s).
 * Some methods do not work on POP3 servers.
 */
public class ReceiveMailSession {

	protected static final String DEFAULT_FOLDER = "INBOX";

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
			throw new MailException("Open session error", msex);
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
			throw new MailException("Failed to connect to folder", msex);
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
			throw new MailException("Failed to connect to folder: " + folderName, msex);
		}
		try {
			folder.open(Folder.READ_WRITE);
		} catch (MessagingException ignore) {
			try {
				folder.open(Folder.READ_ONLY);
			} catch (MessagingException msex) {
				throw new MailException("Failed to open folder: " + folderName, msex);
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

	// ---------------------------------------------------------------- message count

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
			throw new MailException(mex);
		}
	}

	/**
	 * Returns the number of new messages.
	 */
	public int getNewMessageCount() {
		if (folder == null) {
			useDefaultFolder();
		}
		try {
			return folder.getNewMessageCount();
		} catch (MessagingException mex) {
			throw new MailException(mex);
		}
	}

	/**
	 * Returns the number of unread messages.
	 */
	public int getUnreadMessageCount() {
		if (folder == null) {
			useDefaultFolder();
		}
		try {
			return folder.getUnreadMessageCount();
		} catch (MessagingException mex) {
			throw new MailException(mex);
		}
	}

	/**
	 * Returns the number of deleted messages.
	 */
	public int getDeletedMessageCount() {
		if (folder == null) {
			useDefaultFolder();
		}
		try {
			return folder.getDeletedMessageCount();
		} catch (MessagingException mex) {
			throw new MailException(mex);
		}
	}

	// ---------------------------------------------------------------- receive emails

	/**
	 * Receives all emails. Messages are not modified. However, servers
	 * may set SEEN flag anyway, so we force messages to remain
	 * unseen.
	 */
	public ReceivedEmail[] receiveEmail() {
		return receive(null, null);
	}

	/**
	 * Receives all emails that matches given {@link EmailFilter filter}.
	 * Messages are not modified. However, servers may set SEEN flag anyway,
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
				return ReceivedEmail.EMPTY_ARRAY;
			}

			// process messages

			ReceivedEmail[] emails = new ReceivedEmail[messages.length];

			for (int i = 0; i < messages.length; i++) {
				Message msg = messages[i];

				// we need to parse message BEFORE flags are set!
				emails[i] = new ReceivedEmail(msg);

				if (flagsToSet != null) {
					emails[i].setFlags(flagsToSet);
					msg.setFlags(flagsToSet, true);
				}

				if (flagsToSet == null && !emails[i].isSeen()) {
					msg.setFlag(Flags.Flag.SEEN, false);
				}
			}

			return emails;
		} catch (MessagingException msex) {
			throw new MailException("Failed to fetch messages", msex);
		}
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
			throw new MailException(mex);
		}
	}

}
