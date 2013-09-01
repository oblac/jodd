// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.JoddCore;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * URL decoder.
 */
public class URLDecoder {

	/**
	 * Decodes URL elements.
	 */
	public static String decode(String url) {
		return decode(url, JoddCore.encoding);
	}

	/**
	 * Decodes URL elements.
	 */
	public static String decode(String source, String encoding) {
		int length = source.length();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);

		boolean changed = false;

		for (int i = 0; i < length; i++) {
			int ch = source.charAt(i);
			if (ch == '%') {
				if ((i + 2) < length) {
					char hex1 = source.charAt(i + 1);
					char hex2 = source.charAt(i + 2);
					int u = Character.digit(hex1, 16);
					int l = Character.digit(hex2, 16);
					if (u == -1 || l == -1) {
						throw new IllegalArgumentException("Invalid sequence: " + source.substring(i));
					}
					bos.write((char) ((u << 4) + l));
					i += 2;
					changed = true;
				} else {
					throw new IllegalArgumentException("Invalid sequence: " + source.substring(i));
				}
			} else {
				bos.write(ch);
			}
		}
		try {
			return changed ? new String(bos.toByteArray(), encoding) : source;
		} catch (UnsupportedEncodingException ignore) {
			return null;
		}
	}

}