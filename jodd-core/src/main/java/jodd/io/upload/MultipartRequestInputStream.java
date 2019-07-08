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

package jodd.io.upload;

import jodd.io.FastByteArrayOutputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Extended input stream based on buffered requests input stream.
 * It provides some more functions that might be useful when working
 * with uploaded fies.
 */
public class MultipartRequestInputStream extends BufferedInputStream {

	public MultipartRequestInputStream(final InputStream in) {
		super(in);
	}

	/**
	 * Reads expected byte. Throws exception on streams end.
	 */
	public byte readByte() throws IOException {
		int i = super.read();
		if (i == -1) {
			throw new IOException("End of HTTP request stream reached");
		}
		return (byte) i;
	}

	/**
	 * Skips specified number of bytes.
	 */
	public void skipBytes(final int i) throws IOException {
		long len = super.skip(i);
		if (len != i) {
			throw new IOException("Failed to skip data in HTTP request");
		}
	}

	// ---------------------------------------------------------------- boundary

	protected byte[] boundary;

	/**
	 * Reads boundary from the input stream.
	 */
	public byte[] readBoundary() throws IOException {
		FastByteArrayOutputStream boundaryOutput = new FastByteArrayOutputStream();
		byte b;
		// skip optional whitespaces
		while ((b = readByte()) <= ' ') {
		}
		boundaryOutput.write(b);

		// now read boundary chars
		while ((b = readByte()) != '\r') {
			boundaryOutput.write(b);
		}
		if (boundaryOutput.size() == 0) {
			throw new IOException("Problems with parsing request: invalid boundary");
		}
		skipBytes(1);
		boundary = new byte[boundaryOutput.size() + 2];
		System.arraycopy(boundaryOutput.toByteArray(), 0, boundary, 2, boundary.length - 2);
		boundary[0] = '\r';
		boundary[1] = '\n';
		return boundary;
	}

	// ---------------------------------------------------------------- data header

	protected FileUploadHeader lastHeader;

	public FileUploadHeader getLastHeader() {
		return lastHeader;
	}

	/**
	 * Reads data header from the input stream. When there is no more
	 * headers (i.e. end of stream reached), returns <code>null</code>
	 */
	public FileUploadHeader readDataHeader(final String encoding) throws IOException {
		String dataHeader = readDataHeaderString(encoding);
		if (dataHeader != null) {
			lastHeader = new FileUploadHeader(dataHeader);
		} else {
			lastHeader = null;
		}
		return lastHeader;
	}


	protected String readDataHeaderString(final String encoding) throws IOException {
		FastByteArrayOutputStream data = new FastByteArrayOutputStream();
		byte b;
		while (true) {
			// end marker byte on offset +0 and +2 must be 13
			if ((b = readByte()) != '\r') {
				data.write(b);
				continue;
			}
			mark(4);
			skipBytes(1);
			int i = read();
			if (i == -1) {
				// reached end of stream
				return null;
			}
			if (i == '\r') {
				reset();
				break;
			}
			reset();
			data.write(b);
		}
		skipBytes(3);
		if (encoding != null) {
			return data.toString(encoding);
		} else {
			return data.toString();
		}
	}


	// ---------------------------------------------------------------- copy

	/**
	 * Copies bytes from this stream to some output until boundary is
	 * reached. Returns number of copied bytes. It will throw an exception
	 * for any irregular behaviour.
	 */
	public int copyAll(final OutputStream out) throws IOException {
		int count = 0;
		while (true) {
			byte b = readByte();
			if (isBoundary(b)) {
				break;
			}
			out.write(b);
			count++;
		}
		return count;
	}

	/**
	 * Copies max or less number of bytes to output stream. Useful for determining
	 * if uploaded file is larger then expected.
	 */
	public int copyMax(final OutputStream out, final int maxBytes) throws IOException {
		int count = 0;
		while (true) {
			byte b = readByte();
			if (isBoundary(b)) {
				break;
			}
			out.write(b);
			count++;
			if (count == maxBytes) {
				return count;
			}
		}
		return count;
	}

	/**
	 * Skips to the boundary and returns total number of bytes skipped.
	 */
	public int skipToBoundary() throws IOException {
		int count = 0;
		while (true) {
			byte b = readByte();
			count++;
			if (isBoundary(b)) {
				break;
			}
		}
		return count;
	}

	/**
	 * Checks if the current byte (i.e. one that was read last) represents
	 * the very first byte of the boundary.
	 */
	public boolean isBoundary(byte b) throws IOException {
		int boundaryLen = boundary.length;
		mark(boundaryLen + 1);
		int bpos = 0;
		while (b == boundary[bpos]) {
			b = readByte();
			bpos++;
			if (bpos == boundaryLen) {
				return true;	// boundary found!
			}
		}
		reset();
		return false;
	}
}
