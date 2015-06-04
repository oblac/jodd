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

package jodd.http.up;

import java.io.IOException;
import java.io.InputStream;

/**
 * Common interface of uploaded content for {@link jodd.http.HttpBase#form() form parameters}.
 * All supported objects that can be uploaded using
 * the {@link jodd.http.HttpBase#form(String, Object)} has to
 * be wrapped with this interface.
 */
public interface Uploadable<T> {

	/**
	 * Returns the original content.
	 */
	public T getContent();

	/**
	 * Returns content bytes.
	 */
	public byte[] getBytes();

	/**
	 * Returns content file name.
	 * If <code>null</code>, the field's name will be used.
	 */
	public String getFileName();

	/**
	 * Returns MIME type. If <code>null</code>,
	 * MIME type will be determined from
	 * {@link #getFileName() file name's} extension.
	 */
	public String getMimeType();

	/**
	 * Returns size in bytes.
	 */
	public int getSize();

	/**
	 * Opens <code>InputStream</code>. User is responsible
	 * for closing it.
	 */
	public InputStream openInputStream() throws IOException;

}