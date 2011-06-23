// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Consumes a stream.
 * For any <code>Process</code>, the input and error streams must read even
 * if the data written to these streams is not used by the application.
 * The generally accepted solution for this problem is a stream gobbler thread
 * that does nothing but consume data from an input stream until stopped.
 */
public class StreamGobbler extends Thread {

	protected final InputStream is;
	protected final String type;
	protected final OutputStream os;

	public StreamGobbler(InputStream is, String type) {
		this(is, type, null);
	}

	public StreamGobbler(InputStream is) {
		this(is, null, null);
	}

	public StreamGobbler(InputStream is, OutputStream output) {
		this(is, null, output);
	}

	public StreamGobbler(InputStream is, String type, OutputStream output) {
		this.is = is;
		this.type = type;
		this.os = output;
	}

	@Override
	public void run() {
		try {
			PrintWriter pw = null;
			if (os != null) {
				pw = new PrintWriter(os);
			}

			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (pw != null) {
					if (type != null) {
						pw.print(type + "> ");
					}
					pw.println(line);
				}
			}
			if (pw != null) {
				pw.flush();
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}