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

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

/**
 * Appendable writer adapter.
 */
public class AppendableWriter extends Writer {

	private final Appendable appendable;
	private final boolean flushable;
	private boolean closed;

	public AppendableWriter(final Appendable appendable) {
		this.appendable = appendable;
		this.flushable = appendable instanceof Flushable;
		this.closed = false;
	}

	@Override
	public void write(final char[] cbuf, final int off, final int len) throws IOException {
		checkNotClosed();
		appendable.append(CharBuffer.wrap(cbuf), off, off + len);
	}

	@Override
	public void write(final int c) throws IOException {
		checkNotClosed();
		appendable.append((char) c);
	}

	@Override
	public Writer append(final char c) throws IOException {
		checkNotClosed();
		appendable.append(c);
		return this;
	}

	@Override
	public Writer append(final CharSequence csq, final int start, final int end) throws IOException {
		checkNotClosed();
		appendable.append(csq, start, end);
		return this;
	}

	@Override
	public Writer append(final CharSequence csq) throws IOException {
		checkNotClosed();
		appendable.append(csq);
		return this;
	}

	@Override
	public void write(final String str, final int off, final int len) throws IOException {
		checkNotClosed();
		appendable.append(str, off, off + len);
	}

	@Override
	public void write(final String str) throws IOException {
		appendable.append(str);
	}

	@Override
	public void write(final char[] cbuf) throws IOException {
		appendable.append(CharBuffer.wrap(cbuf));
	}

	@Override
	public void flush() throws IOException {
		checkNotClosed();
		if (flushable) {
			((Flushable) appendable).flush();
		}
	}

	private void checkNotClosed() throws IOException {
		if (closed) {
			throw new IOException("Cannot write to closed writer " + this);
		}
	}

	@Override
	public void close() throws IOException {
		if (!closed) {
			flush();
			if (appendable instanceof Closeable) {
				((Closeable) appendable).close();
			}
			closed = true;
		}
	}
}