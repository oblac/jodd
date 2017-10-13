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
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * Unicode input stream for detecting UTF encodings and reading BOM characters.
 * Detects following BOMs:
 * <ul>
 * <li>UTF-8</li>
 * <li>UTF-16BE</li>
 * <li>UTF-16LE</li>
 * <li>UTF-32BE</li>
 * <li>UTF-32LE</li>
 * </ul>
 */
public class UnicodeInputStream extends InputStream {

	public static final int MAX_BOM_SIZE = 4;

	private final PushbackInputStream internalInputStream;
	private boolean initialized;
	private int BOMSize = -1;
	private String encoding;
	private final String targetEncoding;

	/**
	 * Creates new unicode stream. It works in two modes: detect mode and read mode.
	 * <p>
	 * Detect mode is active when target encoding is not specified.
	 * In detect mode, it tries to detect encoding from BOM if exist.
	 * If BOM doesn't exist, encoding is not detected.
	 * <p>
	 * Read mode is active when target encoding is set. Then this stream reads
	 * optional BOM for given encoding. If BOM doesn't exist, nothing is skipped.
	 */
	public UnicodeInputStream(InputStream in, String targetEncoding) {
		internalInputStream = new PushbackInputStream(in, MAX_BOM_SIZE);
		this.targetEncoding = targetEncoding;
	}

	/**
	 * Returns detected UTF encoding or <code>null</code> if no UTF encoding has been detected (i.e. no BOM).
	 * If stream is not read yet, it will be {@link #init() initalized} first.
	 */
	public String getDetectedEncoding() {
		if (!initialized) {
			try {
				init();
			} catch (IOException ioex) {
				throw new IllegalStateException(ioex);
			}
		}
		return encoding;
	}

	public static final byte[] BOM_UTF32_BE = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xFE, (byte) 0xFF};
	public static final byte[] BOM_UTF32_LE = new byte[]{(byte) 0xFF, (byte) 0xFE, (byte) 0x00, (byte) 0x00};
	public static final byte[] BOM_UTF8 = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
	public static final byte[] BOM_UTF16_BE = new byte[]{(byte) 0xFE, (byte) 0xFF};
	public static final byte[] BOM_UTF16_LE = new byte[]{(byte) 0xFF, (byte) 0xFE};

	/**
	 * Detects and decodes encoding from BOM character.
	 * Reads ahead four bytes and check for BOM marks.
	 * Extra bytes are unread back to the stream, so only
	 * BOM bytes are skipped.
	 */
	protected void init() throws IOException {
		if (initialized) {
			return;
		}

		if (targetEncoding == null) {

			// DETECT MODE

			byte[] bom = new byte[MAX_BOM_SIZE];
			int n = internalInputStream.read(bom, 0, bom.length);
			int unread;

			if ((bom[0] == BOM_UTF32_BE[0]) && (bom[1] == BOM_UTF32_BE[1]) && (bom[2] == BOM_UTF32_BE[2]) && (bom[3] == BOM_UTF32_BE[3])) {
				encoding = "UTF-32BE";
				unread = n - 4;
			} else if ((bom[0] == BOM_UTF32_LE[0]) && (bom[1] == BOM_UTF32_LE[1]) && (bom[2] == BOM_UTF32_LE[2]) && (bom[3] == BOM_UTF32_LE[3])) {
				encoding = "UTF-32LE";
				unread = n - 4;
			} else if ((bom[0] == BOM_UTF8[0]) && (bom[1] == BOM_UTF8[1]) && (bom[2] == BOM_UTF8[2])) {
				encoding = "UTF-8";
				unread = n - 3;
			} else if ((bom[0] == BOM_UTF16_BE[0]) && (bom[1] == BOM_UTF16_BE[1])) {
				encoding = "UTF-16BE";
				unread = n - 2;
			} else if ((bom[0] == BOM_UTF16_LE[0]) && (bom[1] == BOM_UTF16_LE[1])) {
				encoding = "UTF-16LE";
				unread = n - 2;
			} else {
				// BOM not found, unread all bytes
				unread = n;
			}

			BOMSize = MAX_BOM_SIZE - unread;

			if (unread > 0) {
				internalInputStream.unread(bom, (n - unread), unread);
			}
		} else {

			// READ MODE

			byte[] bom = null;

			if (targetEncoding.equals("UTF-8")) {
				bom = BOM_UTF8;
			} else if (targetEncoding.equals("UTF-16LE")) {
				bom = BOM_UTF16_LE;
			} else if (targetEncoding.equals("UTF-16BE") || targetEncoding.equals("UTF-16")) {
				bom = BOM_UTF16_BE;
			} else if (targetEncoding.equals("UTF-32LE")) {
				bom = BOM_UTF32_LE;
			} else if (targetEncoding.equals("UTF-32BE") || targetEncoding.equals("UTF-32")) {
				bom = BOM_UTF32_BE;
			} else {
				// no UTF encoding, no BOM
			}

			if (bom != null) {
				byte[] fileBom = new byte[bom.length];
				int n = internalInputStream.read(fileBom, 0, bom.length);

				boolean bomDetected = true;
				for (int i = 0; i < n; i++) {
					if (fileBom[i] != bom[i]) {
						bomDetected = false;
						break;
					}
				}

				if (!bomDetected) {
					internalInputStream.unread(fileBom, 0, fileBom.length);
				}
			}
		}

		initialized = true;
	}

	/**
	 * Closes input stream. If stream was not used, encoding
	 * will be unavailable.
	 */
	@Override
	public void close() throws IOException {
		internalInputStream.close();
	}

	/**
	 * Reads byte from the stream.
	 */
	@Override
	public int read() throws IOException {
		init();
		return internalInputStream.read();
	}

	/**
	 * Returns BOM size in bytes.
	 * Returns <code>-1</code> if BOM not found.
	 */
	public int getBOMSize() {
		return BOMSize;
	}

}