// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.upload;

/**
 * {@link FileUpload} factory for handling uploaded files. Implementations may
 * handle uploaded files differently: to store them to memory, directly to disk
 * or something else.
 * @see jodd.servlet.upload.impl
 */
public interface FileUploadFactory {

	/**
	 * Creates new instance of {@link FileUpload uploaded file}.
	 */
	FileUpload create(MultipartRequestInputStream input);
}