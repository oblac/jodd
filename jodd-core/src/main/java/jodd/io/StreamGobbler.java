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

import jodd.util.StringPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Consumes a stream.
 * For any <code>Process</code>, the input and error streams must read even
 * if the data written to these streams is not used by the application.
 * The generally accepted solution for this problem is a stream gobbler thread
 * that does nothing but consume data from an input stream until stopped.
 */
public class StreamGobbler extends Thread {

	protected final InputStream is;
	protected final String prefix;
	protected final OutputStream out;
	protected final Object lock = new Object();
	protected boolean end = false;

	public StreamGobbler(InputStream is) {
		this(is, null, null);
	}

	public StreamGobbler(InputStream is, OutputStream output) {
		this(is, output, null);
	}

	public StreamGobbler(InputStream is, OutputStream output, String prefix) {
		this.is = is;
		this.prefix = prefix;
		this.out = output;
	}

	@Override
	public void run() {
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		try {
			String line;
			while ((line = br.readLine()) != null) {
				if (out != null) {
					if (prefix != null) {
						out.write(prefix.getBytes());
					}
					out.write(line.getBytes());
					out.write(StringPool.BYTES_NEW_LINE);
				}
			}
		}
		catch (IOException ioe) {
			if (out != null) {
				ioe.printStackTrace(new PrintStream(out));
			}
		}
		finally {
			if (out != null) {
				try {
					out.flush();
				}
				catch (IOException ignore) {
				}
			}
			try {
				br.close();
			}
			catch (IOException ignore) {
			}
		}

		synchronized (lock) {
			lock.notifyAll();
			end = true;
		}
	}

	/**
	 * Waits for gobbler to end.
	 */
	public void waitFor() {
		try {
			synchronized (lock) {
				if (!end) {
					lock.wait();
				}
			}
		}
		catch (InterruptedException ignore) {
			Thread.currentThread().interrupt();
		}
	}

}