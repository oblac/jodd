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