package jodd.http.net;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

public class TrustManagers {
	/**
	 * Array of trust managers that allow all certificates, done in Java8 proper-way.
	 */
	public static TrustManager[] TRUST_ALL_CERTS = new TrustManager[]{
		new X509ExtendedTrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
			}
			@Override
			public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
			}
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			@Override
			public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
			}
			@Override
			public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
			}
			@Override
			public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
			}
			@Override
			public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
			}
		}
	};
}
