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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
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
	public static File zlib(final String file) throws IOException {
		return zlib(new File(file));
	}

	/**
	 * Compresses a file into zlib archive.
	 */
	public static File zlib(final File file) throws IOException {
		if (file.isDirectory()) {
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
	public static File gzip(final String fileName) throws IOException {
		return gzip(new File(fileName));
	}

	/**
	 * Compresses a file into gzip archive.
	 */
	public static File gzip(final File file) throws IOException {
		if (file.isDirectory()) {
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
	public static File ungzip(final String file) throws IOException {
		return ungzip(new File(file));
	}

	/**
	 * Decompress gzip archive.
	 */
	public static File ungzip(final File file) throws IOException {
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
	public static File zip(final String file) throws IOException {
		return zip(new File(file));
	}

	/**
	 * Zips a file or a folder. If adding a folder, all its content will be added.
	 */
	public static File zip(final File file) throws IOException {
		String zipFile = file.getAbsolutePath() + ZIP_EXT;

		return ZipBuilder.createZipFile(zipFile)
					.add(file).recursive().save()
				.toZipFile();
	}

	// ---------------------------------------------------------------- unzip

	/**
	 * Lists zip content.
	 */
	public static List<String> listZip(final File zipFile) throws IOException {
		List<String> entries = new ArrayList<>();

		ZipFile zip = new ZipFile(zipFile);
		Enumeration zipEntries = zip.entries();

		while (zipEntries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) zipEntries.nextElement();
			String entryName = entry.getName();

			entries.add(entryName);
		}

		return Collections.unmodifiableList(entries);
	}

	/**
	 * Extracts zip file content to the target directory.
	 * @see #unzip(java.io.File, java.io.File, String...)
	 */
	public static void unzip(final String zipFile, final String destDir, final String... patterns) throws IOException {
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
	public static void unzip(final File zipFile, final File destDir, final String... patterns) throws IOException {
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

			final File file = (destDir != null) ? new File(destDir, entryName) : new File(entryName);

			// check for Zip slip FLAW
			final File rootDir = destDir != null ? destDir : new File(".");
			if (!FileUtil.isAncestor(rootDir, file, true)) {
				throw new IOException("Unzipping");
			}

			if (entry.isDirectory()) {
				if (!file.mkdirs()) {
					if (!file.isDirectory()) {
						throw new IOException("Failed to create directory: " + file);
					}
				}
			} else {
				File parent = file.getParentFile();
				if (parent != null && !parent.exists()) {
					if (!parent.mkdirs()) {
						if (!file.isDirectory()) {
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
	 * Adds single entry to ZIP output stream.
	 *
	 * @param zos zip output stream
	 * @param file file or folder to add
	 * @param path relative path of file entry; if <code>null</code> files name will be used instead
	 * @param comment optional comment
	 * @param recursive when set to <code>true</code> content of added folders will be added, too
	 */
	public static void addToZip(final ZipOutputStream zos, final File file, String path, final String comment, final boolean recursive) throws IOException {
		if (!file.exists()) {
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

	/**
	 * Adds byte content into the zip as a file.
	 */
	public static void addToZip(final ZipOutputStream zos, final byte[] content, String path, final String comment) throws IOException {
		while (path.length() != 0 && path.charAt(0) == '/') {
			path = path.substring(1);
		}

		if (StringUtil.endsWithChar(path, '/')) {
			path = path.substring(0, path.length() - 1);
		}

		ZipEntry zipEntry = new ZipEntry(path);
		zipEntry.setTime(System.currentTimeMillis());

		if (comment != null) {
			zipEntry.setComment(comment);
		}

		zos.putNextEntry(zipEntry);

		InputStream is = new ByteArrayInputStream(content);
		try {
			StreamUtil.copy(is, zos);
		} finally {
			StreamUtil.close(is);
		}

		zos.closeEntry();
	}

	public static void addFolderToZip(final ZipOutputStream zos, String path, final String comment) throws IOException {
		while (path.length() != 0 && path.charAt(0) == '/') {
			path = path.substring(1);
		}

		// add folder record
		if (!StringUtil.endsWithChar(path, '/')) {
			path += '/';
		}

		ZipEntry zipEntry = new ZipEntry(path);
		zipEntry.setTime(System.currentTimeMillis());

		if (comment != null) {
			zipEntry.setComment(comment);
		}

		zipEntry.setSize(0);
		zipEntry.setCrc(0);

		zos.putNextEntry(zipEntry);
		zos.closeEntry();
	}


	// ---------------------------------------------------------------- close

	/**
	 * Closes zip file safely.
	 */
	public static void close(final ZipFile zipFile) {
		if (zipFile != null) {
			try {
				zipFile.close();
			} catch (IOException ioex) {
				// ignore
			}
		}
	}

}
