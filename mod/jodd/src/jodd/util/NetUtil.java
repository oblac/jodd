// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.io.StreamUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Network utilities.
 */
public class NetUtil {

	public static final String LOCAL_HOST = "localhost";
	public static final String LOCAL_IP = "127.0.0.1";
	public static final String DEFAULT_MASK = "255.255.255.0";
	public static final int INT_VALUE_127_0_0_1 = 0x7f000001;


	/**
	 * Resolves IP address from a hostname.
	 */
	public static String resolveIpAddress(String hostname) {
		try {
			InetAddress netAddress;

			if (hostname == null || hostname.equalsIgnoreCase(LOCAL_HOST)) {
				netAddress = InetAddress.getLocalHost();
			} else {
				netAddress = Inet4Address.getByName(hostname);
			}
			return netAddress.getHostAddress();
		} catch (UnknownHostException ignore) {
			return null;
		}
	}

	/**
	 * Returns IP address as integer.
	 */
	public static int getIpAsInt(String ipAddress) {
		int ipIntValue = 0;
		String tokens[] = StringUtil.splitc(ipAddress, '.');
		for (String token : tokens) {
			if (ipIntValue > 0) {
				ipIntValue <<= 8;
			}
			ipIntValue += Integer.parseInt(token);
		}
		return ipIntValue;
	}

	public static int getMaskAsInt(String mask) {
		if (!validateHostIp(mask)) {
			mask = DEFAULT_MASK;
		}
		return getIpAsInt(mask);
	}

	public static boolean isSocketAccessAllowed(int localIp, int socketIp, int mask) {
		boolean _retVal = false;

		if (socketIp == INT_VALUE_127_0_0_1 || (localIp & mask) == (socketIp & mask)) {
			_retVal = true;
		}
		return _retVal;
	}

	/**
	 * Validates IP address given as a string.
	 */
	public static boolean validateHostIp(String host) {
		boolean retVal = false;
		if (host == null) {
			return retVal;
		}

		int hitDots = 0;
		char data[] = host.toCharArray();
		for (int i = 0; i < data.length; i++) {
			char c = data[i];
			int b = 0;
			do {
				if (c < '0' || c > '9') {
					return false;
				}
				b = (b * 10 + c) - 48;
				if (++i >= data.length) {
					break;
				}
				c = data[i];
			} while (c != '.');

			if (b > 255) {
				return false;
			}
			hitDots++;
		}

		if (hitDots != 4) {
			return false;
		}
		return true;
	}

	/**
	 * Resolves host name from IP address bytes.
	 */
	public static String resolveHostName(byte[] ip) {
		try {
			InetAddress address = InetAddress.getByAddress(ip);
			return address.getHostName();
		} catch (UnknownHostException ignore) {
			return null;
		}
	}

	// ---------------------------------------------------------------- download

	/**
	 * Downloads resource as byte array.
	 */
	public static byte[] downloadBytes(String url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) ((new URL(url).openConnection()));
		return StreamUtil.readBytes(connection.getInputStream());
	}

	/**
	 * Downloads resource as String.
	 */
	public static String downloadString(String url, String encoding) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) ((new URL(url).openConnection()));
		return new String(StreamUtil.readChars(connection.getInputStream(), encoding));
	}

	/**
	 * Downloads resource as String.
	 */
	public static String downloadString(String url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) ((new URL(url).openConnection()));
		return new String(StreamUtil.readChars(connection.getInputStream()));
	}
}