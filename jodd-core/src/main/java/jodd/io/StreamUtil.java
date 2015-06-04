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

import static jodd.core.JoddCore.ioBufferSize;

import jodd.core.JoddCore;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * Optimized byte and character stream utilities.
 */
public class StreamUtil {

	// ---------------------------------------------------------------- silent close

	/**
	 * Closes an input stream and releases any system resources associated with
	 * this stream. No exception will be thrown if an I/O error occurs.
	 */
	public static void close(InputStream in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException ioex) {
				// ignore
			}
		}
	}

	/**
	 * Closes an output stream and releases any system resources associated with
	 * this stream. No exception will be thrown if an I/O error occurs.
	 */
	public static void close(OutputStream out) {
		if (out != null) {
			try {
				out.flush();
			} catch (IOException ioex) {
				// ignore
			}
			try {
				out.close();
			} catch (IOException ioex) {
				// ignore
			}
		}
	}

	/**
	 * Closes a character-input stream and releases any system resources
	 * associated with this stream. No exception will be thrown if an I/O error
	 * occurs.
	 */
	public static void close(Reader in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException ioex) {
				// ignore
			}
		}
	}

	/**
	 * Closes a character-output stream and releases any system resources
	 * associated with this stream. No exception will be thrown if an I/O error
	 * occurs.
	 */
	public static void close(Writer out) {
		if (out != null) {
			try {
				out.flush();
			} catch (IOException ioex) {
				// ignore
			}
			try {
				out.close();
			} catch (IOException ioex) {
				// ignore
			}
		}
	}



	// ---------------------------------------------------------------- copy

	/**
	 * Copies input stream to output stream using buffer. Streams don't have
	 * to be wrapped to buffered, since copying is already optimized.
	 */
	public static int copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[ioBufferSize];
		int count = 0;
		int read;
		while (true) {
			read = input.read(buffer, 0, ioBufferSize);
			if (read == -1) {
				break;
			}
			output.write(buffer, 0, read);
			count += read;
		}
		return count;
	}
	/**
	 * Copies specified number of bytes from input stream to output stream using buffer.
	 */
	public static int copy(InputStream input, OutputStream output, int byteCount) throws IOException {
		int bufferSize = (byteCount > ioBufferSize) ? ioBufferSize : byteCount;

		byte buffer[] = new byte[bufferSize];
		int count = 0;
		int read;
		while (byteCount > 0) {
			if (byteCount < bufferSize) {
				read = input.read(buffer, 0, byteCount);
			} else {
				read = input.read(buffer, 0, bufferSize);
			}
			if (read == -1) {
				break;
			}
			byteCount -= read;
			count += read;
			output.write(buffer, 0, read);
		}
		return count;
	}




	/**
	 * Copies input stream to writer using buffer.
	 */
	public static void copy(InputStream input, Writer output) throws IOException {
		copy(input, output, JoddCore.encoding);
	}
	/**
	 * Copies specified number of bytes from input stream to writer using buffer.
	 */
	public static void copy(InputStream input, Writer output, int byteCount) throws IOException {
		copy(input, output, JoddCore.encoding, byteCount);
	}
	/**
	 * Copies input stream to writer using buffer and specified encoding.
	 */
	public static void copy(InputStream input, Writer output, String encoding) throws IOException {
		copy(new InputStreamReader(input, encoding), output);
	}
	/**
	 * Copies specified number of bytes from input stream to writer using buffer and specified encoding.
	 */
	public static void copy(InputStream input, Writer output, String encoding, int byteCount) throws IOException {
		copy(new InputStreamReader(input, encoding), output, byteCount);
	}

    /**
	 * Copies reader to writer using buffer.
	 * Streams don't have to be wrapped to buffered, since copying is already optimized.
	 */
	public static int copy(Reader input, Writer output) throws IOException {
		char[] buffer = new char[ioBufferSize];
		int count = 0;
		int read;
		while ((read = input.read(buffer, 0, ioBufferSize)) >= 0) {
			output.write(buffer, 0, read);
			count += read;
		}
		output.flush();
		return count;
	}
	/**
	 * Copies specified number of characters from reader to writer using buffer.
	 */
	public static int copy(Reader input, Writer output, int charCount) throws IOException {
		int bufferSize = (charCount > ioBufferSize) ? ioBufferSize : charCount;

		char buffer[] = new char[bufferSize];
		int count = 0;
		int read;
		while (charCount > 0) {
			if (charCount < bufferSize) {
				read = input.read(buffer, 0, charCount);
			} else {
				read = input.read(buffer, 0, bufferSize);
			}
			if (read == -1) {
				break;
			}
			charCount -= read;
			count += read;
			output.write(buffer, 0, read);
		}
		return count;
	}



	/**
	 * Copies reader to output stream using buffer.
	 */
	public static void copy(Reader input, OutputStream output) throws IOException {
		copy(input, output, JoddCore.encoding);
	}
	/**
	 * Copies specified number of characters from reader to output stream using buffer.
	 */
	public static void copy(Reader input, OutputStream output, int charCount) throws IOException {
		copy(input, output, JoddCore.encoding, charCount);
	}
	/**
	 * Copies reader to output stream using buffer and specified encoding.
	 */
	public static void copy(Reader input, OutputStream output, String encoding) throws IOException {
		Writer out = new OutputStreamWriter(output, encoding);
		copy(input, out);
		out.flush();
	}
	/**
	 * Copies specified number of characters from reader to output stream using buffer and specified encoding.
	 */
	public static void copy(Reader input, OutputStream output, String encoding, int charCount) throws IOException {
		Writer out = new OutputStreamWriter(output, encoding);
		copy(input, out, charCount);
		out.flush();
	}


	// ---------------------------------------------------------------- read bytes

	/**
	 * Reads all available bytes from InputStream as a byte array.
	 * Uses <code>in.available()</code> to determine the size of input stream.
	 * This is the fastest method for reading input stream to byte array, but
	 * depends on stream implementation of <code>available()</code>.
	 * Buffered internally.
	 */
	public static byte[] readAvailableBytes(InputStream in) throws IOException {
		int l = in.available();
		byte byteArray[] = new byte[l];
		int i = 0, j;
		while ((i < l) && (j = in.read(byteArray, i, l - i)) >= 0) {
			i +=j;
		}
		if (i < l) {
			throw new IOException("Failed to completely read input stream");
		}
		return byteArray;
	}

	public static byte[] readBytes(InputStream input) throws IOException {
		FastByteArrayOutputStream output = new FastByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}
	public static byte[] readBytes(InputStream input, int byteCount) throws IOException {
		FastByteArrayOutputStream output = new FastByteArrayOutputStream();
		copy(input, output, byteCount);
		return output.toByteArray();
	}

	public static byte[] readBytes(Reader input) throws IOException {
		FastByteArrayOutputStream output = new FastByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}
	public static byte[] readBytes(Reader input, int byteCount) throws IOException {
		FastByteArrayOutputStream output = new FastByteArrayOutputStream();
		copy(input, output, byteCount);
		return output.toByteArray();
	}
	public static byte[] readBytes(Reader input, String encoding) throws IOException {
		FastByteArrayOutputStream output = new FastByteArrayOutputStream();
		copy(input, output, encoding);
		return output.toByteArray();
	}
	public static byte[] readBytes(Reader input, String encoding, int byteCount) throws IOException {
		FastByteArrayOutputStream output = new FastByteArrayOutputStream();
		copy(input, output, encoding, byteCount);
		return output.toByteArray();
	}

	// ---------------------------------------------------------------- read chars

	public static char[] readChars(InputStream input) throws IOException {
		FastCharArrayWriter output = new FastCharArrayWriter();
		copy(input, output);
		return output.toCharArray();
	}
	public static char[] readChars(InputStream input, int charCount) throws IOException {
		FastCharArrayWriter output = new FastCharArrayWriter();
		copy(input, output, charCount);
		return output.toCharArray();
	}

	public static char[] readChars(InputStream input, String encoding) throws IOException {
        FastCharArrayWriter output = new FastCharArrayWriter();
        copy(input, output, encoding);
        return output.toCharArray();
    }
	public static char[] readChars(InputStream input, String encoding, int charCount) throws IOException {
        FastCharArrayWriter output = new FastCharArrayWriter();
        copy(input, output, encoding, charCount);
        return output.toCharArray();
    }

	public static char[] readChars(Reader input) throws IOException {
		FastCharArrayWriter output = new FastCharArrayWriter();
		copy(input, output);
		return output.toCharArray();
	}
	public static char[] readChars(Reader input, int charCount) throws IOException {
		FastCharArrayWriter output = new FastCharArrayWriter();
		copy(input, output, charCount);
		return output.toCharArray();
	}


	// ---------------------------------------------------------------- compare content

	/**
	 * Compares the content of two byte streams.
	 *
	 * @return <code>true</code> if the content of the first stream is equal
	 *         to the content of the second stream.
	 */
	public static boolean compare(InputStream input1, InputStream input2) throws IOException {
        if (!(input1 instanceof BufferedInputStream)) {
            input1 = new BufferedInputStream(input1);
        }
        if (!(input2 instanceof BufferedInputStream)) {
            input2 = new BufferedInputStream(input2);
        }
        int ch = input1.read();
        while (ch != -1) {
            int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }
        int ch2 = input2.read();
		return (ch2 == -1);
	}
	/**
	 * Compares the content of two character streams.
	 *
	 * @return <code>true</code> if the content of the first stream is equal
	 *         to the content of the second stream.
	 */
	public static boolean compare(Reader input1, Reader input2) throws IOException {
        if (!(input1 instanceof BufferedReader)) {
            input1 = new BufferedReader(input1);
        }
        if (!(input2 instanceof BufferedReader)) {
            input2 = new BufferedReader(input2);
        }

        int ch = input1.read();
        while (ch != -1) {
            int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }
        int ch2 = input2.read();
        return (ch2 == -1);
    }

}
