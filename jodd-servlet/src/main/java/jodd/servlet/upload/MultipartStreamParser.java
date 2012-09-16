// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.upload;

import jodd.io.FastByteArrayOutputStream;
import jodd.servlet.upload.impl.MemoryFileUploadFactory;
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

	public MultipartStreamParser(FileUploadFactory fileUploadFactory) {
		this.fileUploadFactory = (fileUploadFactory == null ? new MemoryFileUploadFactory() : fileUploadFactory);
	}

	private boolean loaded;

	/**
	 * Sets the loaded flag that indicates that input stream is loaded and parsed.
	 * Throws an exception if stream already loaded.
	 */
	protected void setLoaded() throws IOException {
		if (loaded == true) {
			throw new IOException("Multi-part request already parsed.");
		}
		loaded = true;
	}

	/**
	 * Returns <code>true</code> if multi-part request is already loaded.
	 */
	public boolean isLoaded() {
		return loaded;
	}


	// ---------------------------------------------------------------- load and extract

	protected void putFile(String name, FileUpload value) {
		if (requestFiles == null) {
			requestFiles = new HashMap<String, FileUpload[]>();
		}

		FileUpload[] fileUploads = requestFiles.get(name);

		if (fileUploads != null) {
			fileUploads = ArraysUtil.append(fileUploads, value);
		} else {
			fileUploads = new FileUpload[] {value};
		}

		requestFiles.put(name, fileUploads);
	}

	protected void putParameters(String name, String[] values) {
		if (requestParameters == null) {
			requestParameters = new HashMap<String, String[]>();
		}
		requestParameters.put(name, values);
	}

	protected void putParameter(String name, String value) {
		if (requestParameters == null) {
			requestParameters = new HashMap<String, String[]>();
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
	public void parseRequestStream(InputStream inputStream, String encoding) throws IOException {
		setLoaded();

		MultipartRequestInputStream input = new MultipartRequestInputStream(inputStream);
		input.readBoundary();
		while (true) {
			FileUploadHeader header = input.readDataHeader(encoding);
			if (header == null) {
				break;
			}

			if (header.isFile == true) {
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
	public String getParameter(String paramName) {
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
	public String[] getParameterValues(String paramName) {
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
	public FileUpload getFile(String paramName) {
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
	public FileUpload[] getFiles(String paramName) {
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
