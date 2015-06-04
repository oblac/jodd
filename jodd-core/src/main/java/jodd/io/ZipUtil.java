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
import jodd.util.StringUtil;
import jodd.util.Wildcard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Performs zip/gzip/zlib operations on files and directories.
 * These are just tools over existing <code>java.util.zip</code> classes,
 * meaning that existing behavior and bugs are persisted.
 * Most common issue is not being able to use UTF8 in file names,
 * because implementation uses old ZIP format that supports only
 * IBM Code Page 437. This bug was resolved in JDK7:
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4244499
 */
public class ZipUtil {

	public static final String ZIP_EXT = ".zip";
	public static final String GZIP_EXT = ".gz";
	public static final String ZLIB_EXT = ".zlib";

	// ---------------------------------------------------------------- deflate

	/**
	 * Compresses a file into zlib archive.
	 */
	public static File zlib(String file) throws IOException {
		return zlib(new File(file));
	}

	/**
	 * Compresses a file into zlib archive.
	 */
	public static File zlib(File file) throws IOException {
		if (file.isDirectory() == true) {
			throw new IOException("Can't zlib folder");
		}
		FileInputStream fis = new FileInputStream(file);
		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);

		String zlibFileName = file.getAbsolutePath() + ZLIB_EXT;

		DeflaterOutputStream dos = new DeflaterOutputStream(new FileOutputStream(zlibFileName), deflater);

		try {
			StreamUtil.copy(fis, dos);
		} finally {
			StreamUtil.close(dos);
			StreamUtil.close(fis);
		}

		return new File(zlibFileName);
	}

	// ---------------------------------------------------------------- gzip
	
	/**
	 * Compresses a file into gzip archive.
	 */
	public static File gzip(String fileName) throws IOException {
		return gzip(new File(fileName));
	}

	/**
	 * Compresses a file into gzip archive.
	 */
	public static File gzip(File file) throws IOException {
		if (file.isDirectory() == true) {
			throw new IOException("Can't gzip folder");
		}
		FileInputStream fis = new FileInputStream(file);

		String gzipName = file.getAbsolutePath() + GZIP_EXT;

		GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(gzipName));
		try {
			StreamUtil.copy(fis, gzos);
		} finally {
			StreamUtil.close(gzos);
			StreamUtil.close(fis);
		}

		return new File(gzipName);
	}

	/**
	 * Decompress gzip archive.
	 */
	public static File ungzip(String file) throws IOException {
		return ungzip(new File(file));
	}

	/**
	 * Decompress gzip archive.
	 */
	public static File ungzip(File file) throws IOException {
		String outFileName = FileNameUtil.removeExtension(file.getAbsolutePath());
		File out = new File(outFileName);
		out.createNewFile();

		FileOutputStream fos = new FileOutputStream(out);
		GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(file));
		try {
			StreamUtil.copy(gzis, fos);
		} finally {
			StreamUtil.close(fos);
			StreamUtil.close(gzis);
		}

		return out;
	}

	// ---------------------------------------------------------------- zip

	/**
	 * Zips a file or a folder.
	 * @see #zip(java.io.File)
	 */
	public static File zip(String file) throws IOException {
		return zip(new File(file));
	}

	/**
	 * Zips a file or a folder. If adding a folder, all its content will be added.
	 */
	public static File zip(File file) throws IOException {
		String zipFile = file.getAbsolutePath() + ZIP_EXT;

		ZipOutputStream zos = null;
		try {
			zos = createZip(zipFile);
			addToZip(zos).file(file).recursive().add();
		} finally {
			StreamUtil.close(zos);
		}

		return new File(zipFile);
	}

	// ---------------------------------------------------------------- unzip

	/**
	 * Extracts zip file content to the target directory.
	 * @see #unzip(java.io.File, java.io.File, String...)
	 */
	public static void unzip(String zipFile, String destDir, String... patterns) throws IOException {
		unzip(new File(zipFile), new File(destDir), patterns);
	}

	/**
	 * Extracts zip file to the target directory. If patterns are provided
	 * only matched paths are extracted.
	 *
	 * @param zipFile zip file
	 * @param destDir destination directory
	 * @param patterns optional wildcard patterns of files to extract, may be <code>null</code>
	 */
	public static void unzip(File zipFile, File destDir, String... patterns) throws IOException {
		ZipFile zip = new ZipFile(zipFile);
		Enumeration zipEntries = zip.entries();

		while (zipEntries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) zipEntries.nextElement();
			String entryName = entry.getName();

			if (patterns != null && patterns.length > 0) {
				if (Wildcard.matchPathOne(entryName, patterns) == -1) {
					continue;
				}
			}

			File file = (destDir != null) ? new File(destDir, entryName) : new File(entryName);
			if (entry.isDirectory()) {
				if (!file.mkdirs()) {
					if (file.isDirectory() == false) {
						throw new IOException("Failed to create directory: " + file);
					}
				}
			} else {
				File parent = file.getParentFile();
				if (parent != null && !parent.exists()) {
					if (!parent.mkdirs()) {
						if (file.isDirectory() == false) {
							throw new IOException("Failed to create directory: " + parent);
						}
					}
				}

				InputStream in = zip.getInputStream(entry);
				OutputStream out = null;
				try {
					out = new FileOutputStream(file);
					StreamUtil.copy(in, out);
				} finally {
					StreamUtil.close(out);
					StreamUtil.close(in);
				}
			}
		}

		close(zip);
	}

	// ---------------------------------------------------------------- zip stream

	/**
	 * Creates and opens zip output stream of a zip file.
	 * @see #createZip(java.io.File)
	 */
	public static ZipOutputStream createZip(String zipFile) throws FileNotFoundException {
		return createZip(new File(zipFile));
	}

	/**
	 * Creates and opens zip output stream of a zip file. If zip file exist it will be recreated.
	 */
	public static ZipOutputStream createZip(File zip) throws FileNotFoundException {
		return new ZipOutputStream(new FileOutputStream(zip));
	}

	/**
	 * Starts a command for adding file entries to the zip.
	 * @see #addToZip(java.util.zip.ZipOutputStream, java.io.File, String, String, boolean)
	 */
	public static AddToZip addToZip(ZipOutputStream zos) {
		return new AddToZip(zos);
	}

	/**
	 * Command: "add to zip".
	 */
	public static class AddToZip {
		private final ZipOutputStream zos;
		private File file;
		private String path;
		private String comment;
		private boolean recursive = true;

		private AddToZip(ZipOutputStream zos) {
			this.zos = zos;
		}

		/**
		 * Defines file or folder to be added to zip.
		 */
		public AddToZip file(File file) {
			this.file = file;
			return this;
		}
		/**
		 * Defines file or folder to be added to zip.
		 */
		public AddToZip file(String fileName) {
			this.file = new File(fileName);
			return this;
		}

		/**
		 * Defines file or folder to be added to zip.
		 */
		public AddToZip file(String parent, String child) {
			this.file = new File(parent, child);
			return this;
		}

		/**
		 * Defines optional entry path.
		 */
		public AddToZip path(String path) {
			this.path = path;
			return this;
		}

		/**
		 * Defines optional comment.
		 */
		public AddToZip comment(String comment) {
			this.comment = comment;
			return this;
		}
		/**
		 * Defines if folders content should be added.
		 * Ignored for files.
		 */
		public AddToZip recursive() {
			this.recursive = true;
			return this;
		}
		/**
		 * Invokes the adding command.
		 */
		public void add() throws IOException {
			addToZip(zos, file, path, comment, recursive);
		}
	}

	/**
	 * Adds single entry to ZIP output stream. For user-friendly way of adding entries to zip
	 * see {@link #addToZip(java.util.zip.ZipOutputStream)}.
	 *
	 * @param zos zip output stream
	 * @param file file or folder to add
	 * @param path relative path of file entry; if <code>null</code> files name will be used instead
	 * @param comment optional comment
	 * @param recursive when set to <code>true</code> content of added folders will be added, too
	 */
	public static void addToZip(ZipOutputStream zos, File file, String path, String comment, boolean recursive) throws IOException {
		if (file.exists() == false) {
			throw new FileNotFoundException(file.toString());
		}

		if (path == null) {
			path = file.getName();
		}

		while (path.length() != 0 && path.charAt(0) == '/') {
			path = path.substring(1);
		}

		boolean isDir = file.isDirectory();

		if (isDir) {
			// add folder record
			if (!StringUtil.endsWithChar(path, '/')) {
				path += '/';
			}
		}

		ZipEntry zipEntry = new ZipEntry(path);
		zipEntry.setTime(file.lastModified());

		if (comment != null) {
			zipEntry.setComment(comment);
		}

		if (isDir) {
			zipEntry.setSize(0);
			zipEntry.setCrc(0);
		}

		zos.putNextEntry(zipEntry);

		if (!isDir) {
			InputStream is = new FileInputStream(file);
			try {
				StreamUtil.copy(is, zos);
			} finally {
				StreamUtil.close(is);
			}
		}

		zos.closeEntry();

		// continue adding

		if (recursive && file.isDirectory()) {
			boolean noRelativePath = StringUtil.isEmpty(path);

			final File[] children = file.listFiles();

			if (children != null && children.length != 0) {
				for (File child : children) {
					String childRelativePath = (noRelativePath ? StringPool.EMPTY : path) + child.getName();
					addToZip(zos, child, childRelativePath, comment, recursive);
				}
			}
		}

	}

	// ---------------------------------------------------------------- close

	/**
	 * Closes zip file safely.
	 */
	public static void close(ZipFile zipFile) {
		if (zipFile != null) {
			try {
				zipFile.close();
			} catch (IOException ioex) {
				// ignore
			}
		}
	}

}
