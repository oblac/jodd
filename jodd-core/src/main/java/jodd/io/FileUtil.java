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
import jodd.util.StringPool;
import jodd.util.StringUtil;
import jodd.util.SystemUtil;
import jodd.util.URLDecoder;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileFilter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ArrayList;

import static jodd.core.JoddCore.fileUtilParams;

/**
 * File utilities.
 */
public class FileUtil {

	private static final String MSG_NOT_A_DIRECTORY = "Not a directory: ";
	private static final String MSG_CANT_CREATE = "Can't create: ";
	private static final String MSG_NOT_FOUND = "Not found: ";
	private static final String MSG_NOT_A_FILE = "Not a file: ";
	private static final String MSG_ALREADY_EXISTS = "Already exists: ";
	private static final String MSG_UNABLE_TO_DELETE = "Unable to delete: ";

	/**
	 * Simple factory for <code>File</code> objects.
	 */
	private static File file(String fileName) {
		return new File(fileName);
	}

	/**
	 * Simple factory for <code>File</code> objects.
	 */
	private static File file(File parent, String fileName) {
		return new File(parent, fileName);
	}

	// ---------------------------------------------------------------- misc shortcuts

	/**
	 * Checks if two files points to the same file.
	 */
	public static boolean equals(String file1, String file2) {
		return equals(file(file1), file(file2));
	}

	/**
	 * Checks if two files points to the same file.
	 */
	public static boolean equals(File file1, File file2) {
		try {
			file1 = file1.getCanonicalFile();
			file2 = file2.getCanonicalFile();
		} catch (IOException ignore) {
			return false;
		}
		return file1.equals(file2);
	}

	/**
	 * Converts file URLs to file. Ignores other schemes and returns <code>null</code>.
	 */
	public static File toFile(URL url) {
		String fileName = toFileName(url);
		if (fileName == null) {
			return null;
		}
		return file(fileName);
	}

	/**
	 * Converts file to URL in a correct way.
	 * Returns <code>null</code> in case of error.
	 */
	public static URL toURL(File file) throws MalformedURLException {
		return file.toURI().toURL();
	}

	/**
	 * Converts file URLs to file name. Accepts only URLs with 'file' protocol.
	 * Otherwise, for other schemes returns <code>null</code>.
	 */
	public static String toFileName(URL url) {
		if ((url == null) || !(url.getProtocol().equals("file"))) {
			return null;
		}
		String filename = url.getFile().replace('/', File.separatorChar);

		return URLDecoder.decode(filename, JoddCore.encoding);
	}

	/**
	 * Returns a file of either a folder or a containing archive.
	 */
	public static File toContainerFile(URL url) {
		String protocol = url.getProtocol();
		if (protocol.equals("file")) {
			return toFile(url);
		}

		String path = url.getPath();

		return new File(URI.create(
				path.substring(0, path.lastIndexOf("!/"))));
	}

	/**
	 * Returns <code>true</code> if file exists.
	 */
	public static boolean isExistingFile(File file) {
		if (file == null) {
			return false;
		}
		return file.exists() && file.isFile();
	}

	/**
	 * Returns <code>true</code> if folder exists.
	 */
	public static boolean isExistingFolder(File folder) {
		if (folder == null) {
			return false;
		}
		return folder.exists() && folder.isDirectory();
	}

	// ---------------------------------------------------------------- mkdirs

	/**
	 * Creates all folders at once.
	 * @see #mkdirs(java.io.File)
	 */
	public static void mkdirs(String dirs) throws IOException {
		mkdirs(file(dirs));
	}
	/**
	 * Creates all folders at once.
	 */
	public static void mkdirs(File dirs) throws IOException {
		if (dirs.exists()) {
			if (!dirs.isDirectory()) {
				throw new IOException(MSG_NOT_A_DIRECTORY + dirs);
			}
			return;
		}
		if (!dirs.mkdirs()) {
			throw new IOException(MSG_CANT_CREATE + dirs);
		}
	}

	/**
	 * Creates single folder.
	 * @see #mkdir(java.io.File)
	 */
	public static void mkdir(String dir) throws IOException {
		mkdir(file(dir));
	}
	/**
	 * Creates single folders.
	 */
	public static void mkdir(File dir) throws IOException {
		if (dir.exists()) {
			if (!dir.isDirectory()) {
				throw new IOException(MSG_NOT_A_DIRECTORY + dir);
			}
			return;
		}
		if (!dir.mkdir()) {
			throw new IOException(MSG_CANT_CREATE + dir);
		}
	}

	// ---------------------------------------------------------------- touch

	/**
	 * @see #touch(java.io.File)
	 */
	public static void touch(String file) throws IOException {
		touch(file(file));
	}
	/**
	 * Implements the Unix "touch" utility. It creates a new file
	 * with size 0 or, if the file exists already, it is opened and
	 * closed without modifying it, but updating the file date and time.
	 */
	public static void touch(File file) throws IOException {
		if (!file.exists()) {
			StreamUtil.close(new FileOutputStream(file));
		}
		file.setLastModified(System.currentTimeMillis());
	}


	// ---------------------------------------------------------------- params

	/**
	 * Creates new {@link FileUtilParams} instance by cloning current default params.
	 */
	public static FileUtilParams cloneParams() {
		try {
			return fileUtilParams.clone();
		} catch (CloneNotSupportedException ignore) {
			return null;
		}
	}

	/**
	 * Creates new {@link FileUtilParams} instance with default values.
	 */
	public static FileUtilParams params() {
		return new FileUtilParams();
	}

	// ---------------------------------------------------------------- copy file to file

	/**
	 * @see #copyFile(java.io.File, java.io.File, FileUtilParams)
	 */
	public static void copyFile(String src, String dest) throws IOException {
		copyFile(file(src), file(dest), fileUtilParams);
	}
	/**
	 * @see #copyFile(java.io.File, java.io.File, FileUtilParams)
	 */
	public static void copyFile(String src, String dest, FileUtilParams params) throws IOException {
		copyFile(file(src), file(dest), params);
	}
	/**
	 * @see #copyFile(java.io.File, java.io.File, FileUtilParams)
	 */
	public static void copyFile(File src, File dest) throws IOException {
		copyFile(src, dest, fileUtilParams);
	}

	/**
	 * Copies a file to another file with specified {@link FileUtilParams copy params}.
	 */
	public static void copyFile(File src, File dest, FileUtilParams params) throws IOException {
		checkFileCopy(src, dest, params);
		doCopyFile(src, dest, params);
	}

	private static void checkFileCopy(File src, File dest, FileUtilParams params) throws IOException {
		if (!src.exists()) {
			throw new FileNotFoundException(MSG_NOT_FOUND + src);
		}
		if (!src.isFile()) {
			throw new IOException(MSG_NOT_A_FILE + src);
		}
		if (equals(src, dest)) {
			throw new IOException("Files '" + src + "' and '" + dest + "' are equal");
		}

		File destParent = dest.getParentFile();
		if (destParent != null && !destParent.exists()) {
			if (!params.createDirs) {
				throw new IOException(MSG_NOT_FOUND + destParent);
			}
			if (!destParent.mkdirs()) {
				throw new IOException(MSG_CANT_CREATE + destParent);
			}
		}
	}

	/**
	 * Internal file copy when most of the pre-checking has passed.
	 */
	private static void doCopyFile(File src, File dest, FileUtilParams params) throws IOException {
		if (dest.exists()) {
			if (dest.isDirectory()) {
				throw new IOException("Destination '" + dest + "' is a directory");
			}
			if (!params.overwrite) {
				throw new IOException(MSG_ALREADY_EXISTS + dest);
			}
		}

		// do copy file
		FileInputStream input = new FileInputStream(src);
		try {
			FileOutputStream output = new FileOutputStream(dest);
			try {
				StreamUtil.copy(input, output);
			} finally {
				StreamUtil.close(output);
			}
		} finally {
			StreamUtil.close(input);
		}

		// done

		if (src.length() != dest.length()) {
			throw new IOException("Copy file failed of '" + src + "' to '" + dest + "' due to different sizes");
		}
		if (params.preserveDate) {
			dest.setLastModified(src.lastModified());
		}
	}

	// ---------------------------------------------------------------- copy file to directory

	/**
	 * @see #copyFileToDir(java.io.File, java.io.File, FileUtilParams)
	 */
	public static File copyFileToDir(String src, String destDir) throws IOException {
		return copyFileToDir(file(src), file(destDir), fileUtilParams);
	}
	/**
	 * @see #copyFileToDir(java.io.File, java.io.File, FileUtilParams)
	 */
	public static File copyFileToDir(String src, String destDir, FileUtilParams params) throws IOException {
		return copyFileToDir(file(src), file(destDir), params);
	}
	/**
	 * @see #copyFileToDir(java.io.File, java.io.File, FileUtilParams)
	 */
	public static File copyFileToDir(File src, File destDir) throws IOException {
		return copyFileToDir(src, destDir, fileUtilParams);
	}
	/**
	 * Copies a file to folder with specified copy params and returns copied destination.
	 */
	public static File copyFileToDir(File src, File destDir, FileUtilParams params) throws IOException {
		if (destDir.exists() && !destDir.isDirectory()) {
			throw new IOException(MSG_NOT_A_DIRECTORY + destDir);
		}
		File dest = file(destDir, src.getName());
		copyFile(src, dest, params);
		return dest;
	}


	// ---------------------------------------------------------------- copy dir


	public static void copyDir(String srcDir, String destDir) throws IOException {
		copyDir(file(srcDir), file(destDir), fileUtilParams);
	}

	public static void copyDir(String srcDir, String destDir, FileUtilParams params) throws IOException {
		copyDir(file(srcDir), file(destDir), params);
	}

	public static void copyDir(File srcDir, File destDir) throws IOException {
		copyDir(srcDir, destDir, fileUtilParams);
	}

	/**
	 * Copies directory with specified copy params.
	 */
	public static void copyDir(File srcDir, File destDir, FileUtilParams params) throws IOException {
		checkDirCopy(srcDir, destDir);
		doCopyDirectory(srcDir, destDir, params);
	}

	private static void checkDirCopy(File srcDir, File destDir) throws IOException {
		if (!srcDir.exists()) {
			throw new FileNotFoundException(MSG_NOT_FOUND + srcDir);
		}
		if (!srcDir.isDirectory()) {
			throw new IOException(MSG_NOT_A_DIRECTORY + srcDir);
		}
		if (equals(srcDir, destDir)) {
			throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are equal");
		}
	}

	private static void doCopyDirectory(File srcDir, File destDir, FileUtilParams params) throws IOException {
		if (destDir.exists()) {
			if (!destDir.isDirectory()) {
				throw new IOException(MSG_NOT_A_DIRECTORY + destDir);
			}
		} else {
			if (!params.createDirs) {
				throw new IOException(MSG_NOT_FOUND + destDir);
			}
			if (!destDir.mkdirs()) {
				throw new IOException(MSG_CANT_CREATE + destDir);
			}
			if (params.preserveDate) {
				destDir.setLastModified(srcDir.lastModified());
			}
		}

		File[] files = srcDir.listFiles();
		if (files == null) {
			throw new IOException("Failed to list contents of: " + srcDir);
		}

		IOException exception = null;
		for (File file : files) {
			File destFile = file(destDir, file.getName());
			try {
				if (file.isDirectory()) {
					if (params.recursive) {
						doCopyDirectory(file, destFile, params);
					}
				} else {
					doCopyFile(file, destFile, params);
				}
			} catch (IOException ioex) {
				if (params.continueOnError) {
					exception = ioex;
					continue;
				}
				throw ioex;
			}
		}

		if (exception != null) {
			throw exception;
		}
	}



	// ---------------------------------------------------------------- move file

	public static File moveFile(String src, String dest) throws IOException {
		return moveFile(file(src), file(dest), fileUtilParams);
	}

	public static File moveFile(String src, String dest, FileUtilParams params) throws IOException {
		return moveFile(file(src), file(dest), params);
	}

	public static File moveFile(File src, File dest) throws IOException {
		return moveFile(src, dest, fileUtilParams);
	}

	public static File moveFile(File src, File dest, FileUtilParams params) throws IOException {
		checkFileCopy(src, dest, params);
		doMoveFile(src, dest, params);
		return dest;
	}

	private static void doMoveFile(File src, File dest, FileUtilParams params) throws IOException {
		if (dest.exists()) {
			if (!dest.isFile()) {
				throw new IOException(MSG_NOT_A_FILE + dest);
			}
			if (!params.overwrite) {
				throw new IOException(MSG_ALREADY_EXISTS + dest);
			}
			dest.delete();
		}

		final boolean rename = src.renameTo(dest);
		if (!rename) {
			doCopyFile(src, dest, params);
			src.delete();
		}
	}

	// ---------------------------------------------------------------- move file to dir


	public static File moveFileToDir(String src, String destDir) throws IOException {
		return moveFileToDir(file(src), file(destDir), fileUtilParams);
	}
	public static File moveFileToDir(String src, String destDir, FileUtilParams params) throws IOException {
		return moveFileToDir(file(src), file(destDir), params);
	}

	public static File moveFileToDir(File src, File destDir) throws IOException {
		return moveFileToDir(src, destDir, fileUtilParams);
	}
	public static File moveFileToDir(File src, File destDir, FileUtilParams params) throws IOException {
		if (destDir.exists() && !destDir.isDirectory()) {
			throw new IOException(MSG_NOT_A_DIRECTORY + destDir);
		}
		return moveFile(src, file(destDir, src.getName()), params);
	}


	// ---------------------------------------------------------------- move dir

	public static File moveDir(String srcDir, String destDir) throws IOException {
		return moveDir(file(srcDir), file(destDir));
	}
	public static File moveDir(File srcDir, File destDir) throws IOException {
		checkDirCopy(srcDir, destDir);
		doMoveDirectory(srcDir, destDir);
		return destDir;
	}

	private static void doMoveDirectory(File src, File dest) throws IOException {
		if (dest.exists()) {
			if (!dest.isDirectory()) {
				throw new IOException(MSG_NOT_A_DIRECTORY + dest);
			}
			dest = file(dest, dest.getName());
			dest.mkdir();
		}

		final boolean rename = src.renameTo(dest);
		if (!rename) {
			doCopyDirectory(src, dest, params());
			deleteDir(src);
		}
	}

	// ---------------------------------------------------------------- delete file

	public static void deleteFile(String dest) throws IOException {
		deleteFile(file(dest));
	}

	public static void deleteFile(File dest) throws IOException {
		if (!dest.exists()) {
			throw new FileNotFoundException(MSG_NOT_FOUND + dest);
		}
		if (!dest.isFile()) {
			throw new IOException(MSG_NOT_A_FILE + dest);
		}
		if (!dest.delete()) {
			throw new IOException(MSG_UNABLE_TO_DELETE + dest);
		}
	}


	// ---------------------------------------------------------------- delete dir

	public static void deleteDir(String dest) throws IOException {
		deleteDir(file(dest), fileUtilParams);
	}
	public static void deleteDir(String dest, FileUtilParams params) throws IOException {
		deleteDir(file(dest), params);
	}
	public static void deleteDir(File dest) throws IOException {
		deleteDir(dest, fileUtilParams);
	}
	/**
	 * Deletes a directory.
	 */
	public static void deleteDir(File dest, FileUtilParams params) throws IOException {
		cleanDir(dest, params);
		if (!dest.delete()) {
			throw new IOException(MSG_UNABLE_TO_DELETE + dest);
		}
	}



	public static void cleanDir(String dest) throws IOException {
		cleanDir(file(dest), fileUtilParams);
	}
	public static void cleanDir(String dest, FileUtilParams params) throws IOException {
		cleanDir(file(dest), params);
	}
	public static void cleanDir(File dest) throws IOException {
		cleanDir(dest, fileUtilParams);
	}

	/**
	 * Cleans a directory without deleting it.
	 */
	public static void cleanDir(File dest, FileUtilParams params) throws IOException {
		if (!dest.exists()) {
			throw new FileNotFoundException(MSG_NOT_FOUND + dest);
		}

		if (!dest.isDirectory()) {
			throw new IOException(MSG_NOT_A_DIRECTORY + dest);
		}

		File[] files = dest.listFiles();
		if (files == null) {
			throw new IOException("Failed to list contents of: " + dest);
		}

		IOException exception = null;
		for (File file : files) {
			try {
				if (file.isDirectory()) {
					if (params.recursive) {
						deleteDir(file, params);
					}
				} else {
					file.delete();
				}
			} catch (IOException ioex) {
				if (params.continueOnError) {
					exception = ioex;
					continue;
				}
				throw ioex;
			}
		}

		if (exception != null) {
			throw exception;
		}
	}

	// ---------------------------------------------------------------- read/write chars

	public static char[] readUTFChars(String fileName) throws IOException {
		return readUTFChars(file(fileName));
	}
	/**
	 * Reads UTF file content as char array.
	 * @see UnicodeInputStream
	 */
	public static char[] readUTFChars(File file) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(MSG_NOT_FOUND + file);
		}
		if (!file.isFile()) {
			throw new IOException(MSG_NOT_A_FILE + file);
		}
		long len = file.length();
		if (len >= Integer.MAX_VALUE) {
			len = Integer.MAX_VALUE;
		}
		UnicodeInputStream in = null;
		try {
			in = new UnicodeInputStream(new FileInputStream(file), null);
			FastCharArrayWriter fastCharArrayWriter = new FastCharArrayWriter((int) len);
			String encoding = in.getDetectedEncoding();
			if (encoding == null) {
				encoding = StringPool.UTF_8;
			}
			StreamUtil.copy(in, fastCharArrayWriter, encoding);
			return fastCharArrayWriter.toCharArray();
		} finally {
			StreamUtil.close(in);
		}
	}

	public static char[] readChars(String fileName) throws IOException {
		return readChars(file(fileName), fileUtilParams.encoding);
	}

	public static char[] readChars(File file) throws IOException {
		return readChars(file, fileUtilParams.encoding);
	}

	public static char[] readChars(String fileName, String encoding) throws IOException {
		return readChars(file(fileName), encoding);
	}

	/**
	 * Reads file content as char array.
	 */
	public static char[] readChars(File file, String encoding) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(MSG_NOT_FOUND + file);
		}
		if (!file.isFile()) {
			throw new IOException(MSG_NOT_A_FILE + file);
		}
		long len = file.length();
		if (len >= Integer.MAX_VALUE) {
			len = Integer.MAX_VALUE;
		}

		InputStream in = null;
		try {
			in = new FileInputStream(file);
			if (encoding.startsWith("UTF")) {
				in = new UnicodeInputStream(in, encoding);
			}
			FastCharArrayWriter fastCharArrayWriter = new FastCharArrayWriter((int) len);
			StreamUtil.copy(in, fastCharArrayWriter, encoding);
			return fastCharArrayWriter.toCharArray();
		} finally {
			StreamUtil.close(in);
		}
	}


	public static void writeChars(File dest, char[] data) throws IOException {
		outChars(dest, data, JoddCore.encoding, false);
	}
	public static void writeChars(String dest, char[] data) throws IOException {
		outChars(file(dest), data, JoddCore.encoding, false);
	}

	public static void writeChars(File dest, char[] data, String encoding) throws IOException {
		outChars(dest, data, encoding, false);
	}
	public static void writeChars(String dest, char[] data, String encoding) throws IOException {
		outChars(file(dest), data, encoding, false);
	}
	
	protected static void outChars(File dest, char[] data, String encoding, boolean append) throws IOException {
		if (dest.exists()) {
			if (!dest.isFile()) {
				throw new IOException(MSG_NOT_A_FILE + dest);
			}
		}
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest, append), encoding));
		try {
			out.write(data);
		} finally {
			StreamUtil.close(out);
		}
	}


	// ---------------------------------------------------------------- read/write string

	public static String readUTFString(String fileName) throws IOException {
		return readUTFString(file(fileName));
	}

	/**
	 * Detects optional BOM and reads UTF string from a file.
	 * If BOM is missing, UTF-8 is assumed.
	 * @see UnicodeInputStream
	 */
	public static String readUTFString(File file) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(MSG_NOT_FOUND + file);
		}
		if (!file.isFile()) {
			throw new IOException(MSG_NOT_A_FILE + file);
		}
		long len = file.length();
		if (len >= Integer.MAX_VALUE) {
			len = Integer.MAX_VALUE;
		}
		UnicodeInputStream in = null;
		try {
			in = new UnicodeInputStream(new FileInputStream(file), null);
			FastCharArrayWriter out = new FastCharArrayWriter((int) len);
			String encoding = in.getDetectedEncoding();
			if (encoding == null) {
				encoding = StringPool.UTF_8;
			}
			StreamUtil.copy(in, out, encoding);
			return out.toString();
		} finally {
			StreamUtil.close(in);
		}
	}

	/**
	 * Detects optional BOM and reads UTF string from an input stream.
	 * If BOM is missing, UTF-8 is assumed.
	 */
	public static String readUTFString(InputStream inputStream) throws IOException {
		UnicodeInputStream in = null;
		try {
			in = new UnicodeInputStream(inputStream, null);
			FastCharArrayWriter out = new FastCharArrayWriter();
			String encoding = in.getDetectedEncoding();
			if (encoding == null) {
				encoding = StringPool.UTF_8;
			}
			StreamUtil.copy(in, out, encoding);
			return out.toString();
		} finally {
			StreamUtil.close(in);
		}
	}


	public static String readString(String source) throws IOException {
		return readString(file(source), fileUtilParams.encoding);
	}

	public static String readString(String source, String encoding) throws IOException {
		return readString(file(source), encoding);
	}

	public static String readString(File source) throws IOException {
		return readString(source, fileUtilParams.encoding);
	}

	/**
	 * Reads file content as string encoded in provided encoding.
	 * For UTF encoded files, detects optional BOM characters.
	 */
	public static String readString(File file, String encoding) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(MSG_NOT_FOUND + file);
		}
		if (!file.isFile()) {
			throw new IOException(MSG_NOT_A_FILE + file);
		}
		long len = file.length();
		if (len >= Integer.MAX_VALUE) {
			len = Integer.MAX_VALUE;
		}
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			if (encoding.startsWith("UTF")) {
				in = new UnicodeInputStream(in, encoding);
			}
			FastCharArrayWriter out = new FastCharArrayWriter((int) len);
			StreamUtil.copy(in, out, encoding);
			return out.toString();
		} finally {
			StreamUtil.close(in);
		}
	}


	public static void writeString(String dest, String data) throws IOException {
		outString(file(dest), data, fileUtilParams.encoding, false);
	}

	public static void writeString(String dest, String data, String encoding) throws IOException {
		outString(file(dest), data, encoding, false);
	}

	public static void writeString(File dest, String data) throws IOException {
		outString(dest, data, fileUtilParams.encoding, false);
	}

	public static void writeString(File dest, String data, String encoding) throws IOException {
		outString(dest, data, encoding, false);
	}


	public static void appendString(String dest, String data) throws IOException {
		outString(file(dest), data, fileUtilParams.encoding, true);
	}

	public static void appendString(String dest, String data, String encoding) throws IOException {
		outString(file(dest), data, encoding, true);
	}

	public static void appendString(File dest, String data) throws IOException {
		outString(dest, data, fileUtilParams.encoding, true);
	}

	public static void appendString(File dest, String data, String encoding) throws IOException {
		outString(dest, data, encoding, true);
	}

	protected static void outString(File dest, String data, String encoding, boolean append) throws IOException {
		if (dest.exists()) {
			if (!dest.isFile()) {
				throw new IOException(MSG_NOT_A_FILE + dest);
			}
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(dest, append);
			out.write(data.getBytes(encoding));
		} finally {
			StreamUtil.close(out);
		}
	}

	// ---------------------------------------------------------------- stream

	public static void writeStream(File dest, InputStream in) throws IOException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(dest);
			StreamUtil.copy(in, out);
		} finally {
			StreamUtil.close(out);
		}
	}

	public static void writeStream(String dest, InputStream in) throws IOException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(dest);
			StreamUtil.copy(in, out);
		} finally {
			StreamUtil.close(out);
		}
	}


	// ---------------------------------------------------------------- read/write string lines


	public static String[] readLines(String source) throws IOException {
		return readLines(file(source), fileUtilParams.encoding);
	}
	public static String[] readLines(String source, String encoding) throws IOException {
		return readLines(file(source), encoding);
	}
	public static String[] readLines(File source) throws IOException {
		return readLines(source, fileUtilParams.encoding);
	}

	/**
	 * Reads lines from source files.
	 */
	public static String[] readLines(File file, String encoding) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(MSG_NOT_FOUND + file);
		}
		if (!file.isFile()) {
			throw new IOException(MSG_NOT_A_FILE + file);
		}
		List<String> list = new ArrayList<>();

		InputStream in = null;
		try {
			in = new FileInputStream(file);
			if (encoding.startsWith("UTF")) {
				in = new UnicodeInputStream(in, encoding);
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(in, encoding));
			String strLine;
			while ((strLine = br.readLine()) != null)   {
				list.add(strLine);
			}
		} finally {
			StreamUtil.close(in);
		}
		return list.toArray(new String[list.size()]);
	}



	// ---------------------------------------------------------------- read/write bytearray


	public static byte[] readBytes(String file) throws IOException {
		return readBytes(file(file));
	}

	public static byte[] readBytes(File file) throws IOException {
		return readBytes(file, -1);
	}
	public static byte[] readBytes(File file, int fixedLength) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(MSG_NOT_FOUND + file);
		}
		if (!file.isFile()) {
			throw new IOException(MSG_NOT_A_FILE + file);
		}
		long len = file.length();
		if (len >= Integer.MAX_VALUE) {
			throw new IOException("File is larger then max array size");
		}

		if (fixedLength > -1 && fixedLength < len) {
			len = fixedLength;
		}

		byte[] bytes = new byte[(int) len];
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
		randomAccessFile.readFully(bytes);
		randomAccessFile.close();

		return bytes;
	}



	public static void writeBytes(String dest, byte[] data) throws IOException {
		outBytes(file(dest), data, 0, data.length, false);
	}

	public static void writeBytes(String dest, byte[] data, int off, int len) throws IOException {
		outBytes(file(dest), data, off, len, false);
	}

	public static void writeBytes(File dest, byte[] data) throws IOException {
		outBytes(dest, data, 0, data.length, false);
	}

	public static void writeBytes(File dest, byte[] data, int off, int len) throws IOException {
		outBytes(dest, data, off, len, false);
	}


	public static void appendBytes(String dest, byte[] data) throws IOException {
		outBytes(file(dest), data, 0, data.length, true);
	}

	public static void appendBytes(String dest, byte[] data, int off, int len) throws IOException {
		outBytes(file(dest), data, off, len, true);
	}

	public static void appendBytes(File dest, byte[] data) throws IOException {
		outBytes(dest, data, 0, data.length, true);
	}

	public static void appendBytes(File dest, byte[] data, int off, int len) throws IOException {
		outBytes(dest, data, off, len, true);
	}

	protected static void outBytes(File dest, byte[] data, int off, int len, boolean append) throws IOException {
		if (dest.exists()) {
			if (!dest.isFile()) {
				throw new IOException(MSG_NOT_A_FILE + dest);
			}
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(dest, append);
			out.write(data, off, len);
		} finally {
			StreamUtil.close(out);
		}
	}

	// ---------------------------------------------------------------- equals content

	public static boolean compare(String file1, String file2) throws IOException {
		return compare(file(file1), file(file2));
	}

	/**
	 * Compare the contents of two files to determine if they are equal or
	 * not.
	 * <p>
	 * This method checks to see if the two files are different lengths
	 * or if they point to the same file, before resorting to byte-by-byte
	 * comparison of the contents.
	 * <p>
	 * Code origin: Avalon
	 */
	public static boolean compare(File file1, File file2) throws IOException {
		boolean file1Exists = file1.exists();
		if (file1Exists != file2.exists()) {
			return false;
		}

		if (!file1Exists) {
			return true;
		}

		if ((!file1.isFile()) || (!file2.isFile())) {
			throw new IOException("Only files can be compared");
		}

		if (file1.length() != file2.length()) {
			return false;
		}

		if (equals(file1, file2)) {
			return true;
		}

		InputStream input1 = null;
		InputStream input2 = null;
		try {
			input1 = new FileInputStream(file1);
			input2 = new FileInputStream(file2);
			return StreamUtil.compare(input1, input2);
		} finally {
			StreamUtil.close(input1);
			StreamUtil.close(input2);
		}
	}

	// ---------------------------------------------------------------- time

	public static boolean isNewer(String file, String reference) {
		return isNewer(file(file), file(reference));
	}

	/**
	 * Test if specified <code>File</code> is newer than the reference <code>File</code>.
	 *
	 * @param file		the <code>File</code> of which the modification date must be compared
	 * @param reference	the <code>File</code> of which the modification date is used
	 * @return <code>true</code> if the <code>File</code> exists and has been modified more
	 * 			recently than the reference <code>File</code>.
	 */
	public static boolean isNewer(File file, File reference) {
		if (!reference.exists()) {
			throw new IllegalArgumentException("Reference file not found: " + reference);
		}
		return isNewer(file, reference.lastModified());
	}


	public static boolean isOlder(String file, String reference) {
		return isOlder(file(file), file(reference));
	}

	public static boolean isOlder(File file, File reference) {
		if (!reference.exists()) {
			throw new IllegalArgumentException("Reference file not found: " + reference);
		}
		return isOlder(file, reference.lastModified());
	}

	/**
	 * Tests if the specified <code>File</code> is newer than the specified time reference.
	 *
	 * @param file			the <code>File</code> of which the modification date must be compared.
	 * @param timeMillis	the time reference measured in milliseconds since the
	 * 						epoch (00:00:00 GMT, January 1, 1970)
	 * @return <code>true</code> if the <code>File</code> exists and has been modified after
	 *         the given time reference.
	 */
	public static boolean isNewer(File file, long timeMillis) {
		if (!file.exists()) {
			return false;
		}
		return file.lastModified() > timeMillis;
	}

	public static boolean isNewer(String file, long timeMillis) {
		return isNewer(file(file), timeMillis);
	}


	public static boolean isOlder(File file, long timeMillis) {
		if (!file.exists()) {
			return false;
		}
		return file.lastModified() < timeMillis;
	}

	public static boolean isOlder(String file, long timeMillis) {
		return isOlder(file(file), timeMillis);
	}


	// ---------------------------------------------------------------- smart copy

	public static void copy(String src, String dest) throws IOException {
		copy(file(src), file(dest), fileUtilParams);
	}

	public static void copy(String src, String dest, FileUtilParams params) throws IOException {
		copy(file(src), file(dest), params);
	}

	public static void copy(File src, File dest) throws IOException {
		copy(src, dest, fileUtilParams);
	}
	/**
	 * Smart copy. If source is a directory, copy it to destination.
	 * Otherwise, if destination is directory, copy source file to it.
	 * Otherwise, try to copy source file to destination file.
	 */
	public static void copy(File src, File dest, FileUtilParams params) throws IOException {
		if (src.isDirectory()) {
			copyDir(src, dest, params);
			return;
		}
		if (dest.isDirectory()) {
			copyFileToDir(src, dest, params);
			return;
		}
		copyFile(src, dest, params);
	}

	// ---------------------------------------------------------------- smart move

	public static void move(String src, String dest) throws IOException {
		move(file(src), file(dest), fileUtilParams);
	}

	public static void move(String src, String dest, FileUtilParams params) throws IOException {
		move(file(src), file(dest), params);
	}

	public static void move(File src, File dest) throws IOException {
		move(src, dest, fileUtilParams);
	}
	/**
	 * Smart move. If source is a directory, move it to destination.
	 * Otherwise, if destination is directory, move source file to it.
	 * Otherwise, try to move source file to destination file.
	 */
	public static void move(File src, File dest, FileUtilParams params) throws IOException {
		if (src.isDirectory()) {
			moveDir(src, dest);
			return;
		}
		if (dest.isDirectory()) {
			moveFileToDir(src, dest, params);
			return;
		}
		moveFile(src, dest, params);
	}


	// ---------------------------------------------------------------- smart delete

	public static void delete(String dest) throws IOException {
		delete(file(dest), fileUtilParams);
	}

	public static void delete(String dest, FileUtilParams params) throws IOException {
		delete(file(dest), params);
	}

	public static void delete(File dest) throws IOException {
		delete(dest, fileUtilParams);
	}

	/**
	 * Smart delete of destination file or directory.
	 */
	public static void delete(File dest, FileUtilParams params) throws IOException {
		if (dest.isDirectory()) {
			deleteDir(dest, params);
			return;
		}
		deleteFile(dest);
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Check if one file is an ancestor of second one.
	 *
	 * @param strict   if <code>false</code> then this method returns <code>true</code> if ancestor
	 *                 and file are equal
	 * @return <code>true</code> if ancestor is parent of file; <code>false</code> otherwise
	 */
	public static boolean isAncestor(File ancestor, File file, boolean strict) {
		File parent = strict ? getParentFile(file) : file;
		while (true) {
			if (parent == null) {
				return false;
			}
			if (parent.equals(ancestor)) {
				return true;
			}
			parent = getParentFile(parent);
		}
	}

	/**
	 * Returns parent for the file. The method correctly
	 * processes "." and ".." in file names. The name
	 * remains relative if was relative before.
	 * Returns <code>null</code> if the file has no parent.
	 */
	public static File getParentFile(final File file) {
		int skipCount = 0;
		File parentFile = file;
		while (true) {
			parentFile = parentFile.getParentFile();
			if (parentFile == null) {
				return null;
			}
			if (StringPool.DOT.equals(parentFile.getName())) {
				continue;
			}
			if (StringPool.DOTDOT.equals(parentFile.getName())) {
				skipCount++;
				continue;
			}
			if (skipCount > 0) {
				skipCount--;
				continue;
			}
			return parentFile;
		}
	}

	public static boolean isFilePathAcceptable(File file, FileFilter fileFilter) {
		do {
			if (fileFilter != null && !fileFilter.accept(file)) {
				return false;
			}
			file = file.getParentFile();
		} while (file != null);
		return true;
	}

	// ---------------------------------------------------------------- temp

	public static File createTempDirectory() throws IOException {
		return createTempDirectory(JoddCore.tempFilePrefix, null, null);
	}

	/**
	 * Creates temporary directory.
	 */
	public static File createTempDirectory(String prefix, String suffix) throws IOException {
		return createTempDirectory(prefix, suffix, null);
	}

	/**
	 * Creates temporary directory.
	 */
	public static File createTempDirectory(String prefix, String suffix, File tempDir) throws IOException {
		File file = createTempFile(prefix, suffix, tempDir);
		file.delete();
		file.mkdir();
		return file;
	}

	/**
	 * Simple method that creates temp file.
	 */
	public static File createTempFile() throws IOException {
		return createTempFile(JoddCore.tempFilePrefix, null, null, true);
	}

	/**
	 * Creates temporary file.
	 * If <code>create</code> is set to <code>true</code> file will be
	 * physically created on the file system. Otherwise, it will be created and then
	 * deleted - trick that will make temp file exist only if they are used.
	 */
	public static File createTempFile(String prefix, String suffix, File tempDir, boolean create) throws IOException {
		File file = createTempFile(prefix, suffix, tempDir);
		file.delete();
		if (create) {
			file.createNewFile();
		}
		return file;
	}

	/**
	 * Creates temporary file. Wraps java method and repeat creation several time
	 * if something fail.
	 */
	public static File createTempFile(String prefix, String suffix, File dir) throws IOException {
		int exceptionsCount = 0;
		while (true) {
			try {
				return File.createTempFile(prefix, suffix, dir).getCanonicalFile();
			} catch (IOException ioex) {	// fixes java.io.WinNTFileSystem.createFileExclusively access denied
				if (++exceptionsCount >= 50) {
					throw ioex;
				}
			}
		}
	}

	// ---------------------------------------------------------------- symlink

	/**
	 * Determines whether the specified file is a symbolic link rather than an actual file.
	 * Always returns <code>false</code> on Windows.
	 */
	public static boolean isSymlink(final File file) throws IOException {
		if (SystemUtil.isHostWindows()) {
			return false;
		}

		File fileInCanonicalDir;

		if (file.getParent() == null) {
			fileInCanonicalDir = file;
		} else {
			File canonicalDir = file.getParentFile().getCanonicalFile();
			fileInCanonicalDir = new File(canonicalDir, file.getName());
		}

		return !fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile());
	}

	// ---------------------------------------------------------------- digests

	/**
	 * Calculates digest for a file using provided algorithm.
	 */
	public static byte[] digest(final File file, MessageDigest algorithm) throws IOException {
		algorithm.reset();
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		DigestInputStream dis = new DigestInputStream(bis, algorithm);

		try {
			while (dis.read() != -1) {
			}
		}
		finally {
			StreamUtil.close(fis);
		}

		return algorithm.digest();
	}

	/**
	 * Creates MD5 digest of a file.
	 */
	public static String md5(final File file) throws IOException {
		MessageDigest md5Digest = null;
		try {
			md5Digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ignore) {
		}

		byte[] digest = digest(file, md5Digest);

		return StringUtil.toHexString(digest);
	}

	/**
	 * Creates SHA-1 digest of a file.
	 */
	public static String sha(final File file) throws IOException {
		MessageDigest md5Digest = null;
		try {
			md5Digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException ignore) {
		}

		byte[] digest = digest(file, md5Digest);

		return StringUtil.toHexString(digest);
	}

	/**
	 * Creates SHA-256 digest of a file.
	 */
	public static String sha256(final File file) throws IOException {
		MessageDigest md5Digest = null;
		try {
			md5Digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException ignore) {
		}

		byte[] digest = digest(file, md5Digest);

		return StringUtil.toHexString(digest);
	}

	/**
	 * Checks the start of the file for ASCII control characters
	 */
	public static boolean isBinary(final File file) throws IOException {
		byte[] bytes = readBytes(file, 128);

		for (byte b : bytes) {
			if (b < 32 && b != 9 && b != 10 && b != 13) {
				return true;
			}
		}

		return false;
	}
}