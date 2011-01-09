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

	/**
	 * Resolves hostname and returns ip address as a string.
	 */
	public static String resolveHost(String hostname) {
		try {
			InetAddress addr = Inet4Address.getByName(hostname);
			byte[] ipAddr = addr.getAddress();
			StringBuilder ipAddrStr = new StringBuilder(15);
			for (int i = 0; i < ipAddr.length; i++) {
				if (i > 0) {
					ipAddrStr.append('.');
				}
				ipAddrStr.append(ipAddr[i] & 0xFF);
			}
			return ipAddrStr.toString();
		} catch (UnknownHostException uhex) {
			return null;
		}
	}

	/**
	 * Resolves string ip address and returns host name as a string.
	 */
	public static String resolveIp(String ip) {
		try {
			InetAddress addr = InetAddress.getByName(ip);
			return addr.getHostName();
		} catch (UnknownHostException uhex) {
			return null;
		}
	}

	/**
	 * Resolves ip address and returns host name as a string.
	 */
	public static String resolveIp(byte[] ip) {
		try {
			InetAddress addr = InetAddress.getByAddress(ip);
			return addr.getHostName();
		} catch (UnknownHostException uhex) {
			return null;
		}
	}

	// ---------------------------------------------------------------- download
	
	/**
	 * Downloads resource as byte array.
	 */
	public static byte[] downloadBytes(String url) throws IOException {
		HttpURLConnection conection = (HttpURLConnection)((new URL(url).openConnection()));
		return StreamUtil.readBytes(conection.getInputStream());
	}

	/**
	 * Downloads resource as String.
	 */
	public static String downloadString(String url, String encoding) throws IOException {
		HttpURLConnection conection = (HttpURLConnection)((new URL(url).openConnection()));
		return new String(StreamUtil.readChars(conection.getInputStream(), encoding));
	}

	/**
	 * Downloads resource as String.
	 */
	public static String downloadString(String url) throws IOException {
		HttpURLConnection conection = (HttpURLConnection)((new URL(url).openConnection()));
		return new String(StreamUtil.readChars(conection.getInputStream()));
	}
}