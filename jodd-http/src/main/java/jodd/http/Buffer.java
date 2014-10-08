// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.http.up.Uploadable;
import jodd.io.StreamUtil;
import jodd.util.StringPool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.LinkedList;

/**
 * Holds request/response content until it is actually send.
 * File content is <b>not</b> read until it is used.
 */
public class Buffer {

	protected LinkedList<Object> list = new LinkedList<Object>();
	protected StringBuilder lastString;
	protected int size;

	/**
	 * Appends string content to buffer.
	 */
	public Buffer append(String string) {
		ensureLastString();
		lastString.append(string);
		size += string.length();
		return this;
	}

	/**
	 * Appends a char.
	 */
	public Buffer append(char c) {
		ensureLastString();
		lastString.append(c);
		size ++;
		return this;
	}

	public Buffer append(int number) {
		append(Integer.toString(number));
		return this;
	}

	/**
	 * Appends {@link jodd.http.up.Uploadable} to buffer.
	 */
	public Buffer append(Uploadable uploadable) {
		list.add(uploadable);
		size += uploadable.getSize();
		lastString = null;
		return this;
	}

	/**
	 * Appends other buffer to this one.
	 */
	public Buffer append(Buffer buffer) {
		if (buffer.list.size() == 0) {
			// nothing to append
			return buffer;
		}
		list.addAll(buffer.list);
		lastString = buffer.lastString;
		size += buffer.size;
		return this;
	}

	/**
	 * Returns buffer size.
	 */
	public int size() {
		return size;
	}

	/**
	 * Ensures that last string builder exist.
	 */
	private void ensureLastString() {
		if (lastString == null) {
			lastString = new StringBuilder();
			list.add(lastString);
		}
	}

	// ---------------------------------------------------------------- write

	/**
	 * Writes content to the writer.
	 */
	public void writeTo(Writer writer) throws IOException {
		for (Object o : list) {
			if (o instanceof StringBuilder) {
				StringBuilder sb = (StringBuilder) o;

				writer.write(sb.toString());
			}
			else if (o instanceof Uploadable) {
				Uploadable uploadable = (Uploadable) o;

				InputStream inputStream = uploadable.openInputStream();

				try {
					StreamUtil.copy(inputStream, writer, StringPool.ISO_8859_1);
				}
				finally {
					StreamUtil.close(inputStream);
				}
			}
		}
	}

	/**
	 * Writes content to the output stream.
	 */
	public void writeTo(OutputStream out) throws IOException {
		for (Object o : list) {
			if (o instanceof StringBuilder) {
				StringBuilder sb = (StringBuilder) o;

				out.write(sb.toString().getBytes(StringPool.ISO_8859_1));
			}
			else if (o instanceof Uploadable) {
				Uploadable uploadable = (Uploadable) o;

				InputStream inputStream = uploadable.openInputStream();

				try {
					StreamUtil.copy(inputStream, out);
				}
				finally {
					StreamUtil.close(inputStream);
				}
			}
		}
	}

	/**
	 * Writes content to the output stream, using progress listener to track the sending progress.
	 */
	public void writeTo(OutputStream out, HttpProgressListener progressListener) throws IOException {

		// start

		final int size = size();
		final int callbackSize = progressListener.callbackSize(size);
		int count = 0;		// total count
		int step = 0;		// step is offset in current chunk

		progressListener.transferred(count);

		// loop

		for (Object o : list) {
			if (o instanceof StringBuilder) {
				StringBuilder sb = (StringBuilder) o;
				byte[] bytes = sb.toString().getBytes(StringPool.ISO_8859_1);

				int offset = 0;

				while (offset < bytes.length) {
					// calc the remaining sending chunk size
					int chunk = callbackSize - step;

					// check if this chunk size fits the bytes array
					if (offset + chunk > bytes.length) {
						chunk = bytes.length - offset;
					}

					// writes the chunk
					out.write(bytes, offset, chunk);

					offset += chunk;
					step += chunk;
					count += chunk;

					// listener
					if (step >= callbackSize) {
						progressListener.transferred(count);
						step -= callbackSize;
					}
				}
			}
			else if (o instanceof Uploadable) {
				Uploadable uploadable = (Uploadable) o;

				InputStream inputStream = uploadable.openInputStream();

				int remaining = uploadable.getSize();

				try {
					while (remaining > 0) {
						// calc the remaining sending chunk size
						int chunk = callbackSize - step;

						// check if this chunk size fits the remaining size
						if (chunk > remaining) {
							chunk = remaining;
						}

						// writes remaining chunk
						StreamUtil.copy(inputStream, out, remaining);

						remaining -= chunk;
						step += chunk;
						count += chunk;

						// listener
						if (step >= callbackSize) {
							progressListener.transferred(count);
							step -= callbackSize;
						}
					}
				}
				finally {
					StreamUtil.close(inputStream);
				}
			}
		}

		// end

		if (step != 0) {
			progressListener.transferred(count);
		}
	}

}