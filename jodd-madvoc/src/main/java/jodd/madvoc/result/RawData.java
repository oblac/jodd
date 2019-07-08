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

package jodd.madvoc.result;

import jodd.io.FileNameUtil;
import jodd.madvoc.MadvocException;
import jodd.madvoc.meta.RenderWith;
import jodd.util.StringPool;
import jodd.net.MimeTypes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Raw data to download.
 */
@RenderWith(RawActionResult.class)
public class RawData {

	protected final InputStream inputStream;
	protected final int length;

	protected String downloadFileName;
	protected String mimeType = MimeTypes.MIME_APPLICATION_OCTET_STREAM;

	public static RawData of(final byte[] bytes) {
		return new RawData(new ByteArrayInputStream(bytes), bytes.length);
	}

	public static RawData of(final File file) {
		return new RawData(createFileInputStream(file), (int) file.length()).downloadableAs(file.getName());
	}

	public RawData(final InputStream inputStream, final int length) {
		this.inputStream = inputStream;
		this.length = length;
	}

	/**
	 * Defines mime type by providing real mime type or just extension!
	 */
	public RawData as(final String mimeOrExtension) {
		if (mimeOrExtension.contains(StringPool.SLASH)) {
			this.mimeType = mimeOrExtension;
		}
		else {
			this.mimeType = MimeTypes.getMimeType(mimeOrExtension);
		}
		return this;
	}

	public RawData asHtml() {
		this.mimeType = MimeTypes.MIME_TEXT_HTML;
		return this;
	}
	public RawData asText() {
		this.mimeType = MimeTypes.MIME_TEXT_PLAIN;
		return this;
	}

	/**
	 * Defines download file name and mime type from the name extension.
	 */
	public RawData downloadableAs(final String downloadFileName) {
		this.downloadFileName = downloadFileName;
		this.mimeType = MimeTypes.getMimeType(FileNameUtil.getExtension(downloadFileName));
		return this;
	}


	// ---------------------------------------------------------------- getter

	public InputStream contentInputStream() {
		return inputStream;
	}

	public String mimeType() {
		return mimeType;
	}

	public String downloadFileName() {
		return downloadFileName;
	}

	public int contentLength() {
		return length;
	}

	private static FileInputStream createFileInputStream(final File file) {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException fis) {
			throw new MadvocException(fis);
		}
	}

}
