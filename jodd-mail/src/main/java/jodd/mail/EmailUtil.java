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

package jodd.mail;

import jodd.util.CharUtil;
import jodd.util.StringPool;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import java.io.UnsupportedEncodingException;

/**
 * Email utilities.
 */
public class EmailUtil {

	protected static final String ATTR_CHARSET = "charset=";

	/**
	 * Extracts mime type from parts content type.
	 */
	public static String extractMimeType(String contentType) {
		int ndx = contentType.indexOf(';');
		String mime;
		if (ndx != -1) {
			mime = contentType.substring(0, ndx);
		}
		else {
			mime = contentType;
		}
		return mime;
	}

	/**
	 * Parses content type for encoding. May return <code>null</code>
	 * if encoding is not specified in content type.
	 */
	public static String extractEncoding(String contentType) {
		int ndx = contentType.indexOf(';');
		String charset = ndx != -1 ? contentType.substring(ndx + 1) : StringPool.EMPTY;
		String encoding = null;

		ndx = charset.indexOf(ATTR_CHARSET);
		if (ndx != -1) {
			ndx += ATTR_CHARSET.length();
			int len = charset.length();

			if (charset.charAt(ndx) == '"') {
				ndx++;
			}
			int start = ndx;

			while (ndx < len) {
				char c = charset.charAt(ndx);
				if ((c == '"') || (CharUtil.isWhitespace(c)) || (c == ';')) {
					break;
				}
				ndx++;
			}
			encoding = charset.substring(start, ndx);
		}
		return encoding;
	}

	/**
	 * Correctly resolves file name from the message part.
	 * Thanx to: Flavio Pompermaier
	 */
	public static String resolveFileName(Part part) throws MessagingException, UnsupportedEncodingException {
		if (!(part instanceof MimeBodyPart)) {
			return part.getFileName();
		}

		String contentType = part.getContentType();
		String ret = null;

		try {
			ret = javax.mail.internet.MimeUtility.decodeText(part.getFileName());
		}
		catch (Exception ex) {
			String[] contentId = part.getHeader("Content-ID");
			if (contentId != null && contentId.length > 0) {
				ret = contentId[0];
			}
			if (contentId == null) {
				ret = "no-name";
			}
			ret += StringPool.DOT  + contentType.substring(contentType.lastIndexOf("/") + 1, contentType.length());
		}

		return ret;
	}

	/**
	 * Setups the system email properties.
	 */
	public static void setupSystemMail() {
		System.setProperty("mail.mime.encodefilename", Boolean.valueOf(JoddMail.defaults().isMailMimeEncodefilename()).toString());
		System.setProperty("mail.mime.decodefilename", Boolean.valueOf(JoddMail.defaults().isMailMimeDecodefilename()).toString());
	}

}