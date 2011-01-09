// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.filter;

import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;



public class GzipResponseStream extends ServletOutputStream {

	/**
	 * Construct a servlet output stream associated with the specified Response.
	 *
	 * @param response The associated response
	 *
	 * @exception IOException
	 */
	public GzipResponseStream(HttpServletResponse response) throws IOException {
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
	 * Set the compressionThreshold number and create buffer for this size
	 */
	protected void setBuffer(int threshold) {
		compressionThreshold = threshold;
		buffer = new byte[compressionThreshold];
	}

	/**
	 * Close this output stream, causing any buffered data to be flushed and any
	 * further output data to throw an IOException.
	 *
	 * @exception IOException
	 */
	@Override
	public void close() throws IOException {
		if (closed == true) {
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
	 * Flush any buffered data for this output stream, which also causes the
	 * response to be committed.
	 *
	 * @exception IOException
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
	 * Write the specified byte to our output stream.
	 *
	 * @param b      The byte to be written
	 *
	 * @exception IOException
	 *                   if an input/output error occurs
	 */
	@Override
	public void write(int b) throws IOException {

		if (closed) {
			throw new IOException("Cannot write to a closed output stream");
		}
		if (bufferCount >= buffer.length) {
			flushToGZip();
		}
		buffer[bufferCount++] = (byte) b;
	}


	/**
	 * Write <code>b.length</code> bytes from the specified byte array to our
	 * output stream.
	 *
	 * @param b      byte array to be written
	 *
	 * @exception IOException
	 *                   if an input/output error occurs
	 */
	@Override
	public void write(byte b[]) throws IOException {
		write(b, 0, b.length);
	}


	/**
	 * Write <code>len</code> bytes from the specified byte array, starting at
	 * the specified offset, to our output stream.
	 *
	 * @param b      byte array containing the bytes to be written
	 * @param off    zero-relative starting offset of the bytes to be written
	 * @param len    number of bytes to be written
	 *
	 * @exception IOException
	 *                   if an input/output error occurs
	 */
	@Override
	public void write(byte b[], int off, int len) throws IOException {

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

	public void writeToGZip(byte b[], int off, int len) throws IOException {

		if (gzipstream == null) {
			gzipstream = new GZIPOutputStream(output);
			response.addHeader("Content-Encoding", "gzip");
		}
		gzipstream.write(b, off, len);

	}


	/**
	 * Has this response stream been closed?
	 *
	 * @return true if stream has been closed
	 */
	public boolean closed() {
		return(this.closed);
	}

}
