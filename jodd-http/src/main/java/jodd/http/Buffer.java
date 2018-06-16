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

package jodd.http;

import jodd.http.up.Uploadable;
import jodd.io.StreamUtil;
import jodd.util.StringPool;
import jodd.buffer.FastByteBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.LinkedList;

/**
 * Holds request/response content until it is actually send.
 * File content (i.e. {@link jodd.http.up.Uploadable}) is
 * <b>not</b> read until it is really used.
 */
public class Buffer {

	protected LinkedList<Object> list = new LinkedList<>();
	protected FastByteBuffer last;
	protected int size;

	/**
	 * Appends string content to buffer.
	 */
	public Buffer append(final String string) {
		ensureLast();

		try {
			byte[] bytes = string.getBytes(StringPool.ISO_8859_1);

			last.append(bytes);

			size += bytes.length;
		} catch (UnsupportedEncodingException ignore) {
		}

		return this;
	}

	/**
	 * Appends a char.
	 */
	public Buffer append(final char c) {
		append(Character.toString(c));
		return this;
	}

	/**
	 * Appends a number.
	 */
	public Buffer append(final int number) {
		append(Integer.toString(number));
		return this;
	}

	/**
	 * Appends {@link jodd.http.up.Uploadable} to buffer.
	 */
	public Buffer append(final Uploadable uploadable) {
		list.add(uploadable);
		size += uploadable.getSize();
		last = null;
		return this;
	}

	/**
	 * Appends other buffer to this one.
	 */
	public Buffer append(final Buffer buffer) {
		if (buffer.list.isEmpty()) {
			// nothing to append
			return buffer;
		}
		list.addAll(buffer.list);
		last = buffer.last;
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
	 * Ensures that last buffer exist.
	 */
	private void ensureLast() {
		if (last == null) {
			last = new FastByteBuffer();
			list.add(last);
		}
	}

	// ---------------------------------------------------------------- write

	/**
	 * Writes content to the writer.
	 */
	public void writeTo(final Writer writer) throws IOException {
		for (Object o : list) {
			if (o instanceof FastByteBuffer) {
				FastByteBuffer fastByteBuffer = (FastByteBuffer) o;

				byte[] array = fastByteBuffer.toArray();

				writer.write(new String(array, StringPool.ISO_8859_1));
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
	public void writeTo(final OutputStream out) throws IOException {
		for (Object o : list) {
			if (o instanceof FastByteBuffer) {
				FastByteBuffer fastByteBuffer = (FastByteBuffer) o;

				out.write(fastByteBuffer.toArray());
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
	public void writeTo(final OutputStream out, final HttpProgressListener progressListener) throws IOException {

		// start

		final int size = size();
		final int callbackSize = progressListener.callbackSize(size);
		int count = 0;		// total count
		int step = 0;		// step is offset in current chunk

		progressListener.transferred(count);

		// loop

		for (Object o : list) {
			if (o instanceof FastByteBuffer) {
				FastByteBuffer fastByteBuffer = (FastByteBuffer) o;
				byte[] bytes = fastByteBuffer.toArray();

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
						StreamUtil.copy(inputStream, out, chunk);

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