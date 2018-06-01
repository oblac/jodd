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

import com.sun.mail.pop3.POP3SSLStore;
import jodd.util.StringPool;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.URLName;
import java.io.File;
import java.util.Properties;

/**
 * POP3 SSL server.
 */
public class Pop3SslServer extends Pop3Server {

	protected static final String MAIL_POP3_SOCKET_FACTORY_PORT = "mail.pop3.socketFactory.port";
	protected static final String MAIL_POP3_SOCKET_FACTORY_CLASS = "mail.pop3.socketFactory.class";
	protected static final String MAIL_POP3_SOCKET_FACTORY_FALLBACK = "mail.pop3.socketFactory.fallback";
	protected static final int DEFAULT_SSL_PORT = 995;

	public Pop3SslServer(final String host, final int port, final Authenticator authenticator, final File attachmentStorage) {
		super(host, port == -1 ? DEFAULT_SSL_PORT : port, authenticator, attachmentStorage);
	}

	@Override
	protected Properties createSessionProperties() {
		final Properties props = super.createSessionProperties();
		props.setProperty(MAIL_POP3_SOCKET_FACTORY_PORT, String.valueOf(getPort()));
		props.setProperty(MAIL_POP3_SOCKET_FACTORY_CLASS, "javax.net.ssl.SSLSocketFactory");
		props.setProperty(MAIL_POP3_SOCKET_FACTORY_FALLBACK, StringPool.FALSE);
		return props;
	}

	/**
	 * Returns email store.
	 *
	 * @param session {@link Session}
	 * @return {@link com.sun.mail.pop3.POP3SSLStore}
	 */
	@Override
	protected POP3SSLStore getStore(final Session session) {
		final SimpleAuthenticator simpleAuthenticator = (SimpleAuthenticator) getAuthenticator();
		final URLName url;

		if (simpleAuthenticator == null) {
			url = new URLName(
				PROTOCOL_POP3,
				getHost(), getPort(),
				StringPool.EMPTY,
				null, null);
		}
		else {
			final PasswordAuthentication pa = simpleAuthenticator.getPasswordAuthentication();
			url = new URLName(
				PROTOCOL_POP3,
				getHost(), getPort(),
				StringPool.EMPTY,
				pa.getUserName(), pa.getPassword());
		}

		return new POP3SSLStore(session, url);
	}

}