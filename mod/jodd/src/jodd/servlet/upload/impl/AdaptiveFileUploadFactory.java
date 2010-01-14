// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.upload.impl;

import jodd.servlet.upload.FileUploadFactory;
import jodd.servlet.upload.FileUpload;
import jodd.servlet.upload.MultipartRequestInputStream;

import java.io.File;

/**
 *
 * Factory for {@link AdaptiveFileUpload}.
 */
public class AdaptiveFileUploadFactory implements FileUploadFactory {

	protected int memoryThreshold = 8192;
	protected File uploadPath;
	protected int maxFileSize = 102400;
	protected boolean breakOnError;
	protected String[] fileExtensions;
	protected boolean allowFileExtensions = true;

	/**
	 * {@inheritDoc}
	 */
	public FileUpload create(MultipartRequestInputStream input) {
		return new AdaptiveFileUpload(input, memoryThreshold, uploadPath, maxFileSize, breakOnError, fileExtensions, allowFileExtensions);
	}

	// ---------------------------------------------------------------- setters

	public int getMemoryThreshold() {
		return memoryThreshold;
	}
	/**
	 * Specifies per file memory limit for keeping uploaded files in the memory.
	 */
	public void setMemoryThreshold(int memoryThreshold) {
		if (memoryThreshold >= 0) {
			this.memoryThreshold = memoryThreshold;
		}
	}

	public AdaptiveFileUploadFactory memoryThreshold(int memoryThreshold) {
		setMemoryThreshold(memoryThreshold);
		return this;
	}



	public File getUploadPath() {
		return uploadPath;
	}

	public void setUploadPath(File uploadPath) {
		this.uploadPath = uploadPath;
	}

	/**
	 * Specifies upload path.
	 */
	public AdaptiveFileUploadFactory uploadPath(File uploadPath) {
		this.uploadPath = uploadPath;
		return this;
	}


	public int getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	/**
	 * Sets maximum file upload size. Setting to -1 will disable this constraint.
	 */
	public AdaptiveFileUploadFactory maxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
		return this;
	}


	public boolean isBreakOnError() {
		return breakOnError;
	}

	public void setBreakOnError(boolean breakOnError) {
		this.breakOnError = breakOnError;
	}

	/**
	 * Specifies if upload should break on error.
	 */
	public AdaptiveFileUploadFactory breakOnError(boolean breakOnError) {
		this.breakOnError = breakOnError;
		return this;
	}

	/**
	 * Allow or disallow set of file extensions. Only one rule can be active at time,
	 * which means user can only specify extensions that are either allowed or disallowed.
	 * Setting this value to <code>null</code> will turn this feature off.
	 */
	public AdaptiveFileUploadFactory setFileExtensions(String[] fileExtensions, boolean allow) {
		this.fileExtensions = fileExtensions;
		this.allowFileExtensions = allow;
		return this;
	}

}
