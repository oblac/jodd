// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.idgen;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * UUID generator of 32 bytes long values. It is builded from:
 * <ol>
 * <li> (0-7) IPAddress as HEX - 8 bytes
 * <li> (8-19) CurrentTimeMillis() as HEX - Display all 12 bytes
 * <li> (20-23) SecureRandom() as HEX - Keep only 4 significant bytes. Since this is "random" it doesn't really matter how many bytes you keep or eliminate
 * <li> (24-31) System.identityHashCode as Hex - 8 bytes
 * </ol>
 */
public class Uuid32Generator {

	public static String generateUUID() {
		return new Uuid32Generator().generate();
	}

	private static final String ZEROS = "000000000000"; // 12

	public String generate() {
		StringBuilder strRetVal = new StringBuilder();
		String strTemp;
		try {
			// IPAddress segment
			InetAddress addr = InetAddress.getLocalHost();
			byte[] ipaddr = addr.getAddress();
			for (byte anIpaddr : ipaddr) {
				Byte b = Byte.valueOf(anIpaddr);
				strTemp = Integer.toHexString(b.intValue() & 0x000000ff);
				strRetVal.append(ZEROS.substring(0, 2 - strTemp.length()));
				strRetVal.append(strTemp);
			}
			strRetVal.append(':');

			// CurrentTimeMillis() segment
			strTemp = Long.toHexString(System.currentTimeMillis());
			strRetVal.append(ZEROS.substring(0, 12 - strTemp.length()));
			strRetVal.append(strTemp).append(':');

			// random segment
			SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
			strTemp = Integer.toHexString(prng.nextInt());
			while (strTemp.length() < 8) {
				strTemp = '0' + strTemp;
			}
			strRetVal.append(strTemp.substring(4)).append(':');

			// IdentityHash() segment
			strTemp = Long.toHexString(System.identityHashCode(this));
			strRetVal.append(ZEROS.substring(0, 8 - strTemp.length()));
			strRetVal.append(strTemp);
		} catch (UnknownHostException uhex) {
			throw new RuntimeException("Unknown host.", uhex);
		} catch (NoSuchAlgorithmException nsaex) {
			throw new RuntimeException("Algorithm 'SHA1PRNG' is unavailiable.", nsaex);
		}
		return strRetVal.toString().toUpperCase();
	}

}
