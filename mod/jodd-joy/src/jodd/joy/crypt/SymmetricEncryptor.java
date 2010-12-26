package jodd.joy.crypt;

import jodd.joy.exception.AppException;
import jodd.util.Base64;
import jodd.util.StringUtil;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import static jodd.util.StringPool.UTF_8;

/**
 * Simple symmetric de/encryptor that uses PBE With MD5 And Triple DES.
 */
public class SymmetricEncryptor {

	protected final Cipher ecipher;
	protected final Cipher dcipher;
	protected final int iterationCount;

	protected static final byte[] defaultSalt = {
			(byte) 0xBA, (byte) 0xC7, (byte) 0x17, (byte) 0x31,
			(byte) 0xBE, (byte) 0x7E, (byte) 0x73, (byte) 0xFF
	};

	private static final String ALGORITHM = "PBEWithMD5AndTripleDES";

	public SymmetricEncryptor(String passPhrase) {
		this(passPhrase, defaultSalt, 19);
	}

	public SymmetricEncryptor(String passPhrase, byte[] salt, int iterationCount) {
		this.iterationCount = iterationCount;
		try {
			// create the key
			KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
			SecretKey key = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(keySpec);
			ecipher = Cipher.getInstance(key.getAlgorithm());
			dcipher = Cipher.getInstance(key.getAlgorithm());

			// prepare the parameter to the ciphers
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

			// create the ciphers
			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		} catch (Exception ex) {
			throw new AppException(ex);
		}
	}

	/**
	 * Symmetrically encrypts the string.
	 */
	public String encrypt(String str) {
		try {
			byte[] utf8 = str.getBytes(UTF_8);		// encode the string into bytes using utf-8
			byte[] enc = ecipher.doFinal(utf8); 	// encrypt
			return Base64.encodeToString(enc);		// encode bytes to base64 to get a string
		} catch (Throwable ignore) {
			return null;
		}
	}

	/**
	 * Symmetrically decrypts the string.
	 */
	public String decrypt(String str) {
		try {
			str = StringUtil.replaceChar(str, ' ', '+');	// replace spaces with chars.
			byte[] dec = Base64.decode(str);    	// decode base64 to get bytes
			byte[] utf8 = dcipher.doFinal(dec);     // decrypt
			return new String(utf8, UTF_8);			// decode using utf-8
		} catch (Throwable ignore) {
			return null;
		}
	}

}
