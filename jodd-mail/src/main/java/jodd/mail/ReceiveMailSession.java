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
 * Encapsulates {@link Email} receiving session. Prepares and receives {@link Email}s.
 * Some methods do not work on POP3 servers.
 */
//TODO: should this implement AutoClosable from MailSession?
public class ReceiveMailSession extends MailSession<Store> {

	/**
	 * Default folder.
	 */
	protected static final String DEFAULT_FOLDER = "INBOX";

	/**
	 * The current folder.
	 */
	private Folder folder;

	static {
		EmailUtil.setupSystemMailProperties();
	}

	/**
	 * Creates new mail session.
	 *
	 * @param session {@link Session}.
	 * @param store   {@link Store}.
	 */
	public ReceiveMailSession(final Session session, final Store store) {
		super(session, store);
	}

	@Override
	public Store getService() {
		return (Store) service;
	}

	// ---------------------------------------------------------------- folders

	/**
	 * Returns array of all {@link Folder}s as {@link String}s. You can use these names in
	 * {@link #useFolder(String)} method.
	 *
	 * @return array of all {@link Folder}s as {@link String}s.
	 */
	public String[] getAllFolders() {
		final Folder[] folders;
		try {
			folders = getService().getDefaultFolder().list("*");
		} catch (final MessagingException msgexc) {
			throw new MailException("Failed to connect to folder", msgexc);
		}
		final String[] folderNames = new String[folders.length];

		for (int i = 0; i < folders.length; i++) {
			final Folder folder = folders[i];
			folderNames[i] = folder.getFullName();
		}
		return folderNames;
	}

	/**
	 * Opens new folder and closes previously opened folder.
	 *
	 * @param folderName Folder to open
	 */
	public void useFolder(final String folderName) {
		closeFolderIfOpened();

		try {
			this.folder = getService().getFolder(folderName);

			try {
				openFolder(Folder.READ_WRITE, folderName);
			} catch (final MailException ignore) {
				openFolder(Folder.READ_ONLY, folderName);
			}
		} catch (final MessagingException msgexc) {
			throw new MailException("Failed to connect to folder: " + folderName, msgexc);
		}
	}

	// ---------------------------------------------------------------- open

	private void openFolder(final int mode, final String folderNameForErr) throws MailException {
		try {
			folder.open(mode);
		} catch (final MessagingException msgexc) {
			throw new MailException("Failed to open folder: " + folderNameForErr, msgexc);
		}
	}

	/**
	 * Opens default folder: DEFAULT_FOLDER.
	 */
	public void useDefaultFolder() {
		closeFolderIfOpened();
		useFolder(DEFAULT_FOLDER);
	}

	// ---------------------------------------------------------------- message count

	/**
	 * Returns number of messages.
	 *
	 * @return The number of messages.
	 */
	public int getMessageCount() {
		if (folder == null) {
			useDefaultFolder();
		}
		try {
			return folder.getMessageCount();
		} catch (final MessagingException msgexc) {
			throw new MailException(msgexc);
		}
	}

	/**
	 * Returns the number of new messages.
	 *
	 * @return The number of new message.
	 */
	public int getNewMessageCount() {
		if (folder == null) {
			useDefaultFolder();
		}
		try {
			return folder.getNewMessageCount();
		} catch (final MessagingException msgexc) {
			throw new MailException(msgexc);
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
		} catch (final MessagingException msgexc) {
			throw new MailException(msgexc);
		}
	}

	/**
	 * Returns the number of deleted messages.
	 *
	 * @return The number of deleted messages.
	 */
	public int getDeletedMessageCount() {
		if (folder == null) {
			useDefaultFolder();
		}
		try {
			return folder.getDeletedMessageCount();
		} catch (final MessagingException msgexc) {
			throw new MailException(msgexc);
		}
	}

	// ---------------------------------------------------------------- receive emails

	/**
	 * Receives all emails. Messages are not modified. However, servers
	 * may set SEEN flag anyway, so we force messages to remain
	 * unseen.
	 *
	 * @return array of {@link ReceivedEmail}s.
	 * @see #receive(EmailFilter, Flags)
	 */
	public ReceivedEmail[] receiveEmail() {
		return receive(null, null);
	}

	/**
	 * Receives all emails that matches given {@link EmailFilter}.
	 * Messages are not modified. However, servers may set SEEN flag anyway,
	 * so we force messages to remain unseen.
	 *
	 * @param filter {@link EmailFilter}
	 * @return array of {@link ReceivedEmail}s.
	 * @see #receive(EmailFilter, Flags)
	 */
	public ReceivedEmail[] receiveEmail(final EmailFilter filter) {
		return receive(filter, null);
	}

	/**
	 * Receives all emails and mark all messages as 'seen' (ie 'read').
	 *
	 * @return array of {@link ReceivedEmail}s.
	 * @see #receiveEmailAndMarkSeen(EmailFilter)
	 */
	public ReceivedEmail[] receiveEmailAndMarkSeen() {
		return receiveEmailAndMarkSeen(null);
	}

	/**
	 * Receives all emails that matches given {@link EmailFilter}
	 * and mark them as 'seen' (ie 'read').
	 *
	 * @param filter {@link EmailFilter}
	 * @return array of {@link ReceivedEmail}s.
	 * @see #receive(EmailFilter, Flags)
	 */
	public ReceivedEmail[] receiveEmailAndMarkSeen(final EmailFilter filter) {
		final Flags flags = new Flags();
		flags.add(Flags.Flag.SEEN);
		return receive(filter, flags);
	}

	/**
	 * Receives all emails and mark all messages as 'seen' and 'deleted'.
	 *
	 * @return array of {@link ReceivedEmail}s.
	 */
	public ReceivedEmail[] receiveEmailAndDelete() {
		return receiveEmailAndDelete(null);
	}

	/**
	 * Receives all emails that matches given {@link EmailFilter} and
	 * mark all messages as 'seen' and 'deleted'.
	 *
	 * @param filter {@link EmailFilter}
	 * @return array of {@link ReceivedEmail}s.
	 * @see #receive(EmailFilter, Flags) s
	 */
	public ReceivedEmail[] receiveEmailAndDelete(final EmailFilter filter) {
		final Flags flags = new Flags();
		flags.add(Flags.Flag.SEEN);
		flags.add(Flags.Flag.DELETED);
		return receive(filter, flags);
	}

	/**
	 * Receives all emails that match given {@link EmailFilter} and set given {@link Flags}.
	 * Both filter and flags to set are optional. If flags to set is not provided, it forces 'seen'
	 * flag to be unset.
	 *
	 * @param filter     {@link EmailFilter filter}
	 * @param flagsToSet {@link Flags} to filter on
	 * @return array of {@link ReceivedEmail}.
	 */
	public ReceivedEmail[] receive(final EmailFilter filter, final Flags flagsToSet) {
		if (folder == null) {
			useDefaultFolder();
		}

		final Message[] messages;

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

			final ReceivedEmail[] emails = new ReceivedEmail[messages.length];

			for (int i = 0; i < messages.length; i++) {
				final Message msg = messages[i];

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
		} catch (final MessagingException msgexc) {
			throw new MailException("Failed to fetch messages", msgexc);
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
			} catch (final MessagingException ignore) {
			}
		}
	}

	@Override
	public void close() {
		closeFolderIfOpened();
		super.close();
	}
}
