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

package jodd.io.upload;

import jodd.io.FileNameUtil;
import jodd.util.StringPool;

/**
 * Parses file upload header.
 */
public class FileUploadHeader {

	String dataHeader;
	String formFieldName;

	String formFileName;
	String path;
	String fileName;

	boolean isFile;
	String contentType;
	String mimeType;
	String mimeSubtype;
	String contentDisposition;


	FileUploadHeader(final String dataHeader) {
		this.dataHeader = dataHeader;
		isFile = dataHeader.indexOf("filename") > 0;
		formFieldName = getDataFieldValue(dataHeader, "name");
		if (isFile) {
			formFileName = getDataFieldValue(dataHeader, "filename");
			if (formFileName == null) {
				return;
			}
			if (formFileName.length() == 0) {
				path = StringPool.EMPTY;
				fileName = StringPool.EMPTY;
			}
			int ls = FileNameUtil.indexOfLastSeparator(formFileName);
			if (ls == -1) {
				path = StringPool.EMPTY;
				fileName = formFileName;
			} else {
				path = formFileName.substring(0, ls);
				fileName = formFileName.substring(ls + 1);
			}
			if (fileName.length() > 0) {
				this.contentType = getContentType(dataHeader);
				mimeType = getMimeType(contentType);
				mimeSubtype = getMimeSubtype(contentType);
				contentDisposition = getContentDisposition(dataHeader);
			}
		}
	}

	// ---------------------------------------------------------------- utilities

	/**
	 * Gets value of data field or <code>null</code> if field not found.
	 */
	private String getDataFieldValue(final String dataHeader, final String fieldName) {
		String value = null;
		String token = String.valueOf((new StringBuffer(String.valueOf(fieldName))).append('=').append('"'));
		int pos = dataHeader.indexOf(token);
		if (pos > 0) {
			int start = pos + token.length();
			int end = dataHeader.indexOf('"', start);
			if ((start > 0) && (end > 0)) {
				value = dataHeader.substring(start, end);
			}
		}
		return value;
	}

	/**
	 * Strips content type information from requests data header.
	 * @param dataHeader data header string
	 * @return content type or an empty string if no content type defined
	 */
	private String getContentType(final String dataHeader) {
		String token = "Content-Type:";
		int start = dataHeader.indexOf(token);
		if (start == -1) {
			return StringPool.EMPTY;
		}
		start += token.length();
		return dataHeader.substring(start);
	}

	private String getContentDisposition(final String dataHeader) {
		int start = dataHeader.indexOf(':') + 1;
		int end = dataHeader.indexOf(';');
		return dataHeader.substring(start, end);
	}

	private String getMimeType(final String ContentType) {
		int pos = ContentType.indexOf('/');
		if (pos == -1) {
			return ContentType;
		}
		return ContentType.substring(1, pos);
	}

	private String getMimeSubtype(final String ContentType) {
		int start = ContentType.indexOf('/');
		if (start == -1) {
			return ContentType;
		}
		start++;
		return ContentType.substring(start);
	}


	// ---------------------------------------------------------------- public interface

	/**
	 * Returns <code>true</code> if uploaded data are correctly marked as a file.
	 * This is true if header contains string 'filename'.
	 */
	public boolean isFile() {
		return isFile;
	}

	/**
	 * Returns form field name.
	 */
	public String getFormFieldName() {
		return formFieldName;
	}

	/**
	 * Returns complete file name as specified at client side.
	 */
	public String getFormFilename() {
		return formFileName;
	}

	/**
	 * Returns file name (base name and extension, without full path data).
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Returns uploaded content type. It is usually in the following form:<br>
	 * mime_type/mime_subtype.
	 *
	 * @see #getMimeType()
	 * @see #getMimeSubtype()
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Returns file types MIME.
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Returns file sub type MIME.
	 */
	public String getMimeSubtype() {
		return mimeSubtype;
	}

	/**
	 * Returns content disposition. Usually it is 'form-data'.
	 */
	public String getContentDisposition() {
		return contentDisposition;
	}
}
