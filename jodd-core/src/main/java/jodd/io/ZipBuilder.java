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

import jodd.util.StringPool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipOutputStream;

/**
 * ZIP builder class for building both files or in-memory zips.
 */
public class ZipBuilder {

	private final ZipOutputStream zos;
	private final File targetZipFile;
	private final ByteArrayOutputStream targetBaos;

	public static ZipBuilder createZipFile(File zipFile) throws IOException {
		return new ZipBuilder(zipFile);
	}
	public static ZipBuilder createZipFile(String zipFile) throws IOException {
		return new ZipBuilder(new File(zipFile));
	}

	public static ZipBuilder createZipInMemory() {
		return new ZipBuilder();
	}

	// ---------------------------------------------------------------- ctors

	protected ZipBuilder(File zipFile) throws IOException {
		if (!FileUtil.isExistingFile(zipFile)) {
			FileUtil.touch(zipFile);
		}
		zos = new ZipOutputStream(new FileOutputStream(zipFile));
		targetZipFile = zipFile;
		targetBaos = null;
	}

	protected ZipBuilder() {
		targetZipFile = null;
		targetBaos = new ByteArrayOutputStream();
		zos = new ZipOutputStream(targetBaos);
	}

	// ---------------------------------------------------------------- get

	public File toZipFile() {
		StreamUtil.close(zos);

		return targetZipFile;
	}

	public byte[] toBytes() {
		StreamUtil.close(zos);

		if (targetZipFile != null) {
			try {
				return FileUtil.readBytes(targetZipFile);
			}
			catch (IOException ignore) {
				return null;
			}
		}

		return targetBaos.toByteArray();
	}

	// ---------------------------------------------------------------- add file to zip

	public AddFileToZip add(File source) {
		return new AddFileToZip(source);
	}

	public class AddFileToZip {
		private final File file;
		private String path;
		private String comment;
		private boolean recursive = true;

		private AddFileToZip(File file) {
			this.file = file;
		}

		/**
		 * Defines optional entry path.
		 */
		public AddFileToZip path(String path) {
			this.path = path;
			return this;
		}

		/**
		 * Defines optional comment.
		 */
		public AddFileToZip comment(String comment) {
			this.comment = comment;
			return this;
		}
		/**
		 * Defines if folders content should be added.
		 * Ignored when used for files.
		 */
		public AddFileToZip recursive() {
			this.recursive = true;
			return this;
		}

		/**
		 * Stores the content into the ZIP.
		 */
		public ZipBuilder save() throws IOException {
			ZipUtil.addToZip(zos, file, path, comment, recursive);
			return ZipBuilder.this;
		}
	}

	// ---------------------------------------------------------------- add content

	public AddContentToZip add(String content) {
		try {
			return new AddContentToZip(content.getBytes(StringPool.UTF_8));
		}
		catch (UnsupportedEncodingException ignore) {
			return null;
		}
	}

	public AddContentToZip add(byte[] content) {
		return new AddContentToZip(content);
	}

	public class AddContentToZip {
		private final byte[] bytes;
		private String path;
		private String comment;

		private AddContentToZip(byte[] content) {
			this.bytes = content;
		}

		/**
		 * Defines optional entry path.
		 */
		public AddContentToZip path(String path) {
			this.path = path;
			return this;
		}

		/**
		 * Defines optional comment.
		 */
		public AddContentToZip comment(String comment) {
			this.comment = comment;
			return this;
		}

		/**
		 * Stores the content into the ZIP.
		 */
		public ZipBuilder save() throws IOException {
			ZipUtil.addToZip(zos, bytes, path, comment);
			return ZipBuilder.this;
		}
	}

	// ---------------------------------------------------------------- folder

	public ZipBuilder addFolder(String folderName) throws IOException {
		ZipUtil.addFolderToZip(zos, folderName, null);
		return this;
	}

}