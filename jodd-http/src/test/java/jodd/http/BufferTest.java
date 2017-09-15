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

package jodd.http;

import jodd.http.up.Uploadable;
import jodd.util.StringPool;
import jodd.util.StringUtil;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BufferTest {

	public class SimpleUploadable implements Uploadable {

		final int size;
		final byte[] bytes;

		public SimpleUploadable(int size) {
			this.size = size;
			bytes = new byte[size];

			Arrays.fill(bytes, (byte) '*');
		}
		public SimpleUploadable(int size, char c) {
			this.size = size;
			bytes = new byte[size];

			Arrays.fill(bytes, (byte) c);
		}

		public Object getContent() {
			return bytes;
		}

		public byte[] getBytes() {
			return bytes;
		}

		public String getFileName() {
			return null;
		}

		public String getMimeType() {
			return null;
		}

		public int getSize() {
			return size;
		}

		public InputStream openInputStream() throws IOException {
			return new ByteArrayInputStream(bytes);
		}
	}

	public class SimpleProgressListener extends HttpProgressListener {

		StringBuilder sb = new StringBuilder();

		@Override
		public int callbackSize(int size) {
			return 10;
		}

		@Override
		public void transferred(int len) {
			if (sb.length() > 0) {
				sb.append(':');
			}
			sb.append(len);
		}
	}

	@Test
	public void testBufferAppend() {
		Buffer buffer = new Buffer();

		assertEquals(0, buffer.size());
		assertEquals(0, buffer.list.size());
		assertNull(buffer.last);

		buffer.append("Hey");

		assertEquals(3, buffer.size());
		assertNotNull(buffer.last);

		buffer.append('!');
		buffer.append(91);

		assertEquals(6, buffer.size());
		assertEquals(1, buffer.list.size());

		buffer.append(new SimpleUploadable(100));

		assertEquals(106, buffer.size());
		assertEquals(2, buffer.list.size());
		assertNull(buffer.last);

		buffer.append("x");

		assertEquals(107, buffer.size());
		assertEquals(3, buffer.list.size());
		assertNotNull(buffer.last);

		Buffer buffer2 = new Buffer();
		buffer2.append(new SimpleUploadable(20));

		buffer.append(buffer2);

		assertEquals(127, buffer.size());
		assertEquals(4, buffer.list.size());
		assertNull(buffer.last);
	}

	@Test
	public void testBufferWrite1() throws IOException {
		Buffer buffer;
		ByteArrayOutputStream baos;
		SimpleProgressListener hpl;

		// size < callbackSize

		buffer = new Buffer();
		buffer.append("12345");

		baos = new ByteArrayOutputStream();
		hpl = new SimpleProgressListener();
		buffer.writeTo(baos, hpl);

		assertEquals("12345", baos.toString(StringPool.ISO_8859_1));
		assertEquals("0:5", hpl.sb.toString());

		// size = callbackSize

		buffer = new Buffer();
		buffer.append("1234567890");

		baos = new ByteArrayOutputStream();
		hpl = new SimpleProgressListener();
		buffer.writeTo(baos, hpl);

		assertEquals("1234567890", baos.toString(StringPool.ISO_8859_1));
		assertEquals("0:10", hpl.sb.toString());

		// size > callbackSize

		buffer = new Buffer();
		buffer.append("1234567890ABC");

		baos = new ByteArrayOutputStream();
		hpl = new SimpleProgressListener();
		buffer.writeTo(baos, hpl);

		assertEquals("1234567890ABC", baos.toString(StringPool.ISO_8859_1));
		assertEquals("0:10:13", hpl.sb.toString());
	}

	@Test
	public void testBufferWrite2() throws IOException {
		Buffer buffer;
		ByteArrayOutputStream baos;
		SimpleProgressListener hpl;

		// size > callbackSize

		buffer = new Buffer();
		buffer.append("12345");
		buffer.append(new SimpleUploadable(10));
		buffer.append("67");
		assertEquals(17, buffer.size());

		baos = new ByteArrayOutputStream();
		hpl = new SimpleProgressListener();
		buffer.writeTo(baos, hpl);

		assertEquals("12345**********67", baos.toString(StringPool.ISO_8859_1));
		assertEquals("0:10:17", hpl.sb.toString());

		// size = callbackSize

		buffer = new Buffer();
		buffer.append("12345");
		buffer.append(new SimpleUploadable(5));
		assertEquals(10, buffer.size());

		baos = new ByteArrayOutputStream();
		hpl = new SimpleProgressListener();
		buffer.writeTo(baos, hpl);

		assertEquals("12345*****", baos.toString(StringPool.ISO_8859_1));
		assertEquals("0:10", hpl.sb.toString());

		// size > callbackSize

		buffer = new Buffer();
		buffer.append("12345");
		buffer.append(new SimpleUploadable(21));
		buffer.append("X");
		assertEquals(27, buffer.size());

		baos = new ByteArrayOutputStream();
		hpl = new SimpleProgressListener();
		buffer.writeTo(baos, hpl);

		assertEquals("12345*********************X", baos.toString(StringPool.ISO_8859_1));
		assertEquals("0:10:20:27", hpl.sb.toString());
	}

	@Test
	public void testBufferWrite3() throws IOException {
		Buffer buffer;
		ByteArrayOutputStream baos;
		SimpleProgressListener hpl;

		//

		buffer = new Buffer();
		buffer.append(new SimpleUploadable(4, '*'));
		buffer.append(new SimpleUploadable(4, '+'));
		buffer.append(new SimpleUploadable(4, '-'));
		assertEquals(12, buffer.size());

		baos = new ByteArrayOutputStream();
		hpl = new SimpleProgressListener();
		buffer.writeTo(baos, hpl);

		assertEquals("****++++----", baos.toString(StringPool.ISO_8859_1));
		assertEquals("0:10:12", hpl.sb.toString());

		//

		buffer.append("12345678");
		assertEquals(20, buffer.size());

		baos = new ByteArrayOutputStream();
		hpl = new SimpleProgressListener();
		buffer.writeTo(baos, hpl);

		assertEquals("****++++----12345678", baos.toString(StringPool.ISO_8859_1));
		assertEquals("0:10:20", hpl.sb.toString());

		//

		buffer.append("A");
		assertEquals(21, buffer.size());

		baos = new ByteArrayOutputStream();
		hpl = new SimpleProgressListener();
		buffer.writeTo(baos, hpl);

		assertEquals("****++++----12345678A", baos.toString(StringPool.ISO_8859_1));
		assertEquals("0:10:20:21", hpl.sb.toString());

		//

		buffer.append(new SimpleUploadable(30, '#'));
		assertEquals(51, buffer.size());

		baos = new ByteArrayOutputStream();
		hpl = new SimpleProgressListener();
		buffer.writeTo(baos, hpl);

		assertEquals("****++++----12345678A" + StringUtil.repeat('#', 30), baos.toString(StringPool.ISO_8859_1));
		assertEquals("0:10:20:30:40:50:51", hpl.sb.toString());

	}

}
