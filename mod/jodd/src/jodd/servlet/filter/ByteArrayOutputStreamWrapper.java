// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.filter;

import jodd.io.FastByteArrayOutputStream;

import java.io.OutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

/**
 * Implementation of ServletOutputStream that allows the filter to hold the
 * Response content for insertion into the cache.
 */
public class ByteArrayOutputStreamWrapper extends ServletOutputStream {
	protected OutputStream intStream;
	protected FastByteArrayOutputStream baStream;
	protected boolean finallized;
	protected boolean flushOnFinalizeOnly = true;

	public ByteArrayOutputStreamWrapper(OutputStream outStream) {
		intStream = outStream;
		baStream = new FastByteArrayOutputStream();
	}

	public ByteArrayOutputStreamWrapper() {
		intStream = System.out;
		baStream = new FastByteArrayOutputStream();
	}

	public FastByteArrayOutputStream getByteArrayStream() {
		return baStream;
	}

	public void setFinallized() {
		finallized = true;
	}

	public boolean isFinallized() {
		return finallized;
	}


	@Override
	public void write(int i) throws IOException {
		baStream.write(i);
	}

	@Override
	public void close() throws IOException {
		if (finallized) {
			processStream();
			intStream.close();
		}
	}
	public void reset() {
		baStream.reset();
	}

	@Override
	public void flush() throws IOException {
		if (baStream.size() != 0) {
			if (!flushOnFinalizeOnly || finallized) {
				processStream();
				baStream = new FastByteArrayOutputStream();
			}
		}
	}

	protected void processStream() throws IOException {
		intStream.write(baStream.toByteArray());
		intStream.flush();
	}

	public void clear() {
		baStream = new FastByteArrayOutputStream();
	}
}

