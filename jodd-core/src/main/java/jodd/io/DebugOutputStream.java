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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Output stream used for debugging purposes.
 */
public class DebugOutputStream extends FilterOutputStream {

	// ---------------------------------------------------------------- ctors

	protected boolean passThrough = true;

	/**
	 * Output stream that debugs to system out.
	 */
	public DebugOutputStream() {
		super(System.out);
	}

	public DebugOutputStream(OutputStream out) {
		super(out);
	}

	public DebugOutputStream(boolean passThrough) {
		super(System.out);
		this.passThrough = passThrough;
	}

	public DebugOutputStream(OutputStream out, boolean passThrough) {
		super(out);
		this.passThrough = passThrough;
	}


	// ---------------------------------------------------------------- methods

	@Override
	public void close() throws IOException {
		super.close();
	}

	@Override
	public void flush() throws IOException {
		super.flush();
	}

	@Override
	public void write(int b) throws IOException {
		if (passThrough == true) {
			super.write(b);
		}
		dumpByte(b);
		System.out.println();
	}

	@Override
	public void write(byte b[]) throws IOException {
		super.write(b);
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		if (passThrough == true) {
			super.write(b, off, len);
		}
		int i = off;
		int count = len;
		while (count-- > 0) {
			dumpByte(b[i++]);
		}
		System.out.println();
	}


	/**
	 * Dumps single byte to output stream.
	 */
	protected void dumpByte(int b) {
		if (passThrough == true) {
			System.out.print('\t');
		}
		if (b < 0) {
			b += 128;
		}
		if (b < 0x10) {
			System.out.print('0');
		}

		System.out.print(' ');		
		System.out.print(Integer.toHexString(b).toUpperCase());
	}
}
