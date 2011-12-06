// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.filter;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

public class CharArrayResponseWrapperTest extends TestCase {

	public void testWriter() {
		HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);

		CharArrayResponseWrapper wrappedResponse = new CharArrayResponseWrapper(response);

		PrintWriter printWriter = wrappedResponse.getWriter();
		printWriter.write("One");
		printWriter.write("Two");
		printWriter.flush();

		char[] chars = wrappedResponse.toCharArray();
		String string = new String(chars);
		assertEquals("OneTwo", string);

		printWriter.write("123");
		printWriter.flush();

		chars = wrappedResponse.toCharArray();
		string = new String(chars);
		assertEquals("OneTwo123", string);

	}

	public void testBytes() throws IOException {
		HttpServletResponseWrapper rw = EasyMock.createMock(HttpServletResponseWrapper.class);
		ServletOutputStream os = EasyMock.createNiceMock(ServletOutputStream.class);
		EasyMock.expect(rw.getOutputStream()).andReturn(os);
		EasyMock.replay(rw, os);

		CharArrayResponseWrapper wrappedResponse = new CharArrayResponseWrapper(rw);
		ServletOutputStream sos = wrappedResponse.getOutputStream();

		sos.write(new byte[] {123, 123});

		char[] chars = wrappedResponse.toCharArray();
		assertNull(chars);
	}

}
