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

import java.io.*;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class StreamUtilTest {

	private String dataRoot;
	private File textFile;

	@BeforeEach
	public void setUp() throws Exception {
		if (dataRoot != null) {
			return;
		}
		URL data = ClassLoaderUtil.getResourceUrl("jodd/io/data");
		dataRoot = data.getFile();
		textFile = new File(dataRoot, "file/a.txt");
	}

	@Test
	public void testCopy() throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream("input".getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamUtil.copy(in, out);
		assertEquals("input", out.toString());
		StreamUtil.close(out);
		StreamUtil.close(in);
	}

	@Test
	public void testCopyWithSize() throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream("input".getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamUtil.copy(in, out, 3);

        assertEquals("inp", out.toString());
		StreamUtil.close(out);
		StreamUtil.close(in);

		in = new ByteArrayInputStream("input".getBytes());
		out = new ByteArrayOutputStream();
        StreamUtil.copy(in, out, 5);
		assertEquals("input", out.toString());
		StreamUtil.close(out);
		StreamUtil.close(in);

		int temp = JoddCore.ioBufferSize;

		JoddCore.ioBufferSize = 3;
		in = new ByteArrayInputStream("input".getBytes());
		out = new ByteArrayOutputStream();
        StreamUtil.copy(in, out, 5);
		assertEquals("input", out.toString());
		StreamUtil.close(out);
		StreamUtil.close(in);

		JoddCore.ioBufferSize = temp;
	}


	@Test
	public void testCompare() throws Exception {
        File file = textFile;
        FileInputStream in1 = new FileInputStream(file);

        String content = "test file\r\n";
        if (file.length() == 10) {
            content = StringUtil.remove(content, '\r');
        }
        ByteArrayInputStream in2 = new ByteArrayInputStream(content.getBytes());
        assertTrue(StreamUtil.compare(in1, in2));
        StreamUtil.close(in2);
        StreamUtil.close(in1);
	}

	@Test
	public void testGetBytes() throws Exception {
        FileInputStream in = new FileInputStream(textFile);
        byte[] data = StreamUtil.readBytes(in);
        StreamUtil.close(in);

        String s = new String(data);
        s = StringUtil.remove(s, '\r');
        assertEquals("test file\n", s);

        in = new FileInputStream(textFile);
        String str = new String(StreamUtil.readChars(in));
        StreamUtil.close(in);
        str = StringUtil.remove(str, '\r');
        assertEquals("test file\n", str);
	}

    @Test
    public void testCompareWithReaderInstances_ExpectedSuccessfulCompare() throws Exception {

        boolean actual;
        try (FileReader input_1 = new FileReader(textFile); FileReader input_2 = new FileReader(textFile)) {
            actual = StreamUtil.compare(input_1, input_2);
        }

        // asserts
        assertTrue(actual);
    }

    @Test
    public void testCompareWithReaderInstances_ExpectedNotSuccessfulCompare() throws Exception {

        boolean actual;

        try (FileReader input_1 = new FileReader(textFile); CharArrayReader input_2 = new CharArrayReader(new char[] {'t','e','s','t',' ','f','i','l','e','!'})) {
            actual = StreamUtil.compare(input_1, input_2);
        }

        // asserts
        assertFalse(actual);
    }

}
