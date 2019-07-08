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
import jodd.system.SystemUtil;
import jodd.util.ArraysUtil;
import jodd.util.MathUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for {@link StreamUtil}.
 * <p>
 * Tests are grouped in nested classes.
 */
class StreamUtilTest {

    static final File BASE_DIR = new File(SystemUtil.info().getTempDir(), "jodd/StreamUtilTest");

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
            try (StringReader reader_1 = new StringReader(text); StringReader reader_2 = new StringReader(text)) {
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
    @DisplayName("tests for StreamUtil#readChars - methods")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS) // needed because annotation MethodSource requires static method without that
    class ReadChars {

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


        @ParameterizedTest
        @MethodSource("testdata_testReadChars_InputStream_CharCount")
        void testReadChars_InputStream_CharCount(char[] expected, String text, int charCount, TestInfo testInfo ) throws Exception {

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

        Stream<Arguments> testdata_testReadChars_InputStream_CharCount() {
            return Stream.of(
                    Arguments.of("jodd".toCharArray(),"jodd", 34 ),
                    Arguments.of("jodd".toCharArray(),"jodd", 4 ),
                    Arguments.of("jo".toCharArray(),"jodd", 2 ),
                    Arguments.of("".toCharArray(),"jodd", 0 )
            );
        }

        @ParameterizedTest
        @MethodSource("testdata_testReadChars_InputStream_Encoding")
        void testReadChars_InputStream_Encoding(char[] expected, String text, String encoding) throws Exception {

            char[] actual = null;

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(text.getBytes())) {
                actual = StreamUtil.readChars(inputStream, encoding);
            }

            // asserts
            assertNotNull(actual);
            assertArrayEquals(expected, actual);
        }

        Stream<Arguments> testdata_testReadChars_InputStream_Encoding() throws Exception {
            return Stream.of(
                    Arguments.of("äüö".toCharArray(),"äüö", "UTF-8" ),
                    Arguments.of(new String("üöä".getBytes(), "ISO-8859-1").toCharArray(),"üöä", "ISO-8859-1" )
            );
        }


        @ParameterizedTest
        @MethodSource("testdata_testReadChars_InputStream_Encoding_CharCount")
        void testReadChars_InputStream_Encoding_CharCount(char[] expected, String text, String encoding, int charCount) throws Exception {

            char[] actual = null;

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(text.getBytes())) {
                actual = StreamUtil.readChars(inputStream, encoding, charCount);
            }

            // asserts
            assertNotNull(actual);
            assertArrayEquals(expected, actual);
        }

        Stream<Arguments> testdata_testReadChars_InputStream_Encoding_CharCount() throws Exception {
            return Stream.of(
                    Arguments.of("äüö".toCharArray(),"äüö", "UTF-8", 4 ),
                    Arguments.of("j".toCharArray(), "jodd", "ISO-8859-1", 1 ),
                    Arguments.of(new String("jodd".getBytes(), "US-ASCII").toCharArray(),"jodd", "US-ASCII", 44 )
            );
        }


        @ParameterizedTest
        @MethodSource("testdata_testReadChars_Reader")
        void testReadChars_Reader(char[] expected, String text) throws Exception {

            char[] actual = null;

            try (StringReader reader = new StringReader(text)) {
                actual = StreamUtil.readChars(reader);
            }

            // asserts
            assertNotNull(actual);
            assertArrayEquals(expected, actual);
        }

        Stream<Arguments> testdata_testReadChars_Reader() throws Exception {
            return Stream.of(
                    Arguments.of("äüö".toCharArray(),"äüö" ),
                    Arguments.of("jodd makes fun".toCharArray(), "jodd makes fun")
            );
        }


        @ParameterizedTest
        @MethodSource("testdata_testReadChars_Reader_CharCount")
        void testReadChars_Reader_CharCount(char[] expected, String text, int charCount) throws Exception {

            char[] actual = null;

            try (StringReader reader = new StringReader(text)) {
                actual = StreamUtil.readChars(reader, charCount);
            }

            // asserts
            assertNotNull(actual);
            assertArrayEquals(expected, actual);
        }

        Stream<Arguments> testdata_testReadChars_Reader_CharCount() throws Exception {
            return Stream.of(
                    Arguments.of("ä".toCharArray(),"äüö", 1 ),
                    Arguments.of("jodd makes fun".toCharArray(), "jodd makes fun", "jodd makes fun".length()),
                    Arguments.of("jodd makes fun".toCharArray(), "jodd makes fun", 478)
            );
        }

    }

    @Nested
    @DisplayName("tests for StreamUtil#readBytes - methods")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS) // needed because annotation MethodSource requires static method without that
    class ReadBytes {

        @ParameterizedTest
        @MethodSource("testdata_testReadBytes_InputStream")
        void testReadBytes_InputStream(byte[] expected, String text) throws Exception {

            byte[] actual = null;

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(text.getBytes())) {
                actual = StreamUtil.readBytes(inputStream);
            }

            // asserts
            assertNotNull(actual);
            assertArrayEquals(expected, actual);
        }

        Stream<Arguments> testdata_testReadBytes_InputStream() throws Exception {
            return Stream.of(
                    Arguments.of("äöü".getBytes(),"äöü" ),
                    Arguments.of("".getBytes(),"" )
            );
        }


        @ParameterizedTest
        @MethodSource("testdata_testReadBytes_InputStream_ByteCount")
        void testReadBytes_InputStream_ByteCount(byte[] expected, String text, int byteCount) throws Exception {

            byte[] actual = null;

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(text.getBytes())) {
                actual = StreamUtil.readBytes(inputStream, byteCount);
            }

            // asserts
            assertNotNull(actual);
            assertArrayEquals(expected, actual);
        }

        Stream<Arguments> testdata_testReadBytes_InputStream_ByteCount() throws Exception {
            return Stream.of(
                    Arguments.of(ArraysUtil.subarray("ä".getBytes(),0,1), "äöü", 1),
                    Arguments.of("jo".getBytes(), "jodd", 2),
                    Arguments.of("".getBytes(), "", 8)
            );
        }


        @ParameterizedTest
        @MethodSource("testdata_testReadBytes_Reader")
        void testReadBytes_Reader(byte[] expected, String text) throws Exception {

            byte[] actual = null;

            try (StringReader reader = new StringReader(text)) {
                actual = StreamUtil.readBytes(reader);
            }

            // asserts
            assertNotNull(actual);
            assertArrayEquals(expected, actual);
        }

        Stream<Arguments> testdata_testReadBytes_Reader() throws Exception {
            return Stream.of(
                    Arguments.of("äöü".getBytes(), "äöü"),
                    Arguments.of("".getBytes(), "")
            );
        }


        @ParameterizedTest
        @MethodSource("testdata_testReadBytes_Reader_ByteCount")
        void testReadBytes_Reader_ByteCount(byte[] expected, String text, int byteCount) throws Exception {

            byte[] actual = null;

            try (StringReader reader = new StringReader(text)) {
                actual = StreamUtil.readBytes(reader, byteCount);
            }

            // asserts
            assertNotNull(actual);
            assertArrayEquals(expected, actual);
        }

        Stream<Arguments> testdata_testReadBytes_Reader_ByteCount() throws Exception {
            return Stream.of(
                    Arguments.of("äö".getBytes(), "äöü", 2),
                    Arguments.of("jodd".getBytes(), "jodd", 8),
                    Arguments.of("".getBytes(), "jodd makes fun", 0),
                    Arguments.of("".getBytes(), "", 4)
            );
        }


        @ParameterizedTest
        @MethodSource("testdata_testReadBytes_Reader_Encoding")
        void testReadBytes_Reader_Encoding(byte[] expected, String text, String encoding, TestInfo testInfo) throws Exception {

            final int random = MathUtil.randomInt(1, 2500);
            final File file = new File(StreamUtilTest.BASE_DIR, testInfo.getTestMethod().get().getName() + random);

            FileUtil.writeString(file, text, encoding);

            byte[] actual = null;

            try (FileReader reader = new FileReader(file)) {
                actual = StreamUtil.readBytes(reader, encoding);
            }

            // asserts
            assertNotNull(actual);
            assertArrayEquals(expected, actual);
        }

        Stream<Arguments> testdata_testReadBytes_Reader_Encoding() throws Exception {
            return Stream.of(
                    Arguments.of("jodd".getBytes("ISO-8859-1"), "jodd" , "ISO-8859-1"),
                    Arguments.of("üäö".getBytes("UTF-8"), "üäö" , "UTF-8")
            );
        }


        @ParameterizedTest
        @MethodSource("testdata_testReadBytes_Reader_Encoding_ByteCount")
        void testReadBytes_Reader_Encoding_ByteCount(byte[] expected, String text, String encoding, int byteCount, TestInfo testInfo) throws Exception {

            final int random = MathUtil.randomInt(1, 2500);
            final File file = new File(StreamUtilTest.BASE_DIR, testInfo.getTestMethod().get().getName() + random);

            FileUtil.writeString(file, text, encoding);

            byte[] actual = null;

            try (FileReader reader = new FileReader(file)) {
                actual = StreamUtil.readBytes(reader, encoding, byteCount);
            }

            // asserts
            assertNotNull(actual);
            assertArrayEquals(expected, actual);
        }

        Stream<Arguments> testdata_testReadBytes_Reader_Encoding_ByteCount() throws Exception {
            return Stream.of(
                    Arguments.of("jodd".getBytes("ISO-8859-1"), "jodd" , "ISO-8859-1", 10),
                    Arguments.of("j".getBytes("ISO-8859-1"), "jodd" , "ISO-8859-1", 1),
                    Arguments.of("üäö".getBytes("UTF-8"), "üäö" , "UTF-8", 3)
            );
        }

    }

    @Nested
    @DisplayName("tests for StreamUtil#copy - methods")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS) // needed because annotation MethodSource requires static method without that
    class Copy {

        @Test
        void testCopy_Inputstream_Outputstream() throws Exception {

            try (ByteArrayInputStream in = new ByteArrayInputStream("input".getBytes());
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                StreamUtil.copy(in, out);

                // asserts
                assertEquals("input", out.toString());
            }

        }

        @ParameterizedTest
        @MethodSource("testdata_testCopy_Inputstream_Outputstream_ByteCount")
        void testCopy_Inputstream_Outputstream_ByteCount(String expected, String text, int byteCount) throws Exception {

            try (ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes());
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                StreamUtil.copy(in, out, byteCount);

                // asserts
                assertEquals(expected, out.toString());
            }

        }

        Stream<Arguments> testdata_testCopy_Inputstream_Outputstream_ByteCount() throws Exception {
            return Stream.of(
                    Arguments.of("The Unbearable Lightness of Java", "The Unbearable Lightness of Java", JoddCore.ioBufferSize + 250),
                    Arguments.of("j", "jodd" , 1),
                    Arguments.of("jodd makes fun!", "jodd makes fun!",  15),
                    Arguments.of("", "text does not matter",  0)
            );
        }

        @ParameterizedTest
        @MethodSource("testdata_testCopy_Inputstream_Writer_Encoding")
        void testCopy_Inputstream_Writer_Encoding(String expected, String text, String encoding) throws Exception {

            try (ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes());
                 StringWriter writer = new StringWriter()) {

                StreamUtil.copy(in, writer, encoding );

                // asserts
                assertEquals(expected, writer.toString());
            }

        }

        Stream<Arguments> testdata_testCopy_Inputstream_Writer_Encoding() throws Exception {
            return Stream.of(
                    Arguments.of("The Unbearable Lightness of Java", "The Unbearable Lightness of Java", "UTF-8"),
                    Arguments.of("Ã¼Ã¶Ã¤", "üöä", "ISO-8859-1"),
                    Arguments.of("", "", "US-ASCII")
            );
        }

        @ParameterizedTest
        @MethodSource("testdata_testCopy_Inputstream_Writer_Encoding_ByteCount")
        void testCopy_Inputstream_Writer_Encoding_ByteCount(String expected, String text, String encoding, int byteCount) throws Exception {

            try (ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes());
                 StringWriter writer = new StringWriter()) {

                StreamUtil.copy(in, writer, encoding, byteCount );

                // asserts
                assertEquals(expected, writer.toString());
            }

        }

        Stream<Arguments> testdata_testCopy_Inputstream_Writer_Encoding_ByteCount() throws Exception {
            return Stream.of(
                    Arguments.of("The Unbearable ", "The Unbearable Lightness of Java", "US-ASCII", 15),
                    Arguments.of("AbC", "AbC", "ISO-8859-1", 15)
            );
        }


        @ParameterizedTest
        @MethodSource("testdata_testCopy_Reader_Outpustream_Encoding")
        void testCopy_Reader_Outpustream_Encoding(byte[] expected, String text, String encoding) throws Exception {

            try (StringReader reader = new StringReader(text);
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                StreamUtil.copy(reader, outputStream, encoding);

                // asserts
                assertArrayEquals(expected, outputStream.toByteArray());
            }

        }

        Stream<Arguments> testdata_testCopy_Reader_Outpustream_Encoding() throws Exception {
            return Stream.of(
                    Arguments.of(new byte[] {63,63,63}, "üöä", "US-ASCII"),
                    Arguments.of(new byte[] {-61,-68,-61,-74,-61,-92}, "üöä", "UTF-8"),
                    Arguments.of(new byte[] {106,111,100,100}, "jodd", "US-ASCII")
            );
        }


        @ParameterizedTest
        @MethodSource("testdata_testCopy_Reader_Outpustream_Encoding_CharCount")
        void testCopy_Reader_Outpustream_Encoding_CharCount(byte[] expected, String text, String encoding, int charCount) throws Exception {

            try (StringReader reader = new StringReader(text);
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                StreamUtil.copy(reader, outputStream, encoding, charCount);

                // asserts
                assertArrayEquals(expected, outputStream.toByteArray());
            }

        }

        Stream<Arguments> testdata_testCopy_Reader_Outpustream_Encoding_CharCount() {
            return Stream.of(
                    Arguments.of(new byte[] {63,63,63}, "üöä", "US-ASCII", 4),
                    Arguments.of(new byte[] {-61,-68,-61,-74}, "üöä", "UTF-8", 2),
                    Arguments.of(new byte[] {106,111,100,100}, "jodd", "US-ASCII", 8)
            );
        }
    }

    @Test
    void testCopy_all() throws IOException {
    	byte[] bytes = randomBuffer(128 * 1024*1024);

    	InputStream inputStream = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		StreamUtil.copy(inputStream, outputStream);
		byte[] outBytes = outputStream.toByteArray();

		assertArrayEquals(bytes, outBytes);
    }

    @Test
    void testCopy_withSize() throws IOException {
    	byte[] bytes = randomBuffer(128 * 1024*1024);

    	InputStream inputStream = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		StreamUtil.copy(inputStream, outputStream, bytes.length);
		byte[] outBytes = outputStream.toByteArray();

		assertArrayEquals(bytes, outBytes);
    }

    private byte[] randomBuffer(int size) {
    	byte[] bytes = new byte[size];
		for (int i = 0; i < size; i++) {
			bytes[i] = (byte) MathUtil.randomInt(Byte.MIN_VALUE, Byte.MAX_VALUE);
		}

		return bytes;
	}

}
