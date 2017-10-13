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

import jodd.madvoc.MadvocException;
import jodd.madvoc.meta.RenderWith;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Download data for raw results.
 */
@RenderWith(RawResult.class)
public class RawDownload extends RawResultData {

	public RawDownload(InputStream inputStream, String downloadFileName, String mimeType, int length) {
		super(inputStream, downloadFileName, mimeType, length);
	}

	public RawDownload(byte[] bytes, String downloadFileName, String mimeType) {
		super(new ByteArrayInputStream(bytes), downloadFileName, mimeType, bytes.length);
	}

	public RawDownload(byte[] bytes, String downloadFileName) {
		this(bytes, downloadFileName, null);
	}

	public RawDownload(File file, String downloadFileName, String mimeType) {
		super(createFileInputStream(file), downloadFileName, mimeType, (int) file.length());
	}

	public RawDownload(File file, String mimeType) {
		super(createFileInputStream(file), file.getName(), mimeType, (int) file.length());
	}

	public RawDownload(File file) {
		this(file, null);
	}

	private static FileInputStream createFileInputStream(File file) {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException fis) {
			throw new MadvocException(fis);
		}
	}

}
