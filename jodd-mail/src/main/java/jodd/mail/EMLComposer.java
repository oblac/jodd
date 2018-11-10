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

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class EMLComposer extends EMLProperties<EMLComposer> {

	public static EMLComposer create() {
		return new EMLComposer();
	}

	/**
	 * Creates EML string from given {@link Email}.
	 *
	 * @param email {@link Email} from which to create EML {@link String}.
	 * @return {@link String} with EML content.
	 */
	public String compose(final Email email) {
		if (getSession() == null) {
			createSession(getProperties());
		}

		final OutputStreamTransport ost = new OutputStreamTransport(getSession());

		final SendMailSession sendMailSession = new SendMailSession(getSession(), ost);

		sendMailSession.sendMail(email);

		return ost.getEml();
	}


	/**
	 * Creates EML string from given {@link ReceivedEmail}.
	 *
	 * @param receivedEmail {@link ReceivedEmail} from which to create EML {@link String}.
	 * @return {@link String} with EML content.
	 */
	public String compose(final ReceivedEmail receivedEmail) {
		Message msg = receivedEmail.originalMessage();

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			msg.writeTo(outputStream);
		} catch (IOException | MessagingException e) {
			throw new MailException(e);
		}

		return outputStream.toString();
	}


	/**
	 * Special transport that writes message into the {@link OutputStream}.
	 */
	private static class OutputStreamTransport extends Transport {

		/**
		 * Creates a new {@link OutputStreamTransport}.
		 *
		 * @param session {@link Session}.
		 */
		public OutputStreamTransport(final Session session) {
			super(session, new URLName("JODD_MAIL_2_EML", null, -1, null, null, null));
		}

		/**
		 * Sends a message.
		 *
		 * @param msg       {@link Message} to send.
		 * @param addresses array of {@link Address}es to send to.
		 */
		@Override
		public void sendMessage(final Message msg, final Address[] addresses) {
			final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			try {
				msg.writeTo(outputStream);
			} catch (IOException | MessagingException e) {
				throw new MailException(e);
			}

			eml = outputStream.toString();
		}

		/**
		 * Returns the EML content.
		 *
		 * @return EML content.
		 */
		public String getEml() {
			return eml;
		}

		/**
		 * String with EML content.
		 */
		private String eml;
	}
}
