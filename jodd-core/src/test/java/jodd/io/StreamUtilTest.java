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

import jodd.core.JoddCore;
import jodd.util.ClassLoaderUtil;
import jodd.util.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class StreamUtilTest {

	protected String dataRoot;

	@BeforeEach
	public void setUp() throws Exception {
		if (dataRoot != null) {
			return;
		}
		URL data = ClassLoaderUtil.getResourceUrl("jodd/io/data");
		dataRoot = data.getFile();
	}

	@Test
	public void testCopy() {
		ByteArrayInputStream in = new ByteArrayInputStream("input".getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			StreamUtil.copy(in, out);
		} catch (IOException ioex) {
			fail("StreamUtil.copy " + ioex.toString());
		}
		assertEquals("input", out.toString());
		StreamUtil.close(out);
		StreamUtil.close(in);
	}

	@Test
	public void testCopyWithSize() {
		ByteArrayInputStream in = new ByteArrayInputStream("input".getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			StreamUtil.copy(in, out, 3);
		} catch (IOException ioex) {
			fail("StreamUtil.copy " + ioex.toString());
		}
		assertEquals("inp", out.toString());
		StreamUtil.close(out);
		StreamUtil.close(in);

		in = new ByteArrayInputStream("input".getBytes());
		out = new ByteArrayOutputStream();
		try {
			StreamUtil.copy(in, out, 5);
		} catch (IOException ioex) {
			fail("StreamUtil.copy " + ioex.toString());
		}
		assertEquals("input", out.toString());
		StreamUtil.close(out);
		StreamUtil.close(in);

		int temp = JoddCore.ioBufferSize;

		JoddCore.ioBufferSize = 3;
		in = new ByteArrayInputStream("input".getBytes());
		out = new ByteArrayOutputStream();
		try {
			StreamUtil.copy(in, out, 5);
		} catch (IOException ioex) {
			fail("StreamUtil.copy " + ioex.toString());
		}
		assertEquals("input", out.toString());
		StreamUtil.close(out);
		StreamUtil.close(in);

		JoddCore.ioBufferSize = temp;

	}


	@Test
	public void testCompare() {
		try {
			File file = new File(dataRoot, "file/a.txt");
			FileInputStream in1 = new FileInputStream(file);

			String content = "test file\r\n";
			if (file.length() == 10) {
				content = StringUtil.remove(content, '\r');
			}
			ByteArrayInputStream in2 = new ByteArrayInputStream(content.getBytes());
			assertTrue(StreamUtil.compare(in1, in2));
			StreamUtil.close(in2);
			StreamUtil.close(in1);
		} catch (FileNotFoundException e) {
			fail("StreamUtil.testCloneCompare " + e.toString());
		} catch (IOException e) {
			fail("StreamUtil.testCloneCompare " + e.toString());
		}
	}

	@Test
	public void testGetBytes() {
		try {
			FileInputStream in = new FileInputStream(new File(dataRoot, "file/a.txt"));
			byte[] data = StreamUtil.readBytes(in);
			StreamUtil.close(in);

			String s = new String(data);
			s = StringUtil.remove(s, '\r');
			assertEquals("test file\n", s);

			in = new FileInputStream(new File(dataRoot, "file/a.txt"));
			String str = new String(StreamUtil.readChars(in));
			StreamUtil.close(in);
			str = StringUtil.remove(str, '\r');
			assertEquals("test file\n", str);
		} catch (FileNotFoundException e) {
			fail("StreamUtil.testGetBytes " + e.toString());
		} catch (IOException e) {
			fail("StreamUtil.testGetBytes " + e.toString());
		}
	}
}
