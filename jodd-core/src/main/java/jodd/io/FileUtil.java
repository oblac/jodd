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
import jodd.core.JoddCoreDefaults;
import jodd.util.StringPool;
import jodd.util.StringUtil;
import jodd.util.SystemUtil;
import jodd.util.net.URLDecoder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * File utilities.
 */
public class FileUtil {

	private static final String MSG_NOT_A_DIRECTORY = "Not a directory: ";
	private static final String MSG_CANT_CREATE = "Can't create: ";
	private static final String MSG_NOT_FOUND = "Not found: ";
	private static final String MSG_NOT_A_FILE = "Not a file: ";
	private static final String MSG_UNABLE_TO_DELETE = "Unable to delete: ";

	private static final JoddCoreDefaults DEFAULTS = JoddCore.get().defaults();
	private static final String DEFAULT_ENCODING = DEFAULTS.getEncoding();
	private static final String TEMP_PREFIX = DEFAULTS.getTempFilePrefix();
	private static final int ZERO = 0;
	private static final int NEGATIVE_ONE = -1;
	private static final String FILE_PROTOCOL = "file";

	private FileUtil() {
	}

	/**
	 * Simple factory for {@link File} objects but with home resolving.
	 */
	public static File file(String fileName) {
		fileName = StringUtil.replace(fileName, "~", SystemUtil.userHome());
		return new File(fileName);
	}

	/**
	 * Simple factory for {@link File} objects.
	 */
	private static File file(File parent, String fileName) {
		return new File(parent, fileName);
	}

	// ---------------------------------------------------------------- misc shortcuts

	/**
	 * @see #equals(File, File)
	 */
	public static boolean equals(String one, String two) {
		return equals(file(one), file(two));
	}

	/**
	 * Checks if two {@link File}s point to the same {@link File}.
	 *
	 * @param one {@link File} one.
	 * @param two {@link File} two.
	 * @return {@code true} if the {@link File}s match.
	 */
	public static boolean equals(File one, File two) {
		try {
			one = one.getCanonicalFile();
			two = two.getCanonicalFile();
		} catch (IOException ignore) {
			return false;
		}
		return one.equals(two);
	}

	/**
	 * Converts {@link File} {@link URL}s to {@link File}. Ignores other schemes and returns {@code null}.
	 */
	public static File toFile(URL url) {
		String fileName = toFileName(url);
		if (fileName == null) {
			return null;
		}
		return file(fileName);
	}

	/**
	 * Converts {@link File} to {@link URL} in a correct way.
	 *
	 * @return {@link URL} or {@code null} in case of error.
	 * @throws MalformedURLException if {@link File} cannot be converted.
	 */
	public static URL toURL(File file) throws MalformedURLException {
		return file.toURI().toURL();
	}

	/**
	 * Converts {@link File} {@link URL}s to file name. Accepts only {@link URL}s with 'file' protocol.
	 * Otherwise, for other schemes returns {@code null}.
	 *
	 * @param url {@link URL} to convert
	 * @return file name
	 */
	public static String toFileName(URL url) {
		if ((url == null) || !(url.getProtocol().equals(FILE_PROTOCOL))) {
			return null;
		}
		String filename = url.getFile().replace('/', File.separatorChar);

		return URLDecoder.decode(filename, DEFAULT_ENCODING);
	}

	/**
	 * Returns a file of either a folder or a containing archive.
	 */
	public static File toContainerFile(URL url) {
		String protocol = url.getProtocol();
		if (protocol.equals(FILE_PROTOCOL)) {
			return toFile(url);
		}

		String path = url.getPath();

		return new File(URI.create(
			path.substring(ZERO, path.lastIndexOf("!/"))));
	}

	/**
	 * Returns {@code true} if {@link File} exists.
	 */
	public static boolean isExistingFile(File file) {
		return file != null && file.exists() && file.isFile();
	}

	/**
	 * Returns {@code true} if directory exists.
	 */
	public static boolean isExistingFolder(File folder) {
		return folder != null && folder.exists() && folder.isDirectory();
	}

	// ---------------------------------------------------------------- mkdirs

	/**
	 * @see #mkdirs(File)
	 */
	public static void mkdirs(String dirs) throws IOException {
		mkdirs(file(dirs));
	}

	/**
	 * Creates all directories at once.
	 *
	 * @param dirs Directories to make.
	 * @throws IOException if cannot create directory.
	 */
	public static void mkdirs(File dirs) throws IOException {
		if (dirs.exists()) {
			checkIsDirectory(dirs);
			return;
		}
		checkCreateDirectory(dirs);
	}

	/**
	 * @see #mkdir(File)
	 */
	public static void mkdir(String dir) throws IOException {
		mkdir(file(dir));
	}

	/**
	 * Creates single directory.
	 *
	 * @throws IOException if cannot create directory.
	 */
	public static void mkdir(File dir) throws IOException {
		if (dir.exists()) {
			checkIsDirectory(dir);
			return;
		}
		checkCreateDirectory(dir);
	}

	// ---------------------------------------------------------------- touch

	/**
	 * @see #touch(File)
	 */
	public static void touch(String file) throws IOException {
		touch(file(file));
	}

	/**
	 * Implements the Unix "touch" utility. It creates a new {@link File}
	 * with size 0 or, if the {@link File} exists already, it is opened and
	 * closed without modifying it, but updating the {@link File} date and time.
	 */
	public static void touch(File file) throws IOException {
		if (!file.exists()) {
			StreamUtil.close(getFileOutputStream(file));
		}
		file.setLastModified(System.currentTimeMillis());
	}

	// ---------------------------------------------------------------- copy file to file

	/**
	 * @see #copyFile(File, File)
	 */
	public static void copyFile(String srcFile, String destFile) throws IOException {
		copyFile(file(srcFile), file(destFile));
	}

	/**
	 * Copies a {@link File} to another {@link File}.
	 *
	 * @param srcFile  Source {@link File}.
	 * @param destFile Destination {@link File}.
	 * @throws IOException if cannot copy
	 */
	public static void copyFile(File srcFile, File destFile) throws IOException {
		checkFileCopy(srcFile, destFile);
		doCopyFile(srcFile, destFile);
	}

	/**
	 * Internal file copy when most of the pre-checking has passed.
	 *
	 * @param srcFile  Source {@link File}.
	 * @param destFile Destination {@link File}.
	 * @throws IOException if cannot copy
	 */
	private static void doCopyFile(File srcFile, File destFile) throws IOException {
		if (destFile.exists()) {
			if (destFile.isDirectory()) {
				throw new IOException("Destination '" + destFile + "' is a directory");
			}
		}

		// do copy file
		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			input = getFileInputStream(srcFile);
			output = getFileOutputStream(destFile);
			StreamUtil.copy(input, output);
		} finally {
			StreamUtil.close(output);
			StreamUtil.close(input);
		}

		// done

		if (srcFile.length() != destFile.length()) {
			throw new IOException("Copy file failed of '" + srcFile + "' to '" + destFile + "' due to different sizes");
		}
		destFile.setLastModified(srcFile.lastModified());
	}

	// ---------------------------------------------------------------- copy file to directory

	/**
	 * @see #copyFileToDir(File, File)
	 */
	public static File copyFileToDir(String srcFile, String destDir) throws IOException {
		return copyFileToDir(file(srcFile), file(destDir));
	}

	/**
	 * Copies a {@link File} to directory with specified copy params and returns copied destination.
	 */
	public static File copyFileToDir(File srcFile, File destDir) throws IOException {
		checkExistsAndDirectory(destDir);
		File destFile = file(destDir, srcFile.getName());
		copyFile(srcFile, destFile);
		return destFile;
	}

	// ---------------------------------------------------------------- copy dir

	/**
	 * @see #copyDir(File, File)
	 */
	public static void copyDir(String srcDir, String destDir) throws IOException {
		copyDir(file(srcDir), file(destDir));
	}

	/**
	 * Copies directory with specified copy params.
	 *
	 * @see #doCopyDirectory(File, File)
	 */
	public static void copyDir(File srcDir, File destDir) throws IOException {
		checkDirCopy(srcDir, destDir);
		doCopyDirectory(srcDir, destDir);
	}

	/**
	 * @param srcDir
	 * @param destDir
	 * @throws IOException
	 */
	private static void doCopyDirectory(File srcDir, File destDir) throws IOException {
		if (destDir.exists()) {
			checkIsDirectory(destDir);
		} else {
			checkCreateDirectory(destDir);
			destDir.setLastModified(srcDir.lastModified());
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
					doCopyDirectory(file, destFile);
				} else {
					doCopyFile(file, destFile);
				}
			} catch (IOException ioex) {
				exception = ioex;
			}
		}

		if (exception != null) {
			throw exception;
		}
	}

	// ---------------------------------------------------------------- move file

	/**
	 * @see #moveFile(File, File)
	 */
	public static File moveFile(String srcFile, String destFile) throws IOException {
		return moveFile(file(srcFile), file(destFile));
	}

	/**
	 * @see #doMoveFile(File, File)
	 */
	public static File moveFile(File srcFile, File destFile) throws IOException {
		checkFileCopy(srcFile, destFile);
		doMoveFile(srcFile, destFile);
		return destFile;
	}

	/**
	 * Moves a {@link File}.
	 *
	 * @param srcFile  Source {@link File}.
	 * @param destFile Destination directory.
	 * @throws IOException
	 */
	private static void doMoveFile(File srcFile, File destFile) throws IOException {
		if (destFile.exists()) {
			checkIsFile(destFile);
			destFile.delete();
		}

		final boolean rename = srcFile.renameTo(destFile);
		if (!rename) {
			doCopyFile(srcFile, destFile);
			srcFile.delete();
		}
	}

	// ---------------------------------------------------------------- move file to dir

	/**
	 * @see #moveFileToDir(File, File)
	 */
	public static File moveFileToDir(String srcFile, String destDir) throws IOException {
		return moveFileToDir(file(srcFile), file(destDir));
	}

	/**
	 * Moves a file to a directory.
	 *
	 * @param srcFile Source {@link File}.
	 * @param destDir Destination directory.
	 * @throws IOException if there is an error during move.
	 */
	public static File moveFileToDir(File srcFile, File destDir) throws IOException {
		checkExistsAndDirectory(destDir);
		return moveFile(srcFile, file(destDir, srcFile.getName()));
	}

	// ---------------------------------------------------------------- move dir

	/**
	 * @see #moveDir(File, File)
	 */
	public static File moveDir(String srcDir, String destDir) throws IOException {
		return moveDir(file(srcDir), file(destDir));
	}

	/**
	 * @see #doMoveDirectory(File, File)
	 */
	public static File moveDir(File srcDir, File destDir) throws IOException {
		checkDirCopy(srcDir, destDir);
		doMoveDirectory(srcDir, destDir);
		return destDir;
	}

	/**
	 * Moves a directory.
	 *
	 * @param srcDest Source directory
	 * @param destDir Destination directory.
	 * @throws IOException if there is an error during move.
	 */
	private static void doMoveDirectory(File srcDest, File destDir) throws IOException {
		if (destDir.exists()) {
			checkIsDirectory(destDir);
			destDir = file(destDir, destDir.getName());
			destDir.mkdir();
		}

		final boolean rename = srcDest.renameTo(destDir);
		if (!rename) {
			doCopyDirectory(srcDest, destDir);
			deleteDir(srcDest);
		}
	}

	// ---------------------------------------------------------------- delete file

	/**
	 * @see #deleteFile(File)
	 */
	public static void deleteFile(String destFile) throws IOException {
		deleteFile(file(destFile));
	}

	/**
	 * Deletes a {@link File}.
	 *
	 * @param destFile Destination to delete.
	 * @throws IOException if there was an error deleting.
	 */
	public static void deleteFile(File destFile) throws IOException {
		checkIsFile(destFile);
		checkDeleteSuccessful(destFile);
	}

	// ---------------------------------------------------------------- delete dir

	/**
	 * @see #deleteDir(File)
	 */
	public static void deleteDir(String destDir) throws IOException {
		deleteDir(file(destDir));
	}

	/**
	 * Deletes a directory.
	 *
	 * @param destDir Destination to delete.
	 * @throws IOException if there was an error deleting.
	 */
	public static void deleteDir(File destDir) throws IOException {
		cleanDir(destDir);
		checkDeleteSuccessful(destDir);
	}

	/**
	 * @see #cleanDir(File)
	 */
	public static void cleanDir(String dest) throws IOException {
		cleanDir(file(dest));
	}

	/**
	 * Cleans a directory without deleting it.
	 *
	 * @param destDir destination to clean.
	 * @throws IOException if something went wrong.
	 */
	public static void cleanDir(File destDir) throws IOException {
		checkExists(destDir);
		checkIsDirectory(destDir);

		File[] files = destDir.listFiles();
		if (files == null) {
			throw new IOException("Failed to list contents of: " + destDir);
		}

		IOException exception = null;
		for (File file : files) {
			try {
				if (file.isDirectory()) {
					deleteDir(file);
				} else {
					file.delete();
				}
			} catch (IOException ioex) {
				exception = ioex;
				continue;
			}
		}

		if (exception != null) {
			throw exception;
		}
	}

	// ---------------------------------------------------------------- read/write chars

	/**
	 * @see #readUTFChars(File)
	 */
	public static char[] readUTFChars(String fileName) throws IOException {
		return readUTFChars(file(fileName));
	}

	/**
	 * Reads UTF file content as char array.
	 *
	 * @param file {@link File} to read.
	 * @return array of characters.
	 * @throws IOException if something went wrong.
	 */
	public static char[] readUTFChars(File file) throws IOException {
		checkExists(file);
		checkIsFile(file);

		UnicodeInputStream in = getUnicodeInputStream(file);
		try {
			return StreamUtil.readChars(in, detectEncoding(in));
		} finally {
			StreamUtil.close(in);
		}
	}

	/**
	 * Reads file content as char array.
	 *
	 * @param file     {@link File} to read.
	 * @param encoding Encoding to use.
	 * @return array of characters.
	 * @throws IOException if something went wrong.
	 */
	public static char[] readChars(File file, String encoding) throws IOException {
		checkExists(file);
		checkIsFile(file);

		InputStream in = getStream(file, encoding);
		try {
			return StreamUtil.readChars(in, encoding);
		} finally {
			StreamUtil.close(in);
		}
	}

	/**
	 * @see #readChars(String, String)
	 */
	public static char[] readChars(String fileName) throws IOException {
		return readChars(fileName, DEFAULT_ENCODING);
	}

	/**
	 * @see #readChars(File, String)
	 */
	public static char[] readChars(File file) throws IOException {
		return readChars(file, DEFAULT_ENCODING);
	}

	/**
	 * @see #readChars(File, String)
	 */
	public static char[] readChars(String fileName, String encoding) throws IOException {
		return readChars(file(fileName), encoding);
	}

	/**
	 * @see #writeChars(File, char[], String)
	 */
	public static void writeChars(File dest, char[] data) throws IOException {
		writeChars(dest, data, DEFAULT_ENCODING);
	}

	/**
	 * @see #writeChars(File, char[])
	 */
	public static void writeChars(String dest, char[] data) throws IOException {
		writeChars(file(dest), data);
	}

	/**
	 * @see #writeChars(File, char[], String)
	 */
	public static void writeChars(String dest, char[] data, String encoding) throws IOException {
		writeChars(file(dest), data, encoding);
	}

	/**
	 * Write characters. append = false
	 *
	 * @see #outChars(File, char[], String, boolean)
	 */
	public static void writeChars(File dest, char[] data, String encoding) throws IOException {
		outChars(dest, data, encoding, false);
	}

	/**
	 * Writes characters to {@link File} destination.
	 *
	 * @param dest     destination {@link File}
	 * @param data     Data as a {@link String}
	 * @param encoding Encoding as a {@link String}
	 * @param append   {@code true} if appending; {@code false} if {@link File} should be overwritten.
	 * @throws IOException if something went wrong.
	 */
	protected static void outChars(File dest, char[] data, String encoding, boolean append) throws IOException {
		if (dest.exists()) {
			checkIsFile(dest);
		}
		Writer out = new BufferedWriter(StreamUtil.getOutputStreamWriter(getFileOutputStream(dest, append), encoding));
		try {
			out.write(data);
		} finally {
			StreamUtil.close(out);
		}
	}

	// ---------------------------------------------------------------- read/write string

	/**
	 * @see #readUTFString(File)
	 */
	public static String readUTFString(String fileName) throws IOException {
		return readUTFString(file(fileName));
	}

	/**
	 * Detects optional BOM and reads UTF {@link String} from a {@link File}.
	 * If BOM is missing, UTF-8 is assumed.
	 *
	 * @param file {@link File} to read.
	 * @return String in UTF encoding.
	 * @throws IOException if copy to {@link InputStream} errors.
	 * @see #getUnicodeInputStream(File)
	 * @see StreamUtil#copy(InputStream, String)
	 */
	public static String readUTFString(File file) throws IOException {
		UnicodeInputStream in = getUnicodeInputStream(file);
		try {
			return StreamUtil.copy(in, detectEncoding(in)).toString();
		} finally {
			StreamUtil.close(in);
		}
	}

	/**
	 * Detects optional BOM and reads UTF {@link String} from an {@link InputStream}.
	 * If BOM is missing, UTF-8 is assumed.
	 *
	 * @param inputStream {@link InputStream} to read.
	 * @return String in UTF encoding.
	 * @throws IOException if copy to {@link InputStream} errors.
	 * @see #getUnicodeInputStream(File)
	 * @see StreamUtil#copy(InputStream, String)
	 */
	public static String readUTFString(InputStream inputStream) throws IOException {
		UnicodeInputStream in = null;
		try {
			in = new UnicodeInputStream(inputStream, null);
			return StreamUtil.copy(in, detectEncoding(in)).toString();
		} finally {
			StreamUtil.close(in);
		}
	}

	/**
	 * Reads {@link File} content as {@link String} encoded in provided encoding.
	 * For UTF encoded files, detects optional BOM characters.
	 *
	 * @param file     {@link File} to read.
	 * @param encoding Encoding to use.
	 * @return String representing {@link File} content.
	 * @throws IOException if copy to {@link InputStream} errors.
	 * @see #getStream(File, String)
	 * @see StreamUtil#copy(InputStream, String)
	 */
	public static String readString(File file, String encoding) throws IOException {
		checkExists(file);
		checkIsFile(file);
		InputStream in = getStream(file, encoding);
		try {
			return StreamUtil.copy(in, encoding).toString();
		} finally {
			StreamUtil.close(in);
		}
	}

	/**
	 * @see #readString(String, String)
	 */
	public static String readString(String source) throws IOException {
		return readString(source, DEFAULT_ENCODING);
	}

	/**
	 * @see #readString(File, String)
	 */
	public static String readString(String source, String encoding) throws IOException {
		return readString(file(source), encoding);
	}

	/**
	 * @see #readString(File, String)
	 */
	public static String readString(File source) throws IOException {
		return readString(source, DEFAULT_ENCODING);
	}

	/**
	 * @see #writeString(File, String, String)
	 */
	public static void writeString(String dest, String data) throws IOException {
		writeString(file(dest), data, DEFAULT_ENCODING);
	}

	/**
	 * @see #writeString(File, String, String)
	 */
	public static void writeString(String dest, String data, String encoding) throws IOException {
		writeString(file(dest), data, encoding);
	}

	/**
	 * @see #writeString(File, String, String)
	 */
	public static void writeString(File dest, String data) throws IOException {
		writeString(dest, data, DEFAULT_ENCODING);
	}

	/**
	 * Writes String. append = false
	 *
	 * @see #outString(File, String, String, boolean)
	 */
	public static void writeString(File dest, String data, String encoding) throws IOException {
		outString(dest, data, encoding, false);
	}

	/**
	 * @see #appendString(File, String)
	 */
	public static void appendString(String dest, String data) throws IOException {
		appendString(file(dest), data);
	}

	/**
	 * @see #appendString(File, String, String)
	 */
	public static void appendString(String dest, String data, String encoding) throws IOException {
		appendString(file(dest), data, encoding);
	}

	/**
	 * @see #appendString(File, String, String)
	 */
	public static void appendString(File dest, String data) throws IOException {
		appendString(dest, data, DEFAULT_ENCODING);
	}

	/**
	 * Appends String. append = true
	 *
	 * @see #outString(File, String, String, boolean)
	 */
	public static void appendString(File dest, String data, String encoding) throws IOException {
		outString(dest, data, encoding, true);
	}

	/**
	 * Writes data using encoding to {@link File}.
	 *
	 * @param dest     destination {@link File}
	 * @param data     Data as a {@link String}
	 * @param encoding Encoding as a {@link String}
	 * @param append   {@code true} if appending; {@code false} if {@link File} should be overwritten.
	 * @throws IOException if something went wrong.
	 */
	protected static void outString(File dest, String data, String encoding, boolean append) throws IOException {
		if (dest.exists()) {
			checkIsFile(dest);
		}
		FileOutputStream out = null;
		try {
			out = getFileOutputStream(dest, append);
			out.write(data.getBytes(encoding));
		} finally {
			StreamUtil.close(out);
		}
	}

	// ---------------------------------------------------------------- stream


	/**
	 * @see #writeStream(File, InputStream)
	 */
	public static void writeStream(String dest, InputStream in) throws IOException {
		writeStream(file(dest), in);
	}

	/**
	 * @see #writeStream(FileOutputStream, InputStream)
	 */
	public static void writeStream(File dest, InputStream in) throws IOException {
		writeStream(getFileOutputStream(dest), in);
	}

	/**
	 * Write {@link InputStream} in to {@link FileOutputStream}.
	 *
	 * @param out {@link FileOutputStream} to write to.
	 * @param in  {@link InputStream} to read.
	 * @throws IOException if there is an issue reading/writing.
	 */
	public static void writeStream(FileOutputStream out, InputStream in) throws IOException {
		try {
			StreamUtil.copy(in, out);
		} finally {
			StreamUtil.close(out);
		}
	}

	// ---------------------------------------------------------------- read/write string lines

	/**
	 * @see #readLines(String, String)
	 */
	public static String[] readLines(String source) throws IOException {
		return readLines(source, DEFAULT_ENCODING);
	}

	/**
	 * @see #readLines(File, String)
	 */
	public static String[] readLines(String source, String encoding) throws IOException {
		return readLines(file(source), encoding);
	}

	/**
	 * @see #readLines(File, String)
	 */
	public static String[] readLines(File source) throws IOException {
		return readLines(source, DEFAULT_ENCODING);
	}

	/**
	 * Reads lines from source {@link File} with specified encoding and returns lines as {@link String}s in array.
	 *
	 * @param file     {@link File} to read.
	 * @param encoding Endoing to use.
	 * @return array of Strings which represents lines in the {@link File}.
	 * @throws IOException if {@link File} does not exist or is not a {@link File} or there is an issue reading
	 *                     the {@link File}.
	 */
	public static String[] readLines(File file, String encoding) throws IOException {
		checkExists(file);
		checkIsFile(file);
		List<String> list = new ArrayList<>();

		InputStream in = getStream(file, encoding);
		try {
			BufferedReader br = new BufferedReader(StreamUtil.getInputStreamReader(in, encoding));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				list.add(strLine);
			}
		} finally {
			StreamUtil.close(in);
		}
		return list.toArray(new String[list.size()]);
	}

	// ---------------------------------------------------------------- read/write byte array

	/**
	 * @see #readBytes(File)
	 */
	public static byte[] readBytes(String file) throws IOException {
		return readBytes(file(file));
	}

	/**
	 * @see #readBytes(File, int)
	 */
	public static byte[] readBytes(File file) throws IOException {
		return readBytes(file, NEGATIVE_ONE);
	}

	/**
	 * Read file and returns byte array with contents.
	 *
	 * @param file  {@link File} to read
	 * @param count number of bytes to read
	 * @return byte array from {@link File} contents.
	 * @throws IOException if not a {@link File} or {@link File} does not exist or file size is
	 *                     larger than {@link Integer#MAX_VALUE}.
	 */
	public static byte[] readBytes(File file, int count) throws IOException {
		checkExists(file);
		checkIsFile(file);
		long numToRead = file.length();
		if (numToRead >= Integer.MAX_VALUE) {
			throw new IOException("File is larger then max array size");
		}

		if (count > NEGATIVE_ONE && count < numToRead) {
			numToRead = count;
		}

		byte[] bytes = new byte[(int) numToRead];
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
		randomAccessFile.readFully(bytes);
		randomAccessFile.close();

		return bytes;
	}

	/**
	 * @see #writeBytes(File, byte[])
	 */
	public static void writeBytes(String dest, byte[] data) throws IOException {
		writeBytes(file(dest), data);
	}

	/**
	 * @see #writeBytes(File, byte[], int, int)
	 */
	public static void writeBytes(File dest, byte[] data) throws IOException {
		writeBytes(dest, data, ZERO, data.length);
	}

	/**
	 * @see #writeBytes(File, byte[], int, int)
	 */
	public static void writeBytes(String dest, byte[] data, int off, int len) throws IOException {
		writeBytes(file(dest), data, off, len);
	}

	/**
	 * Write bytes. append = false
	 *
	 * @see #outBytes(File, byte[], int, int, boolean)
	 */
	public static void writeBytes(File dest, byte[] data, int off, int len) throws IOException {
		outBytes(dest, data, off, len, false);
	}

	/**
	 * @see #appendBytes(File, byte[])
	 */
	public static void appendBytes(String dest, byte[] data) throws IOException {
		appendBytes(file(dest), data);
	}

	/**
	 * @see #appendBytes(File, byte[], int, int)
	 */
	public static void appendBytes(String dest, byte[] data, int off, int len) throws IOException {
		appendBytes(file(dest), data, off, len);
	}

	/**
	 * @see #appendBytes(File, byte[], int, int)
	 */
	public static void appendBytes(File dest, byte[] data) throws IOException {
		appendBytes(dest, data, ZERO, data.length);
	}

	/**
	 * Appends bytes. append = true
	 *
	 * @see #outBytes(File, byte[], int, int, boolean)
	 */
	public static void appendBytes(File dest, byte[] data, int off, int len) throws IOException {
		outBytes(dest, data, off, len, true);
	}

	/**
	 * Writes data to {@link File} destination.
	 *
	 * @param dest   destination {@link File}
	 * @param data   Data as a {@link String}
	 * @param off    the start offset in the data.
	 * @param len    the number of bytes to write.
	 * @param append {@code true} if appending; {@code false} if {@link File} should be overwritten.
	 * @throws IOException if something went wrong.
	 */
	protected static void outBytes(File dest, byte[] data, int off, int len, boolean append) throws IOException {
		if (dest.exists()) {
			checkIsFile(dest);
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
	 * Compare the contents of two {@link File}s to determine if they are equal or
	 * not.
	 * <p>
	 * This method checks to see if the two {@link File}s are different lengths
	 * or if they point to the same {@link File}, before resorting to byte-by-byte
	 * comparison of the contents.
	 * <p>
	 * Code origin: Avalon
	 */
	public static boolean compare(File one, File two) throws IOException {
		boolean file1Exists = one.exists();
		if (file1Exists != two.exists()) {
			return false;
		}

		if (!file1Exists) {
			return true;
		}

		if ((!one.isFile()) || (!two.isFile())) {
			throw new IOException("Only files can be compared");
		}

		if (one.length() != two.length()) {
			return false;
		}

		if (equals(one, two)) {
			return true;
		}

		InputStream input1 = null;
		InputStream input2 = null;
		try {
			input1 = getFileInputStream(one);
			input2 = getFileInputStream(two);
			return StreamUtil.compare(input1, input2);
		} finally {
			StreamUtil.close(input1);
			StreamUtil.close(input2);
		}
	}

	// ---------------------------------------------------------------- time

	/**
	 * @see #isOlder(File, File)
	 */
	public static boolean isOlder(String file, String reference) {
		return isOlder(file(file), file(reference));
	}

	/**
	 * @see #isNewer(File, File)
	 */
	public static boolean isNewer(String file, String reference) {
		return isNewer(file(file), file(reference));
	}

	/**
	 * Uses {@link File#lastModified()} for reference.
	 *
	 * @see #isNewer(File, long)
	 */
	public static boolean isNewer(File file, File reference) {
		checkReferenceExists(reference);
		return isNewer(file, reference.lastModified());
	}

	/**
	 * Uses {@link File#lastModified()} for reference.
	 *
	 * @see #isOlder(File, long)
	 */
	public static boolean isOlder(File file, File reference) {
		checkReferenceExists(reference);
		return isOlder(file, reference.lastModified());
	}

	/**
	 * Tests if the specified {@link File} is newer than the specified time reference.
	 *
	 * @param file       the {@link File} of which the modification date must be compared.
	 * @param timeMillis the time reference measured in milliseconds since the
	 *                   epoch (00:00:00 GMT, January 1, 1970)
	 * @return {@code true} if the {@link File} exists and has been modified after
	 * the given time reference.
	 */
	public static boolean isNewer(File file, long timeMillis) {
		return file.exists() && file.lastModified() > timeMillis;
	}

	/**
	 * @see #isNewer(File, long)
	 */
	public static boolean isNewer(String file, long timeMillis) {
		return isNewer(file(file), timeMillis);
	}

	/**
	 * Tests if the specified {@link File} is older than the specified time reference.
	 *
	 * @param file       the {@link File} of which the modification date must be compared.
	 * @param timeMillis the time reference measured in milliseconds since the
	 *                   epoch (00:00:00 GMT, January 1, 1970)
	 * @return {@code true} if the {@link File} exists and has been modified after
	 * the given time reference.
	 */
	public static boolean isOlder(File file, long timeMillis) {
		return file.exists() && file.lastModified() < timeMillis;
	}

	/**
	 * @see #isOlder(File, long)
	 */
	public static boolean isOlder(String file, long timeMillis) {
		return isOlder(file(file), timeMillis);
	}

	// ---------------------------------------------------------------- smart copy

	/**
	 * @see #copy(File, File)
	 */
	public static void copy(String src, String dest) throws IOException {
		copy(file(src), file(dest));
	}

	/**
	 * Smart copy. If source is a directory, copy it to destination.
	 * Otherwise, if destination is directory, copy source file to it.
	 * Otherwise, try to copy source file to destination file.
	 *
	 * @param src  source {@link File}
	 * @param dest destination {@link File}
	 * @throws IOException if there is an error copying.
	 * @see #copyDir(File, File)
	 * @see #copyFileToDir(File, File)
	 * @see #copyFile(File, File)
	 */
	public static void copy(File src, File dest) throws IOException {
		if (src.isDirectory()) {
			copyDir(src, dest);
			return;
		}
		if (dest.isDirectory()) {
			copyFileToDir(src, dest);
			return;
		}
		copyFile(src, dest);
	}

	// ---------------------------------------------------------------- smart move

	/**
	 * @see #move(File, File)
	 */
	public static void move(String src, String dest) throws IOException {
		move(file(src), file(dest));
	}

	/**
	 * Smart move. If source is a directory, move it to destination.
	 * Otherwise, if destination is directory, move source {@link File} to it.
	 * Otherwise, try to move source {@link File} to destination {@link File}.
	 *
	 * @param src  source {@link File}
	 * @param dest destination {@link File}
	 * @throws IOException if there is an error moving.
	 * @see #moveDir(File, File)
	 * @see #moveFileToDir(File, File)
	 * @see #moveFile(File, File)
	 */
	public static void move(File src, File dest) throws IOException {
		if (src.isDirectory()) {
			moveDir(src, dest);
			return;
		}
		if (dest.isDirectory()) {
			moveFileToDir(src, dest);
			return;
		}
		moveFile(src, dest);
	}


	// ---------------------------------------------------------------- smart delete

	/**
	 * @see #delete(File)
	 */
	public static void delete(String dest) throws IOException {
		delete(file(dest));
	}

	/**
	 * Smart delete of destination file or directory.
	 *
	 * @throws IOException if there is an issue deleting the file/directory.
	 * @see #deleteFile(File)
	 * @see #deleteDir(File)
	 */
	public static void delete(File dest) throws IOException {
		if (dest.isDirectory()) {
			deleteDir(dest);
			return;
		}
		deleteFile(dest);
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Check if one {@link File} is an ancestor of second one.
	 *
	 * @param strict if c then this method returns {@code true} if ancestor
	 *               and {@link File} are equal
	 * @return {@code true} if ancestor is parent of {@link File}; otherwise, {@code false}
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
	 * processes "." and ".." in {@link File} names. The name
	 * remains relative if was relative before.
	 * Returns {@code null} if the {@link File} has no parent.
	 *
	 * @param file {@link File}
	 * @return {@code null} if the {@link File} has no parent.
	 */
	public static File getParentFile(final File file) {
		int skipCount = ZERO;
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
			if (skipCount > ZERO) {
				skipCount--;
				continue;
			}
			return parentFile;
		}
	}

	/**
	 * Checks if file and its ancestors are acceptable by using {@link FileFilter#accept(File)}.
	 *
	 * @param file       {@link File} to check.
	 * @param fileFilter {@link FileFilter} to use.
	 * @return if file and its ancestors are acceptable
	 */
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

	/**
	 * @see #createTempDirectory(String, String)
	 */
	public static File createTempDirectory() throws IOException {
		return createTempDirectory(TEMP_PREFIX, null);
	}

	/**
	 * @see #createTempDirectory(String, String, File)
	 */
	public static File createTempDirectory(String prefix, String suffix) throws IOException {
		return createTempDirectory(prefix, suffix, null);
	}

	/**
	 * Creates temporary directory.
	 *
	 * @see #createTempFile(String, String, File)
	 */
	public static File createTempDirectory(String prefix, String suffix, File tempDir) throws IOException {
		File file = createTempFile(prefix, suffix, tempDir);
		file.delete();
		file.mkdir();
		return file;
	}

	/**
	 * @see #createTempFile(String, String, File, boolean)
	 */
	public static File createTempFile() throws IOException {
		return createTempFile(TEMP_PREFIX, null, null, true);
	}

	/**
	 * Creates temporary {@link File}.
	 *
	 * @param prefix  The prefix string to be used in generating the file's
	 *                name; must be at least three characters long
	 * @param suffix  The suffix string to be used in generating the file's
	 *                name; may be {@code null}, in which case the
	 *                suffix {@code ".tmp"} will be used
	 * @param tempDir The directory in which the file is to be created, or
	 *                {@code null} if the default temporary-file
	 *                directory is to be used
	 * @param create  If {@code create} is set to {@code true} {@link File} will be
	 *                physically created on the file system. Otherwise, it will be created and then
	 *                deleted - trick that will make temp {@link File} exist only if they are used.
	 * @return File
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
	 * Creates temporary {@link File}. Wraps Java method and repeats creation several times
	 * if something fails.
	 *
	 * @param prefix  The prefix string to be used in generating the file's
	 *                name; must be at least three characters long
	 * @param suffix  The suffix string to be used in generating the file's
	 *                name; may be {@code null}, in which case the
	 *                suffix {@code ".tmp"} will be used
	 * @param tempDir The directory in which the file is to be created, or
	 *                {@code null} if the default temporary-file
	 *                directory is to be used
	 */
	public static File createTempFile(String prefix, String suffix, File tempDir) throws IOException {
		int exceptionsCount = ZERO;
		while (true) {
			try {
				return File.createTempFile(prefix, suffix, tempDir).getCanonicalFile();
			} catch (IOException ioex) {  // fixes java.io.WinNTFileSystem.createFileExclusively access denied
				if (++exceptionsCount >= 50) {
					throw ioex;
				}
			}
		}
	}

	// ---------------------------------------------------------------- symlink

	/**
	 * Determines whether the specified file is a symbolic link rather than an actual file.
	 *
	 * @deprecated {@link java.nio.file.Files#isSymbolicLink(java.nio.file.Path)} provides this functionality natively as of Java 1.7.
	 */
	@Deprecated
	public static boolean isSymlink(final File file) {
		return Files.isSymbolicLink(file.toPath());
	}

	// ---------------------------------------------------------------- digests

	/**
	 * Creates MD5 digest of a {@link File}.
	 *
	 * @param file {@link File} to create digest of.
	 * @return MD5 digest of the {@link File}.
	 */
	public static String md5(final File file) throws IOException {
		return _createDigestOfFileWithAlgorithm(file, "MD5");
	}

	/**
	 * Creates SHA-1 digest of a file.
	 *
	 * @param file {@link File} to create digest of.
	 * @return SHA-1 digest of the {@link File}.
	 */
	public static String sha1(final File file) throws IOException {
		return _createDigestOfFileWithAlgorithm(file, "SHA-1");
	}

	/**
	 * Creates SHA-256 digest of a file.
	 *
	 * @param file {@link File} to create digest of.
	 * @return SHA-256 digest of the {@link File}.
	 */
	public static String sha256(final File file) throws IOException {
		return _createDigestOfFileWithAlgorithm(file, "SHA-256");
	}

	/**
	 * Creates SHA-384 digest of a file.
	 *
	 * @param file {@link File} to create digest of.
	 * @return SHA-384 digest of the {@link File}.
	 */
	public static String sha384(final File file) throws IOException {
		return _createDigestOfFileWithAlgorithm(file, "SHA-384");
	}

	/**
	 * Creates SHA-512 digest of a file.
	 *
	 * @param file {@link File} to create digest of.
	 * @return SHA-512 digest of the {@link File}.
	 */
	public static String sha512(final File file) throws IOException {
		return _createDigestOfFileWithAlgorithm(file, "SHA-512");
	}

	/**
	 * Create a digest (as String) of a {@link File}.
	 *
	 * @param file      {@link File} to create digest of.
	 * @param algorithm Algorithm to use for {@link MessageDigest#getInstance(String)}.
	 * @return digest of the {@link File}.
	 * @throws IOException if there is an error creating digest.
	 */
	private static String _createDigestOfFileWithAlgorithm(final File file, final String algorithm) throws IOException {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException ignore) {
		}

		byte[] digest = digest(file, messageDigest);

		return StringUtil.toHexString(digest);
	}

	/**
	 * Calculates digest for a {@link File} using provided algorithm.
	 *
	 * @param file      {@link File} to create digest of.
	 * @param algorithm Algorithm to use from {@link MessageDigest#getInstance(String)}.
	 * @return digest for the {@link File}.
	 * @throws IOException if something went wrong.
	 */
	public static byte[] digest(final File file, MessageDigest algorithm) throws IOException {
		algorithm.reset();
		FileInputStream fis = getFileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		DigestInputStream dis = new DigestInputStream(bis, algorithm);

		try {
			while (dis.read() != NEGATIVE_ONE) {
			}
		} finally {
			StreamUtil.close(dis);
			StreamUtil.close(bis);
			StreamUtil.close(fis);
		}

		return algorithm.digest();
	}

	/**
	 * Checks the start of the file for ASCII control characters
	 *
	 * @param file {@link File}
	 * @return true if the the start of the {@link File} is ASCII control characters.
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

	/**
	 * Returns new {@link FileInputStream}.
	 *
	 * @param file {@link File}
	 * @return new {@link FileInputStream}.
	 * @throws IOException if something went wrong.
	 */
	private static FileInputStream getFileInputStream(File file) throws IOException {
		return new FileInputStream(file);
	}

	/**
	 * @see #getFileOutputStream(File, boolean)
	 */
	private static FileOutputStream getFileOutputStream(File file) throws IOException {
		return getFileOutputStream(file, false);
	}

	/**
	 * Returns new {@link FileOutputStream}.
	 *
	 * @param file     {@link File}
	 * @param isAppend if {@code true}, then bytes will be written
	 *                 to the end of the file rather than the beginning
	 * @return new {@link FileOutputStream}.
	 * @throws IOException
	 */
	private static FileOutputStream getFileOutputStream(File file, boolean isAppend) throws IOException {
		return new FileOutputStream(file, isAppend);
	}

	/**
	 * @see #getUnicodeInputStream(InputStream, String)
	 * @see #checkExists(File)
	 * @see #checkIsFile(File)
	 * @see #getFileInputStream(File)
	 */
	private static UnicodeInputStream getUnicodeInputStream(File file) throws IOException {
		checkExists(file);
		checkIsFile(file);
		return getUnicodeInputStream(getFileInputStream(file), null);
	}

	/**
	 * Returns new {@link UnicodeInputStream} using {@link InputStream} and targetEncoding.
	 *
	 * @param input          {@link InputStream}
	 * @param targetEncoding Encoding to use.
	 * @return new {@link UnicodeInputStream}.
	 */
	private static UnicodeInputStream getUnicodeInputStream(InputStream input, String targetEncoding) {
		return new UnicodeInputStream(input, targetEncoding);
	}

	/**
	 * Returns either new {@link FileInputStream} or new {@link UnicodeInputStream}.
	 *
	 * @return either {@link FileInputStream} or {@link UnicodeInputStream}.
	 * @throws IOException if something went wrong.
	 * @see #getFileInputStream(File)
	 * @see #getUnicodeInputStream(InputStream, String)
	 */
	private static InputStream getStream(File file, String encoding) throws IOException {
		InputStream in = getFileInputStream(file);
		if (encoding.startsWith("UTF")) {
			in = getUnicodeInputStream(in, encoding);
		}
		return in;
	}

	/**
	 * Detect encoding on {@link UnicodeInputStream} by using {@link UnicodeInputStream#getDetectedEncoding()}.
	 *
	 * @param in {@link UnicodeInputStream}
	 * @return UTF encoding as a String. If encoding could not be detected, defaults to {@link StringPool#UTF_8}.
	 * @see UnicodeInputStream#getDetectedEncoding()
	 */
	private static String detectEncoding(UnicodeInputStream in) {
		String encoding = in.getDetectedEncoding();
		if (encoding == null) {
			encoding = StringPool.UTF_8;
		}
		return encoding;
	}

	/**
	 * Checks if {@link File} exists. Throws IOException if not.
	 *
	 * @param file {@link File}
	 * @throws FileNotFoundException if file does not exist.
	 */
	private static void checkExists(File file) throws FileNotFoundException {
		if (!file.exists()) {
			throw new FileNotFoundException(MSG_NOT_FOUND + file);
		}
	}

	/**
	 * Checks if {@link File} exists. Throws IllegalArgumentException if not.
	 *
	 * @param file {@link File}
	 * @throws IllegalArgumentException if file does not exist.
	 */
	private static void checkReferenceExists(File file) throws IllegalArgumentException {
		try {
			checkExists(file);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Reference file not found: " + file);
		}
	}

	/**
	 * Checks if {@link File} is a file. Throws IOException if not.
	 *
	 * @param file {@link File}
	 * @throws IOException if {@link File} is not a file.
	 */
	private static void checkIsFile(File file) throws IOException {
		if (!file.isFile()) {
			throw new IOException(MSG_NOT_A_FILE + file);
		}
	}

	/**
	 * Checks if {@link File} is a directory. Throws IOException if not.
	 *
	 * @param dir Directory
	 * @throws IOException if {@link File} is not a directory.
	 */
	private static void checkIsDirectory(File dir) throws IOException {
		if (!dir.isDirectory()) {
			throw new IOException(MSG_NOT_A_DIRECTORY + dir);
		}
	}

	/**
	 * Checks if directory exists. Throws IOException if it does not.
	 *
	 * @param dir Directory
	 * @throws IOException if directory does not exist.
	 * @see #checkIsDirectory(File)
	 */
	private static void checkExistsAndDirectory(File dir) throws IOException {
		if (dir.exists()) {
			checkIsDirectory(dir);
		}
	}

	/**
	 * Checks if directory can be created. Throws IOException if it cannot.
	 * <p>
	 * This actually creates directory (and its ancestors) (as per {@link File#mkdirs()} }).
	 *
	 * @param dir Directory
	 * @throws IOException if directory cannot be created.
	 */
	private static void checkCreateDirectory(File dir) throws IOException {
		if (!dir.mkdirs()) {
			throw new IOException(MSG_CANT_CREATE + dir);
		}
	}

	/**
	 * Checks if directory can be deleted. Throws IOException if it cannot.
	 * This actually deletes directory (as per {@link File#delete()}).
	 *
	 * @param dir Directory
	 * @throws IOException if directory cannot be created.
	 */
	private static void checkDeleteSuccessful(File dir) throws IOException {
		if (!dir.delete()) {
			throw new IOException(MSG_UNABLE_TO_DELETE + dir);
		}
	}

	/**
	 * Checks that srcDir exists, that it is a directory and if srcDir and destDir are not equal.
	 *
	 * @param srcDir  Source directory
	 * @param destDir Destination directory
	 * @throws IOException if any of the above conditions are not true.
	 */
	private static void checkDirCopy(File srcDir, File destDir) throws IOException {
		checkExists(srcDir);
		checkIsDirectory(srcDir);
		if (equals(srcDir, destDir)) {
			throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are equal");
		}
	}


	/**
	 * Checks that file copy can occur.
	 *
	 * @param srcFile  Source {@link File}
	 * @param destFile Destination {@link File}
	 * @throws IOException if srcFile does not exist or is not a file or
	 *                     srcFile and destFile are equal or cannot create ancestor directories.
	 */
	private static void checkFileCopy(File srcFile, File destFile) throws IOException {
		checkExists(srcFile);
		checkIsFile(srcFile);
		if (equals(srcFile, destFile)) {
			throw new IOException("Files '" + srcFile + "' and '" + destFile + "' are equal");
		}

		File destParent = destFile.getParentFile();
		if (destParent != null && !destParent.exists()) {
			checkCreateDirectory(destParent);
		}
	}
}
