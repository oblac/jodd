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

package jodd.io;

import jodd.core.JoddCore;

/**
 * {@link FileUtil File utilities} parameters.
 */
public class FileUtilParams implements Cloneable {

	protected boolean preserveDate = true;				// should destination file have the same timestamp as source
	protected boolean overwrite = true;					// overwrite existing destination
	protected boolean createDirs = true;				// create missing subdirectories of destination
	protected boolean recursive = true;					// use recursive directory copying and deleting
	protected boolean continueOnError = true;			// don't stop on error and continue job as much as possible
	protected String encoding = JoddCore.encoding;		// default encoding for reading/writing strings


	public boolean isPreserveDate() {
		return preserveDate;
	}
	public FileUtilParams setPreserveDate(boolean preserveDate) {
		this.preserveDate = preserveDate;
		return this;
	}

	public boolean isOverwrite() {
		return overwrite;
	}
	public FileUtilParams setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
		return this;
	}

	public boolean isCreateDirs() {
		return createDirs;
	}
	public FileUtilParams setCreateDirs(boolean createDirs) {
		this.createDirs = createDirs;
		return this;
	}

	public boolean isRecursive() {
		return recursive;
	}
	public FileUtilParams setRecursive(boolean recursive) {
		this.recursive = recursive;
		return this;
	}

	public boolean isContinueOnError() {
		return continueOnError;
	}
	public FileUtilParams setContinueOnError(boolean continueOnError) {
		this.continueOnError = continueOnError;
		return this;
	}


	public String getEncoding() {
		return encoding;
	}
	public FileUtilParams setEncoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	// ------------------------------------------------------------ clone

	@Override
	public FileUtilParams clone() throws CloneNotSupportedException {
		return (FileUtilParams) super.clone();
	}

}