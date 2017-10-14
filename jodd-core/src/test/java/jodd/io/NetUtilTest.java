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

import jodd.util.SystemUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * test class for {@link NetUtil}
 */
public class NetUtilTest {

    private static URL joddInfoTxt;

    @BeforeAll
    static void beforeTest() {
        joddInfoTxt = NetUtilTest.class.getResource("/jodd/io/jodd-info.txt");
    }

    @Test
    void testDownloadString() throws IOException {
        final String expected = "Jodd - The Unbearable Lightness of Java - üäößÜÄÖ";

        final String actual = NetUtil.downloadString(joddInfoTxt.toExternalForm());

        // Asserts
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testDownloadStringWithUTF8Encoding() throws IOException {
        final String expected = "Jodd - The Unbearable Lightness of Java - üäößÜÄÖ";

        final String actual = NetUtil.downloadString(joddInfoTxt.toExternalForm(), "UTF-8");

        // Asserts
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testDownloadStringWithISO88591Encoding() throws IOException {
        // due to ISO-8859-1 encoding
        final String expected = "Jodd - The Unbearable Lightness of Java - Ã¼Ã¤Ã¶Ã\u009FÃ\u009CÃ\u0084Ã\u0096";

        final String actual = NetUtil.downloadString(joddInfoTxt.toExternalForm(), "ISO-8859-1");

        // Asserts
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testDownloadBytes() throws IOException {
        final byte[] expected = new byte[] {74,111,100,100,32,45,32,84,104,101,32,85,110,98,101,97,114,97,98,108,101,32,
                76,105,103,104,116,110,101,115,115,32,111,102,32,74,97,118,97,32,45,32,-61,-68,-61,-92,-61,-74,-61,-97,-61,-100,-61,-124,-61,-106};
        final byte[] actual = NetUtil.downloadBytes(joddInfoTxt.toExternalForm());

        // Asserts
        Assertions.assertNotNull(actual);
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    void testDownloadFile() throws IOException {
        final byte[] expected = new byte[] {74,111,100,100,32,45,32,84,104,101,32,85,110,98,101,97,114,97,98,108,101,32,
                76,105,103,104,116,110,101,115,115,32,111,102,32,74,97,118,97,32,45,32,-61,-68,-61,-92,-61,-74,-61,-97,-61,-100,-61,-124,-61,-106};

        final File targetFile = new File(SystemUtil.tempDir(), "jodd-info.txt");
        if (targetFile.exists()) {
            Assertions.assertTrue(targetFile.delete());
        }

        NetUtil.downloadFile(joddInfoTxt.toExternalForm(), targetFile);

        // Asserts - Is copied target file equals to known file jodd-info.txt?
        Assertions.assertArrayEquals(expected, NetUtil.downloadBytes((targetFile.toURI().toURL().toExternalForm())));
    }

}