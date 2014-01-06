// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.filter;

import org.junit.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CharArrayResponseWrapperTest {

	@Test
	public void testWriter() {
		HttpServletResponse response = mock(HttpServletResponse.class);

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

	@Test
	public void testBytes() throws IOException {
		HttpServletResponseWrapper rw = mock(HttpServletResponseWrapper.class);
		ServletOutputStream os = mock(ServletOutputStream.class);
		when(rw.getOutputStream()).thenReturn(os);

		CharArrayResponseWrapper wrappedResponse = new CharArrayResponseWrapper(rw);
		ServletOutputStream sos = wrappedResponse.getOutputStream();

		sos.write(new byte[]{123, 123});

		char[] chars = wrappedResponse.toCharArray();
		assertNull(chars);
	}

}
