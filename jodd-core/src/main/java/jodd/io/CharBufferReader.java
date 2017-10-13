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

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * Reader that wraps a <code>CharBuffer</code>.
 */
public class CharBufferReader extends Reader {

	private final CharBuffer charBuffer;

	public CharBufferReader(CharBuffer charBuffer) {
		 // duplicate so to allow to move independently,
		 // but share the same underlying data.
		this.charBuffer = charBuffer.duplicate();
	}

	@Override
	public int read(char[] chars, int offset, int length) throws IOException {
		int read = Math.min(charBuffer.remaining(), length);
		charBuffer.get(chars, offset, read);
		return read;
	}

	@Override
	public int read() throws IOException {
		return charBuffer.position() < charBuffer.limit() ? charBuffer.get() : -1;
	}

	@Override
	public void close() throws IOException {
	}

}