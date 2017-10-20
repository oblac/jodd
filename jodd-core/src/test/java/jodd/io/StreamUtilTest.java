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

import jodd.util.MathUtil;
import jodd.util.SystemUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * test class for {@link StreamUtil} <br/>
 * <p>
 *     tests for methods may be grouped in nested classes
 * </p>
 *
 * @see Nested
 */
class StreamUtilTest {

    static final File BASE_DIR = new File(SystemUtil.tempDir(), "jodd/StreamUtilTest");

    @BeforeAll
    static void beforeAll() throws Exception {
        if (BASE_DIR.exists()) {
            // clean up all subdirs & files
            Files.walk(BASE_DIR.toPath(), FileVisitOption.FOLLOW_LINKS)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .peek(System.out::println)
                    .forEach(File::delete);
        }
        // created directory is needed for tests
        BASE_DIR.mkdirs();
    }

    @Nested
    @DisplayName("tests for StreamUtil#close - method")
    class Close {

        private class MyCloseable implements Closeable {

            boolean closed = false;
            boolean flushed = false;

            @Override
            public void close() throws IOException {
                closed = true;
            }
        }

        private class MyFlushable extends MyCloseable implements Flushable {
            @Override
            public void flush() throws IOException {
                flushed = true;
            }
        }

        @Test
        void close_with_null() {
            StreamUtil.close(null);
        }

        @Test
        void close_with_closeable_instance() {
            final MyCloseable input = new MyCloseable();

            StreamUtil.close(input);

            // asserts
            assertTrue(input.closed);
            assertFalse(input.flushed);
        }

        @Test
        void close_with_closeable_and_flushable_instance() {
            final MyFlushable input = new MyFlushable();

            StreamUtil.close(input);

            // asserts
            assertTrue(input.closed);
            assertTrue(input.flushed);
        }

    }


    @Nested
    @DisplayName("tests for StreamUtil#compare - methods")
    class Compare {

        @Test
        void testCompareWithReaderInstances_ExpectedSuccessfulCompare() throws Exception {

            final String text = new String("jodd and german umlauts öäü".getBytes(),Charset.forName("ISO-8859-1"));

            boolean actual;
            try (StringReader reader_1 = new StringReader(text); StringReader reader_2 = new StringReader(text);) {
                actual = StreamUtil.compare(reader_1, reader_2);
            }

            // asserts
            assertTrue(actual);
        }

        @Test
        void testCompareWithReaderInstances_ExpectedNoSuccessfulCompare() throws Exception {

            final String text_1 = "jodd and german umlauts öäü";
            final String text_2 = new String(text_1.getBytes(),Charset.forName("ISO-8859-1"));

            boolean actual;

            try (StringReader reader_1 = new StringReader(text_1); StringReader reader_2 = new StringReader(text_2)) {
                actual = StreamUtil.compare(reader_1, reader_2);
            }

            // asserts
            assertFalse(actual);
        }

        @Test
        void testCompareWithInputStreams_ExpectedSuccessfulCompare(TestInfo testInfo) throws Exception {

            final String text = "jodd makes fun!" + System.lineSeparator();
            final File file = new File(StreamUtilTest.BASE_DIR, testInfo.getTestMethod().get().getName() + ".txt");
            FileUtil.writeString(file, text, "UTF-8");

            boolean actual;

            try (ByteArrayInputStream in1 = new ByteArrayInputStream(text.getBytes());
                 FileInputStream in2 = new FileInputStream(file)) {
                actual = StreamUtil.compare(in1, in2);
            }

            // asserts
            assertTrue(actual);
        }

        @Test
        void testCompareWithInputStreams_ExpectedNoSuccessfulCompare(TestInfo testInfo) throws Exception {

            final String text = "jodd makes fun!";
            final File file = new File(StreamUtilTest.BASE_DIR, testInfo.getTestMethod().get().getName() + ".txt");
            FileUtil.writeString(file, " " + text, "UTF-8");

            boolean actual;

            try (ByteArrayInputStream in1 = new ByteArrayInputStream(text.getBytes());
                 FileInputStream in2 = new FileInputStream(file)) {
                actual = StreamUtil.compare(in1, in2);
            }

            // asserts
            assertFalse(actual);
        }

    }


    @Nested
    @DisplayName("tests for StreamUtil#readAvailableBytes - method")
    class ReadAvailableBytes {

        @Test
        void testReadAvailableBytes_with_null() throws Exception {
            assertThrows(NullPointerException.class, () -> {
               StreamUtil.readAvailableBytes(null);
            });
        }

        @Test
        void testReadAvailableBytes_with_inputstream_from_empty_byte_arry() throws Exception {

            final byte[] input = new byte[]{};

            final int expected_length = 0;
            final byte[] expected_array = new byte[]{};


            final byte[] actual = StreamUtil.readAvailableBytes(new ByteArrayInputStream(input));

            // asserts
            assertNotNull(actual);
            assertEquals(expected_length, actual.length);
            assertArrayEquals(expected_array, actual);
        }

        @Test
        void testReadAvailableBytes_with_inputstream() throws Exception {

            final byte[] input = "jodd".getBytes();

            final int expected_length = 4;
            final byte[] expected_array = new byte[]{106,111,100,100};

            final byte[] actual = StreamUtil.readAvailableBytes(new ByteArrayInputStream(input));

            // asserts
            assertNotNull(actual);
            assertEquals(expected_length, actual.length);
            assertArrayEquals(expected_array, actual);
        }

    }

    @Nested
    @DisplayName("tests for StreamUtil#readChars - method")
    class ReadChars {

        @Nested
        @DisplayName("tests for StreamUtil#readChars(InputStream input)")
        class ReadChars_InputStream {

            @Test
            void testReadChars_InputStream(TestInfo testInfo) throws Exception {

                final String text = "jodd - Get things done!" + System.lineSeparator();
                final char[] expected = text.toCharArray();
                final File file = new File(BASE_DIR, testInfo.getTestMethod().get().getName());

                FileUtil.writeString(file, text, "UTF-8");

                char[] actual = null;

                try (FileInputStream inputStream = new FileInputStream(file)) {
                    actual = StreamUtil.readChars(inputStream);
                }

                // asserts
                assertNotNull(actual);
                assertArrayEquals(expected, actual);
            }

        }

        @Nested
        @DisplayName("tests for StreamUtil#readChars(InputStream input, int charCount)")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS) // needed because static method in inner class is not allowed
        class ReadChars_InputStream_CharCount {

            @ParameterizedTest
            @MethodSource("testdata")
            void testReadChars_InputStream_CharCount_0(char[] expected, String text, int charCount, TestInfo testInfo ) throws Exception {

                final int random = MathUtil.randomInt(1, 2500);
                final File file = new File(BASE_DIR, testInfo.getTestMethod().get().getName() + "." + random);

                FileUtil.writeString(file, text, "UTF-8");

                char[] actual = null;

                try (FileInputStream inputStream = new FileInputStream(file)) {
                    actual = StreamUtil.readChars(inputStream, charCount);
                }

                // asserts
                assertNotNull(actual);
                assertArrayEquals(expected, actual);
            }

            Stream<Arguments> testdata() {
                return Stream.of(
                        Arguments.of("jodd".toCharArray(),"jodd", 34 ),
                        Arguments.of("jodd".toCharArray(),"jodd", 4 ),
                        Arguments.of("jo".toCharArray(),"jodd", 2 ),
                        Arguments.of("".toCharArray(),"jodd", 0 )
                );
            }

        }

    }
}
