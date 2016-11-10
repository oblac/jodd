package jodd.http.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * Add a certificate to the cacerts keystore if it's not already included
 */
public class SSLUtil {

	private static final String CACERTS_PATH = "/jre/lib/security/cacerts";
	private static final String CACERTS_PASSWORD = "changeit";

	/**
	 * Add a certificate to the cacerts keystore if it's not already included.
	 *
	 * @param alias    The alias for the certificate, if added
	 * @param certFile The certificate file stream
	 */

	public static void ensureSslCertIsInKeystore(final String alias, final String certFile)
			throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
		ensureSslCertIsInKeystore(CACERTS_PASSWORD, alias, certFile);
	}

	public static void ensureSslCertIsInKeystore(final String cacertsPassword, final String alias, final String certFile)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{

		// get default cacerts file
		final File cacertsFile = new File(System.getProperty("java.home") + CACERTS_PATH);

		if (!cacertsFile.exists()) {
			throw new FileNotFoundException(cacertsFile.getAbsolutePath());
		}

		// load cacerts keystore
		FileInputStream cacertsIs = new FileInputStream(cacertsFile);
		final KeyStore cacerts = KeyStore.getInstance(KeyStore.getDefaultType());
		cacerts.load(cacertsIs, cacertsPassword.toCharArray());
		cacertsIs.close();

		//load certificate from input stream
		final CertificateFactory cf = CertificateFactory.getInstance("X.509");

		final InputStream certInputStream = new FileInputStream(certFile);
		final Certificate cert = cf.generateCertificate(certInputStream);
		certInputStream.close();

		// check if cacerts contains the certificate
		if (cacerts.getCertificateAlias(cert) == null) {

			// cacerts doesn't contain the certificate, add it
			cacerts.setCertificateEntry(alias, cert);

			// write the updated cacerts keystore
			FileOutputStream cacertsOs = new FileOutputStream(cacertsFile);
			cacerts.store(cacertsOs, CACERTS_PASSWORD.toCharArray());
			cacertsOs.close();
		}
	}
}
