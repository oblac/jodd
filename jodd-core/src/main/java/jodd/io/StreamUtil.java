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

import javax.activation.DataSource;
import java.io.*;

/**
 * Optimized byte and character stream utilities.
 */
public class StreamUtil {

  /**
   * Default encoding
   */
  private static final String DEFAULT_ENCODING = JoddCore.get().defaults().getEncoding();

  /**
   * Default IO buffer size
   */
  private static final int IO_BUFFER_SIZE = JoddCore.get().defaults().getIoBufferSize();

  private static final int ZERO = 0;
  private static final int NEGATIVE_ONE = -1;
  private static final int ALL = -103;

  private StreamUtil() {
  }

  // ---------------------------------------------------------------- silent close

  /**
   * Closes silently the closable object. If it is {@link Flushable}, it
   * will be flushed first. No exception will be thrown if an I/O error occurs.
   */
  public static void close(Closeable closeable) {
    if (closeable != null) {
      if (closeable instanceof Flushable) {
        try {
          ((Flushable) closeable).flush();
        } catch (IOException ignored) {
        }
      }

      try {
        closeable.close();
      } catch (IOException ignored) {
      }
    }
  }

  // ---------------------------------------------------------------- copy

  /**
   * Copies bytes from {@link Reader} to {@link Writer} using buffer.
   * {@link Reader} and {@link Writer} don't have to be wrapped to buffered, since copying is already optimized.
   *
   * @param input  {@link Reader} to read.
   * @param output {@link Writer} to write to.
   * @return The total number of characters read.
   * @throws IOException if there is an error reading or writing.
   */
  public static int copy(Reader input, Writer output) throws IOException {
    int numToRead = getBufferSize();
    char[] buffer = new char[numToRead];

    int totalRead = ZERO;
    int read;

    while ((read = input.read(buffer, ZERO, numToRead)) >= ZERO) {
      output.write(buffer, ZERO, read);
      totalRead = totalRead + read;
    }

    output.flush();
    return totalRead;
  }

  /**
   * Copies bytes from {@link InputStream} to {@link OutputStream} using buffer.
   * {@link InputStream} and {@link OutputStream} don't have to be wrapped to buffered,
   * since copying is already optimized.
   *
   * @param input  {@link InputStream} to read.
   * @param output {@link OutputStream} to write to.
   * @return The total number of bytes read.
   * @throws IOException if there is an error reading or writing.
   */
  public static int copy(InputStream input, OutputStream output) throws IOException {
    int numToRead = getBufferSize();
    byte[] buffer = new byte[numToRead];

    int totalRead = ZERO;
    int read;

    while ((read = input.read(buffer, ZERO, numToRead)) >= ZERO) {
      output.write(buffer, ZERO, read);
      totalRead = totalRead + read;
    }

    output.flush();
    return totalRead;
  }

  /**
   * Copies specified number of characters from {@link Reader} to {@link Writer} using buffer.
   * {@link Reader} and {@link Writer} don't have to be wrapped to buffered, since copying is already optimized.
   *
   * @param input  {@link Reader} to read.
   * @param output {@link Writer} to write to.
   * @param count  The number of characters to read.
   * @return The total number of characters read.
   * @throws IOException if there is an error reading or writing.
   */
  public static int copy(Reader input, Writer output, int count) throws IOException {
    if (count == ALL) {
      return copy(input, output);
    }

    int numToRead = getBufferSize(count);
    char[] buffer = new char[numToRead];

    int totalRead = ZERO;
    int read;

    while (numToRead > ZERO) {
      read = input.read(buffer, ZERO, getBufferSize(numToRead));
      if (read == NEGATIVE_ONE) {
        break;
      }
      output.write(buffer, ZERO, read);

      numToRead = numToRead - read;
      totalRead = totalRead + read;
    }

    output.flush();
    return totalRead;
  }

  /**
   * Copies specified number of bytes from {@link InputStream} to {@link OutputStream} using buffer.
   * {@link InputStream} and {@link OutputStream} don't have to be wrapped to buffered, since copying is already optimized.
   *
   * @param input  {@link InputStream} to read.
   * @param output {@link OutputStream} to write to.
   * @param count  The number of bytes to read.
   * @return The total number of bytes read.
   * @throws IOException if there is an error reading or writing.
   */
  public static int copy(InputStream input, OutputStream output, int count) throws IOException {
    if (count == ALL) {
      return copy(input, output);
    }

    int numToRead = getBufferSize(count);
    byte[] buffer = new byte[numToRead];

    int totalRead = ZERO;
    int read;

    while (numToRead > ZERO) {
      read = input.read(buffer, ZERO, getBufferSize(numToRead));
      if (read == NEGATIVE_ONE) {
        break;
      }
      output.write(buffer, ZERO, read);

      numToRead = numToRead - read;
      totalRead = totalRead + read;
    }

    output.flush();
    return totalRead;
  }

  // ---------------------------------------------------------------- read bytes

  /**
   * Reads all available bytes from {@link InputStream} as a byte array.
   * Uses {@link InputStream#available()} to determine the size of input stream.
   * This is the fastest method for reading {@link InputStream} to byte array, but
   * depends on {@link InputStream} implementation of {@link InputStream#available()}.
   *
   * @param input {@link InputStream} to read.
   * @return byte[]
   * @throws IOException if total read is less than {@link InputStream#available()};
   */
  public static byte[] readAvailableBytes(InputStream input) throws IOException {
    int numToRead = input.available();
    byte[] buffer = new byte[numToRead];

    int totalRead = ZERO;
    int read;

    while ((totalRead < numToRead) && (read = input.read(buffer, totalRead, numToRead - totalRead)) >= ZERO) {
      totalRead = totalRead + read;
    }

    if (totalRead < numToRead) {
      throw new IOException("Failed to completely read InputStream");
    }

    return buffer;
  }

  // ---------------------------------------------------------------- copy to OutputStream

  /**
   * @see #copy(Reader, OutputStream, String)
   */
  public static <T extends OutputStream> T copy(Reader input, T output) throws IOException {
    return copy(input, output, DEFAULT_ENCODING);
  }

  /**
   * @see #copy(Reader, OutputStream, String, int)
   */
  public static <T extends OutputStream> T copy(Reader input, T output, int count) throws IOException {
    return copy(input, output, DEFAULT_ENCODING, count);
  }

  /**
   * @see #copy(Reader, OutputStream, String, int)
   */
  public static <T extends OutputStream> T copy(Reader input, T output, String encoding) throws IOException {
    return copy(input, output, encoding, ALL);
  }

  /**
   * Copies {@link Reader} to {@link OutputStream} using buffer and specified encoding.
   *
   * @see #copy(Reader, Writer, int)
   */
  public static <T extends OutputStream> T copy(Reader input, T output, String encoding, int count) throws IOException {
    try (Writer out = getOutputStreamWriter(output, encoding)) {
      copy(input, out, count);
      return output;
    }
  }

  /**
   * Copies data from {@link DataSource} to a new {@link FastByteArrayOutputStream} and returns this.
   *
   * @param input {@link DataSource} to copy from.
   * @return new {@link FastByteArrayOutputStream} with data from input.
   * @see #copyToOutputStream(InputStream)
   */
  public static FastByteArrayOutputStream copyToOutputStream(DataSource input) throws IOException {
    return copyToOutputStream(input.getInputStream());
  }

  /**
   * @see #copyToOutputStream(InputStream, int)
   */
  public static FastByteArrayOutputStream copyToOutputStream(InputStream input) throws IOException {
    return copyToOutputStream(input, ALL);
  }

  /**
   * Copies {@link InputStream} to a new {@link FastByteArrayOutputStream} using buffer and specified encoding.
   *
   * @see #copy(InputStream, OutputStream, int)
   */
  public static FastByteArrayOutputStream copyToOutputStream(InputStream input, int count) throws IOException {
    try (FastByteArrayOutputStream output = getFastByteArrayOutputStream()) {
      copy(input, output, count);
      return output;
    }
  }

  /**
   * @see #copyToOutputStream(Reader, String)
   */
  public static FastByteArrayOutputStream copyToOutputStream(Reader input) throws IOException {
    return copyToOutputStream(input, DEFAULT_ENCODING);
  }

  /**
   * @see #copyToOutputStream(Reader, String, int)
   */
  public static FastByteArrayOutputStream copyToOutputStream(Reader input, String encoding) throws IOException {
    return copyToOutputStream(input, encoding, ALL);
  }

  /**
   * @see #copyToOutputStream(Reader, String, int)
   */
  public static FastByteArrayOutputStream copyToOutputStream(Reader input, int count) throws IOException {
    return copyToOutputStream(input, DEFAULT_ENCODING, count);
  }

  /**
   * Copies {@link Reader} to a new {@link FastByteArrayOutputStream} using buffer and specified encoding.
   *
   * @see #copy(Reader, OutputStream, String, int)
   */
  public static FastByteArrayOutputStream copyToOutputStream(Reader input, String encoding, int count) throws IOException {
    try (FastByteArrayOutputStream output = getFastByteArrayOutputStream()) {
      copy(input, output, encoding, count);
      return output;
    }
  }

  // ---------------------------------------------------------------- copy to Writer

  /**
   * @see #copy(InputStream, Writer, String)
   */
  public static <T extends Writer> T copy(InputStream input, T output) throws IOException {
    return copy(input, output, DEFAULT_ENCODING);
  }

  /**
   * @see #copy(InputStream, Writer, String, int)
   */
  public static <T extends Writer> T copy(InputStream input, T output, int count) throws IOException {
    return copy(input, output, DEFAULT_ENCODING, count);
  }

  /**
   * @see #copy(InputStream, Writer, String, int)
   */
  public static <T extends Writer> T copy(InputStream input, T output, String encoding) throws IOException {
    return copy(input, output, encoding, ALL);
  }

  /**
   * Copies {@link InputStream} to {@link Writer} using buffer and specified encoding.
   *
   * @see #copy(Reader, Writer, int)
   */
  public static <T extends Writer> T copy(InputStream input, T output, String encoding, int count) throws IOException {
    copy(getInputStreamReader(input, encoding), output, count);
    return output;
  }

  /**
   * @see #copy(InputStream, String)
   */
  public static FastCharArrayWriter copy(InputStream input) throws IOException {
    return copy(input, DEFAULT_ENCODING);
  }

  /**
   * @see #copy(InputStream, String, int)
   */
  public static FastCharArrayWriter copy(InputStream input, int count) throws IOException {
    return copy(input, DEFAULT_ENCODING, count);
  }

  /**
   * @see #copy(InputStream, String, int)
   */
  public static FastCharArrayWriter copy(InputStream input, String encoding) throws IOException {
    return copy(input, encoding, ALL);
  }

  /**
   * Copies {@link InputStream} to a new {@link FastCharArrayWriter} using buffer and specified encoding.
   *
   * @see #copy(InputStream, Writer, String, int)
   */
  public static FastCharArrayWriter copy(InputStream input, String encoding, int count) throws IOException {
    try (FastCharArrayWriter output = getFastCharArrayWriter()) {
      copy(input, output, encoding, count);
      return output;
    }
  }

  /**
   * @see #copy(Reader, int)
   */
  public static FastCharArrayWriter copy(Reader input) throws IOException {
    return copy(input, ALL);
  }

  /**
   * Copies {@link Reader} to a new {@link FastCharArrayWriter} using buffer and specified encoding.
   *
   * @see #copy(Reader, Writer, int)
   */
  public static FastCharArrayWriter copy(Reader input, int count) throws IOException {
    try (FastCharArrayWriter output = getFastCharArrayWriter()) {
      copy(input, output, count);
      return output;
    }
  }

  /**
   * Copies data from {@link DataSource} to a new {@link FastCharArrayWriter} and returns this.
   *
   * @param input {@link DataSource} to copy from.
   * @return new {@link FastCharArrayWriter} with data from input.
   * @see #copy(InputStream)
   */
  public static FastCharArrayWriter copy(DataSource input) throws IOException {
    return copy(input.getInputStream());
  }

  // ---------------------------------------------------------------- read bytes

  /**
   * @see #readBytes(InputStream, int)
   */
  public static byte[] readBytes(InputStream input) throws IOException {
    return readBytes(input, ALL);
  }

  /**
   * @see #copyToOutputStream(InputStream, int)
   */
  public static byte[] readBytes(InputStream input, int count) throws IOException {
    return copyToOutputStream(input, count).toByteArray();
  }

  /**
   * @see #readBytes(Reader, String)
   */
  public static byte[] readBytes(Reader input) throws IOException {
    return readBytes(input, DEFAULT_ENCODING);
  }

  /**
   * @see #readBytes(Reader, String, int)
   */
  public static byte[] readBytes(Reader input, int count) throws IOException {
    return readBytes(input, DEFAULT_ENCODING, count);
  }

  /**
   * @see #readBytes(Reader, String, int)
   */
  public static byte[] readBytes(Reader input, String encoding) throws IOException {
    return readBytes(input, encoding, ALL);
  }

  /**
   * @see #copyToOutputStream(Reader, String, int)
   */
  public static byte[] readBytes(Reader input, String encoding, int count) throws IOException {
    return copyToOutputStream(input, encoding, count).toByteArray();
  }

  // ---------------------------------------------------------------- read chars

  /**
   * @see #readChars(Reader, int)
   */
  public static char[] readChars(Reader input) throws IOException {
    return readChars(input, ALL);
  }

  /**
   * @see #copy(Reader, int)
   */
  public static char[] readChars(Reader input, int count) throws IOException {
    return copy(input, count).toCharArray();
  }

  /**
   * @see #readChars(InputStream, int)
   */
  public static char[] readChars(InputStream input) throws IOException {
    return readChars(input, ALL);
  }

  /**
   * @see #readChars(InputStream, String, int)
   */
  public static char[] readChars(InputStream input, String encoding) throws IOException {
    return readChars(input, encoding, ALL);
  }

  /**
   * @see #readChars(InputStream, String, int)
   */
  public static char[] readChars(InputStream input, int count) throws IOException {
    return readChars(input, DEFAULT_ENCODING, count);
  }

  /**
   * @see #copy(InputStream, String, int)
   */
  public static char[] readChars(InputStream input, String encoding, int count) throws IOException {
    return copy(input, encoding, count).toCharArray();
  }

  // ---------------------------------------------------------------- compare content

  /**
   * Compares the content of two byte streams ({@link InputStream}s).
   *
   * @return {@code true} if the content of the first {@link InputStream} is equal
   * to the content of the second {@link InputStream}.
   */
  public static boolean compare(InputStream input1, InputStream input2) throws IOException {
    if (!(input1 instanceof BufferedInputStream)) {
      input1 = new BufferedInputStream(input1);
    }
    if (!(input2 instanceof BufferedInputStream)) {
      input2 = new BufferedInputStream(input2);
    }
    int ch = input1.read();
    while (ch != NEGATIVE_ONE) {
      int ch2 = input2.read();
      if (ch != ch2) {
        return false;
      }
      ch = input1.read();
    }
    int ch2 = input2.read();
    return (ch2 == NEGATIVE_ONE);
  }

  /**
   * Compares the content of two character streams ({@link Reader}s).
   *
   * @return {@code true} if the content of the first {@link Reader} is equal
   * to the content of the second {@link Reader}.
   */
  public static boolean compare(Reader input1, Reader input2) throws IOException {
    if (!(input1 instanceof BufferedReader)) {
      input1 = new BufferedReader(input1);
    }
    if (!(input2 instanceof BufferedReader)) {
      input2 = new BufferedReader(input2);
    }

    int ch = input1.read();
    while (ch != NEGATIVE_ONE) {
      int ch2 = input2.read();
      if (ch != ch2) {
        return false;
      }
      ch = input1.read();
    }
    int ch2 = input2.read();
    return (ch2 == NEGATIVE_ONE);
  }

  // ---------------------------------------------------------------- buffer size

  /**
   * Returns default IO buffer size.
   *
   * @return default IO buffer size.
   */
  private static int getBufferSize() {
    return IO_BUFFER_SIZE;
  }

  /**
   * Returns either count or default IO buffer size (whichever is smaller).
   *
   * @param count Number of characters or bytes to retrieve.
   * @return buffer size (either count or default IO buffer size, whichever is smaller).
   */
  private static int getBufferSize(int count) {
    if (count < IO_BUFFER_SIZE) {
      return count;
    } else {
      return IO_BUFFER_SIZE;
    }
  }

  // ---------------------------------------------------------------- wrappers

  /**
   * Returns new {@link FastCharArrayWriter} using default IO buffer size.
   *
   * @return new {@link FastCharArrayWriter} using default IO buffer size.
   */
  private static FastCharArrayWriter getFastCharArrayWriter() {
    return new FastCharArrayWriter(IO_BUFFER_SIZE);
  }

  /**
   * Returns new {@link FastByteArrayOutputStream} using default IO buffer size.
   *
   * @return new {@link FastByteArrayOutputStream} using default IO buffer size.
   */
  private static FastByteArrayOutputStream getFastByteArrayOutputStream() {
    return new FastByteArrayOutputStream(IO_BUFFER_SIZE);
  }

  /**
   * @see #getInputStreamReader(InputStream, String)
   */
  public static InputStreamReader getInputStreamReader(InputStream input) throws UnsupportedEncodingException {
    return getInputStreamReader(input, DEFAULT_ENCODING);
  }

  /**
   * Returns new {@link InputStreamReader} using specified {@link InputStream} and encoding.
   *
   * @param input    {@link InputStream}
   * @param encoding Encoding as {@link String} to use for {@link InputStreamReader}.
   * @return new {@link InputStreamReader}
   * @throws UnsupportedEncodingException if encoding is not valid.
   */
  public static InputStreamReader getInputStreamReader(InputStream input, String encoding) throws UnsupportedEncodingException {
    return new InputStreamReader(input, encoding);
  }

  /**
   * @see #getOutputStreamWriter(OutputStream, String)
   */
  public static OutputStreamWriter getOutputStreamWriter(OutputStream output) throws UnsupportedEncodingException {
    return getOutputStreamWriter(output, DEFAULT_ENCODING);
  }

  /**
   * Returns new {@link OutputStreamWriter} using specified {@link OutputStream} and encoding.
   *
   * @param output   {@link OutputStream}
   * @param encoding Encoding as {@link String} to use for {@link OutputStreamWriter}.
   * @return new {@link OutputStreamWriter}
   * @throws UnsupportedEncodingException if encoding is not valid.
   */
  public static OutputStreamWriter getOutputStreamWriter(OutputStream output, String encoding) throws UnsupportedEncodingException {
    return new OutputStreamWriter(output, encoding);
  }
}
