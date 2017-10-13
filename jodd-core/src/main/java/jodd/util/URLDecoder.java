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

package jodd.util;

import jodd.core.JoddCore;

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
		return decode(url, JoddCore.encoding, false);
	}

	/**
	 * Decodes URL elements. This method may be used for all
	 * parts of URL, except for the query parts, since it does
	 * not decode the '+' character.
	 * @see #decodeQuery(String, String)
	 */
	public static String decode(String source, String encoding) {
		return decode(source, encoding, false);
	}

	/**
	 * Decodes query name or value.
	 */
	public static String decodeQuery(String source) {
		return decode(source, JoddCore.encoding, true);
	}

	/**
	 * Decodes query name or value.
	 */
	public static String decodeQuery(String source, String encoding) {
		return decode(source, encoding, true);
	}

	private static String decode(String source, String encoding, boolean decodePlus) {
		int length = source.length();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);

		boolean changed = false;

		for (int i = 0; i < length; i++) {
			int ch = source.charAt(i);
			switch (ch) {
				case '%':
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
					break;

				case '+':
					if (decodePlus) {
						ch = ' ';
						changed = true;
					}

				default:
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