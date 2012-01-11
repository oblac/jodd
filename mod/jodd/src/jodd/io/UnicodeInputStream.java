// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import jodd.JoddDefault;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * Unicode input stream detects and decodes BOM character.
 * Detects following BOMs: UTF-8, UTF-16BE, UTF-16LE, UTF-32BE, UTF-32LE.
 */
public class UnicodeInputStream extends InputStream {

	public static final int MAX_BOM_SIZE = 4;

	private PushbackInputStream internalInputStream;
	private boolean initialized;
	private int BOMSize = -1;
	private String encoding;
	private String defaultEncoding;

	/**
	 * Creates new unicode stream with default UTF-8 encoding.
	 */
	public UnicodeInputStream(InputStream in) {
		this(in, JoddDefault.encoding);
	}

	/**
	 * Creates new unicode stream with provided default encoding.
	 * Default encoding is the one used if BOM is missing or not recognized.
	 */
	public UnicodeInputStream(InputStream in, String defaultEncoding) {
		internalInputStream = new PushbackInputStream(in, MAX_BOM_SIZE);
		this.defaultEncoding = defaultEncoding;
	}

	/**
	 * Returns default encoding.
	 */
	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	/**
	 * Returns detected encoding. If stream is not read yet,
	 * it will be {@link #init() initalized} first.
	 */
	public String getEncoding() {
		if (!initialized) {
			try {
				init();
			} catch (IOException ioex) {
				throw new IllegalStateException(ioex);
			}
		}
		return encoding;
	}

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

		byte bom[] = new byte[MAX_BOM_SIZE];
		int n = internalInputStream.read(bom, 0, bom.length);
		int unread;

		if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
			encoding = "UTF-32BE";
			unread = n - 4;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
			encoding = "UTF-32LE";
			unread = n - 4;
		} else if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
			encoding = "UTF-8";
			unread = n - 3;
		} else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
			encoding = "UTF-16BE";
			unread = n - 2;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
			encoding = "UTF-16LE";
			unread = n - 2;
		} else {
			encoding = defaultEncoding;		// BOM not found, unread all bytes
			unread = n;
		}

		BOMSize = MAX_BOM_SIZE - unread;
		if (unread > 0) {
			internalInputStream.unread(bom, (n - unread), unread);
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
	 */
	public int getBOMSize() {
		return BOMSize;
	}

}