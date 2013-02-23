// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.upload;

/**
 * {@link FileUpload} factory for handling uploaded files. Implementations may
 * handle uploaded files differently: to store them to memory, directly to disk
 * or something else.
 */
public interface FileUploadFactory {

	/**
	 * Creates new instance of {@link FileUpload uploaded file}.
	 */
	FileUpload create(MultipartRequestInputStream input);
}