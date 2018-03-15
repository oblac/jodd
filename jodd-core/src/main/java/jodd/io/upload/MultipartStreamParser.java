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

import jodd.io.FastByteArrayOutputStream;
import jodd.io.upload.impl.MemoryFileUploadFactory;
import jodd.util.ArraysUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Generic, serlvets-free multipart request input stream parser.
 */
public class MultipartStreamParser {

	protected FileUploadFactory fileUploadFactory;
	protected Map<String, String[]> requestParameters;
	protected Map<String, FileUpload[]> requestFiles;

	public MultipartStreamParser() {
		this(null);
	}

	public MultipartStreamParser(final FileUploadFactory fileUploadFactory) {
		this.fileUploadFactory = (fileUploadFactory == null ? new MemoryFileUploadFactory() : fileUploadFactory);
	}

	private boolean parsed;

	/**
	 * Sets the loaded flag that indicates that input stream is loaded and parsed.
	 * Throws an exception if stream already loaded.
	 */
	protected void setParsed() throws IOException {
		if (parsed) {
			throw new IOException("Multi-part request already parsed");
		}
		parsed = true;
	}

	/**
	 * Returns <code>true</code> if multi-part request is already loaded.
	 */
	public boolean isParsed() {
		return parsed;
	}

	// ---------------------------------------------------------------- load and extract

	protected void putFile(final String name, final FileUpload value) {
		if (requestFiles == null) {
			requestFiles = new HashMap<>();
		}

		FileUpload[] fileUploads = requestFiles.get(name);

		if (fileUploads != null) {
			fileUploads = ArraysUtil.append(fileUploads, value);
		} else {
			fileUploads = new FileUpload[] {value};
		}

		requestFiles.put(name, fileUploads);
	}

	protected void putParameters(final String name, final String[] values) {
		if (requestParameters == null) {
			requestParameters = new HashMap<>();
		}
		requestParameters.put(name, values);
	}

	protected void putParameter(final String name, final String value) {
		if (requestParameters == null) {
			requestParameters = new HashMap<>();
		}
		
		String[] params = requestParameters.get(name);

		if (params != null) {
			params = ArraysUtil.append(params, value);
		} else {
			params = new String[] {value};
		}

		requestParameters.put(name, params);
	}

	/**
	 * Extracts uploaded files and parameters from the request data.
	 */
	public void parseRequestStream(final InputStream inputStream, final String encoding) throws IOException {
		setParsed();

		MultipartRequestInputStream input = new MultipartRequestInputStream(inputStream);
		input.readBoundary();
		while (true) {
			FileUploadHeader header = input.readDataHeader(encoding);
			if (header == null) {
				break;
			}

			if (header.isFile) {
				String fileName = header.fileName;
				if (fileName.length() > 0) {
					if (header.contentType.indexOf("application/x-macbinary") > 0) {
						input.skipBytes(128);
					}
				}
				FileUpload newFile = fileUploadFactory.create(input);
				newFile.processStream();
				if (fileName.length() == 0) {
					// file was specified, but no name was provided, therefore it was not uploaded
					if (newFile.getSize() == 0) {
						newFile.size = -1;
					}
				}
				putFile(header.formFieldName, newFile);
			} else {
				// no file, therefore it is regular form parameter.
				FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
				input.copyAll(fbos);
				String value = encoding != null ? new String(fbos.toByteArray(), encoding) : new String(fbos.toByteArray());
				putParameter(header.formFieldName, value);
			}

			input.skipBytes(1);
			input.mark(1);

			// read byte, but may be end of stream
			int nextByte = input.read();
			if (nextByte == -1 || nextByte == '-') {
				input.reset();
				break;
			}
			input.reset();
		}
	}

	// ---------------------------------------------------------------- parameters


	/**
	 * Returns single value of a parameter. If parameter name is used for
	 * more then one parameter, only the first one will be returned.
	 *
	 * @return parameter value, or <code>null</code> if not found
	 */
	public String getParameter(final String paramName) {
		if (requestParameters == null) {
			return null;
		}
		String[] values = requestParameters.get(paramName);
		if ((values != null) && (values.length > 0)) {
			return values[0];
		}
		return null;
	}

	/**
	 * Returns the names of the parameters contained in this request.
	 */
	public Set<String> getParameterNames() {
		if (requestParameters == null) {
			return Collections.emptySet();
		}
		return requestParameters.keySet();
	}

	/**
	 * Returns all values all of the values the given request parameter has.
	 */
	public String[] getParameterValues(final String paramName) {
		if (requestParameters == null) {
			return null;
		}
		return requestParameters.get(paramName);
	}


	/**
	 * Returns uploaded file.
	 * @param paramName parameter name of the uploaded file
	 * @return uploaded file or <code>null</code> if parameter name not found
	 */
	public FileUpload getFile(final String paramName) {
		if (requestFiles == null) {
			return null;
		}
		FileUpload[] values = requestFiles.get(paramName);
		if ((values != null) && (values.length > 0)) {
			return values[0];
		}
		return null;
	}


	/**
	 * Returns all uploaded files the given request parameter has.
	 */
	public FileUpload[] getFiles(final String paramName) {
		if (requestFiles == null) {
			return null;
		}
		return requestFiles.get(paramName);
	}

	/**
	 * Returns parameter names of all uploaded files.
	 */
	public Set<String> getFileParameterNames() {
		if (requestFiles == null) {
			return Collections.emptySet();
		}
		return requestFiles.keySet();
	}

}
