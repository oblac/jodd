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

package jodd.mail.att;

import jodd.mail.EmailAttachment;
import jodd.mail.MailException;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;

/**
 * <code>InputStream</code> {@link EmailAttachment email attachment}.
 */
public class InputStreamAttachment extends EmailAttachment {

	protected final InputStream inputStream;
	protected final String contentType;

	public InputStreamAttachment(InputStream inputStream, String contentType, String name, String contentId) {
		super(name, contentId);
		this.inputStream = inputStream;
		this.contentType = contentType;
	}

	/**
	 * Returns <code>ByteArrayDataSource</code>.
	 */
	@Override
	public DataSource getDataSource() {
		try {
			return new ByteArrayDataSource(inputStream, contentType);
		} catch (IOException ioex) {
			throw new MailException(ioex);
		}
	}

	/**
	 * Returns content type.
	 */
	public String getContentType() {
		return contentType;
	}

}