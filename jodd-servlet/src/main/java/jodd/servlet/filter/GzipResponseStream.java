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

package jodd.servlet.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP response stream.
 */
public class GzipResponseStream extends ServletOutputStream {

	/**
	 * Constructs a servlet output stream associated with the specified Response.
	 */
	public GzipResponseStream(final HttpServletResponse response) throws IOException {
		super();
		closed = false;
		this.response = response;
		this.output = response.getOutputStream();
	}

	/**
	 * The threshold number which decides to compress or not.
	 */
	protected int compressionThreshold;

	/**
	 * The buffer through which all of our output bytes are passed.
	 */
	protected byte[] buffer;

	/**
	 * The number of data bytes currently in the buffer.
	 */
	protected int bufferCount;

	/**
	 * The underlying gzip output stream to which we should write data.
	 */
	protected GZIPOutputStream gzipstream;

	/**
	 * Has this stream been closed?
	 */
	protected boolean closed;

	/**
	 * The content length past which we will not write, or -1 if there is no
	 * defined content length.
	 */
	protected int length = -1;

	/**
	 * The response with which this servlet output stream is associated.
	 */
	protected HttpServletResponse response;

	/**
	 * The underlying servlet output stream to which we should write data.
	 */
	protected ServletOutputStream output;


	/**
	 * Sets the compressionThreshold number and create buffer for this size.
	 */
	protected void setBuffer(final int threshold) {
		compressionThreshold = threshold;
		buffer = new byte[compressionThreshold];
	}

	@Override
	public boolean isReady() {
		return output.isReady();
	}

	@Override
	public void setWriteListener(final WriteListener writeListener) {
		output.setWriteListener(writeListener);
	}

	/**
	 * Closes this output stream, causing any buffered data to be flushed and any
	 * further output data to throw an IOException.
	 */
	@Override
	public void close() throws IOException {
		if (closed) {
			return;
		}
		if (gzipstream != null) {
			flushToGZip();
			gzipstream.close();
			gzipstream = null;
		} else {
			if (bufferCount > 0) {
				output.write(buffer, 0, bufferCount);
				bufferCount = 0;
			}
		}
		output.close();
		closed = true;
	}


	/**
	 * Flushes any buffered data for this output stream, which also causes the
	 * response to be committed.
	 */
	@Override
	public void flush() throws IOException {

		if (closed) {
			return;
		}
		if (gzipstream != null) {
			gzipstream.flush();
		}

	}

	public void flushToGZip() throws IOException {
		if (bufferCount > 0) {
			writeToGZip(buffer, 0, bufferCount);
			bufferCount = 0;
		}
	}

	/**
	 * Writes the specified byte to our output stream.
	 */
	@Override
	public void write(final int b) throws IOException {

		if (closed) {
			throw new IOException("Cannot write to a closed output stream");
		}
		if (bufferCount >= buffer.length) {
			flushToGZip();
		}
		buffer[bufferCount++] = (byte) b;
	}


	/**
	 * Writes <code>b.length</code> bytes from the specified byte array to our
	 * output stream.
	 */
	@Override
	public void write(final byte[] b) throws IOException {
		write(b, 0, b.length);
	}


	/**
	 * Writes <code>len</code> bytes from the specified byte array, starting at
	 * the specified offset, to our output stream.
	 *
	 * @param b      byte array containing the bytes to be written
	 * @param off    zero-relative starting offset of the bytes to be written
	 * @param len    number of bytes to be written
	 */
	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {

		if (closed) {
			throw new IOException("Cannot write to a closed output stream");
		}

		if (len == 0) {
			return;
		}

		// Can we write into buffer ?
		if (len <= (buffer.length - bufferCount)) {
			System.arraycopy(b, off, buffer, bufferCount, len);
			bufferCount += len;
			return;
		}

		// There is not enough space in buffer. Flush it ...
		flushToGZip();

		// ... and try again. Note, that bufferCount = 0 here !
		if (len <= (buffer.length - bufferCount)) {
			System.arraycopy(b, off, buffer, bufferCount, len);
			bufferCount += len;
			return;
		}

		// write direct to gzip
		writeToGZip(b, off, len);
	}

	/**
	 * Writes byte array to gzip output stream. Creates new <code>GZIPOutputStream</code>
	 * if not created yet. Also sets the "Content-Encoding" header.
	 */
	public void writeToGZip(final byte[] b, final int off, final int len) throws IOException {
		if (gzipstream == null) {
			gzipstream = new GZIPOutputStream(output);
			response.setHeader("Content-Encoding", "gzip");
		}
		gzipstream.write(b, off, len);

	}

	/**
	 * Returns <code>true</code> if this response stream been closed.
	 */
	public boolean closed() {
		return(this.closed);
	}

}
