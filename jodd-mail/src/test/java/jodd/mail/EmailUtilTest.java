//  Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class EmailUtilTest {

	@Test
	public void testExtractContentType() {
		String contentType = "multipart/mixed;";
		assertEquals("multipart/mixed", EmailUtil.extractMimeType(contentType));
		assertNull(EmailUtil.extractEncoding(contentType));

		contentType = "multipart/mixed; boundary=-----";
		assertEquals("multipart/mixed", EmailUtil.extractMimeType(contentType));
		assertNull(EmailUtil.extractEncoding(contentType));

		contentType = "text/html;\n\tcharset=\"us-ascii\"";
		assertEquals("text/html", EmailUtil.extractMimeType(contentType));
		assertEquals("us-ascii", EmailUtil.extractEncoding(contentType));

		contentType = "TEXT/PLAIN; charset=US-ASCII; name=example.eml";
		assertEquals("TEXT/PLAIN", EmailUtil.extractMimeType(contentType));
		assertEquals("US-ASCII", EmailUtil.extractEncoding(contentType));
	}
}