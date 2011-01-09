// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.upload;

import jodd.io.FastByteArrayOutputStream;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Extended input stream based on buffered requests input stream.
 * It provides some more functions that might be useful when working
 * with uploaded fies.
 */
public class MultipartRequestInputStream extends BufferedInputStream {

	public MultipartRequestInputStream(InputStream in) {
		super(in);
	}

	public byte readByte() throws IOException {
		int i = super.read();
		if (i == -1) {
			throw new IOException("Unable to read data from HTTP request.");
		}
		return (byte) i;
	}

	public void skipBytes(int i) throws IOException {
		long len = super.skip(i);
		if (len != i) {
			throw new IOException("Unable to skip data in HTTP request.");
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
		while ((b = readByte()) != '\r') {
			boundaryOutput.write(b);
		}
		if (boundaryOutput.size() == 0) {
			throw new IOException("Problems with parsing request: invalid boundary.");
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
	public FileUploadHeader readDataHeader(String encoding) throws IOException {
		String dataHeader = readDataHeaderString(encoding);
		if (dataHeader != null) {
			lastHeader = new FileUploadHeader(dataHeader);
		} else {
			lastHeader = null;
		}
		return lastHeader;
	}


	protected String readDataHeaderString(String encoding) throws IOException {
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
	public int copyAll(OutputStream out) throws IOException {
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
	public int copyMax(OutputStream out, int maxBytes) throws IOException {
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
