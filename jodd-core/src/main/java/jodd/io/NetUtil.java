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

package jodd.io;

import jodd.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
		String[] tokens = StringUtil.splitc(ipAddress, '.');
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
		if (host == null) {
			return false;
		}

		int hitDots = 0;
		char[] data = host.toCharArray();
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

		return hitDots == 4;
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
		try (InputStream inputStream = new URL(url).openStream()) {
			return StreamUtil.readBytes(inputStream);
		}
	}

	/**
	 * Downloads resource as String.
	 */
	public static String downloadString(String url, String encoding) throws IOException {
		try (InputStream inputStream = new URL(url).openStream()) {
			return new String(StreamUtil.readChars(inputStream, encoding));
		}
	}

	/**
	 * Downloads resource as String.
	 */
	public static String downloadString(String url) throws IOException {
		try (InputStream inputStream = new URL(url).openStream()) {
			return new String(StreamUtil.readChars(inputStream));
		}
	}

	/**
	 * Downloads resource to a file, potentially very efficiently.
	 */
	public static void downloadFile(String url, File file) throws IOException {
		try (
			InputStream inputStream = new URL(url).openStream();
			ReadableByteChannel rbc = Channels.newChannel(inputStream);
			FileChannel fileChannel = FileChannel.open(
				file.toPath(),
				StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.WRITE)
		) {
			fileChannel.transferFrom(rbc, 0, Long.MAX_VALUE);
		}
	}
}