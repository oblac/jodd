//  Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.util.CharUtil;
import jodd.util.StringPool;

import javax.mail.Address;

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
		} else {
			mime = contentType;
		}
		return mime;
	}

	/**
	 * Parses content type for encoding.
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
				if ((c == '"') || (CharUtil.isWhitespace(c) == true) || (c == ';')) {
					break;
				}
				ndx++;
			}
			encoding = charset.substring(start, ndx);
		}
		return encoding;
	}

	/**
	 * Converts mail address to strings.
	 */
	public static String[] address2String(Address[] addresses) {
		if (addresses == null) {
			return null;
		}
		if (addresses.length == 0) {
			return null;
		}
		String[] res = new String[addresses.length];
		for (int i = 0; i < addresses.length; i++) {
			Address address = addresses[i];
			res[i] = address.toString();
		}
		return res;
	}

}