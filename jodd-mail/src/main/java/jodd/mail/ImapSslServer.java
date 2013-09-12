package jodd.mail;

import com.sun.mail.imap.IMAPSSLStore;
import jodd.util.StringPool;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import java.util.Properties;

/**
 * IMAP SSL Server.
 */
public class ImapSslServer extends ImapServer {

	protected static final String MAIL_IMAP_SOCKET_FACTORY_PORT = "mail.imap.socketFactory.port";
	protected static final String MAIL_IMAP_SOCKET_FACTORY_CLASS = "mail.imap.socketFactory.class";
	protected static final String MAIL_IMAP_SOCKET_FACTORY_FALLBACK = "mail.imap.socketFactory.fallback";
	protected static final int DEFAULT_SSL_PORT = 993;

	protected final String username;
	protected final String password;

	public ImapSslServer(String host, String username, String password) {
		this(host, DEFAULT_SSL_PORT, username, password);
	}

	public ImapSslServer(String host, int port, String username, String password) {
		super(host, port, username, password);
		this.username = username;
		this.password = password;
	}


	@Override
	protected Properties createSessionProperties() {
		Properties props = new Properties();
		props.setProperty(MAIL_IMAP_PORT, String.valueOf(port));
		props.setProperty(MAIL_IMAP_SOCKET_FACTORY_PORT, String.valueOf(port));
		props.setProperty(MAIL_IMAP_SOCKET_FACTORY_CLASS, "javax.net.ssl.SSLSocketFactory");
		props.setProperty(MAIL_IMAP_SOCKET_FACTORY_FALLBACK, StringPool.FALSE);
		return props;
	}

	@Override
	protected Store getStore(Session session) throws NoSuchProviderException {
		URLName url = new URLName(PROTOCOL_IMAP, host, port, "", username, password);
		return new IMAPSSLStore(session, url);
	}
}