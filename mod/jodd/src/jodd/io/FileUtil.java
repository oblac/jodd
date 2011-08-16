// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import jodd.JoddDefault;
import jodd.util.StringPool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileFilter;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

/**
 * File utilities.
 */
public class FileUtil {

	// ---------------------------------------------------------------- misc shortcuts

	/**
	 * Checks if two files points to the same file.
	 */
	public static boolean equals(String file1, String file2) {
		return equals(new File(file1), new File(file2));
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
	 * Converts URI to file. Returns <code>null</code> in case of invalid URI.
	 */
	public static File toFile(URI uri) {
		try {
			return new File(uri);
		} catch (IllegalArgumentException ignore) {
			return null;
		}
	}

	/**
	 * Converts file URLs to file. Ignores other schemes and returns <code>null</code>.
	 */
	public static File toFile(URL url) {
		String fileName = toFileName(url);
		if (fileName == null) {
			return null;
		}
		return new File(fileName);
	}

	/**
	 * Converts file URLs to file name. Ignores other schemes and returns <code>null</code>.
	 */
	public static String toFileName(URL url) {
		if ((url == null) || (url.getProtocol().equals("file") == false)) {
			return null;
		}
		String filename = url.getFile().replace('/', File.separatorChar);
		int pos = 0;
		while ((pos = filename.indexOf('%', pos)) >= 0) {
			if (pos + 2 < filename.length()) {
				String hexStr = filename.substring(pos + 1, pos + 3);
				char ch = (char) Integer.parseInt(hexStr, 16);
				filename = filename.substring(0, pos) + ch + filename.substring(pos + 3);
			}
		}
		return filename;
	}

	/**
	 * Converts array of URLS to file names string. Other schemes are ignored.
	 */
	public static String toFileNames(URL[] urls) {
		StringBuilder path = new StringBuilder();
		for (URL url : urls) {
			String fileName = toFileName(url);
			if (fileName == null) {
				continue;
			}
			path.append(fileName).append(File.pathSeparatorChar);
		}
		return path.toString();
	}


	// ---------------------------------------------------------------- mkdirs

	/**
	 * Creates all folders at once.
	 */
	public static void mkdirs(String dirs) throws IOException {
		mkdirs(new File(dirs));
	}
	/**
	 * Creates all folders at once.
	 */
	public static void mkdirs(File dirs) throws IOException {
		if (dirs.exists()) {
			if (dirs.isDirectory() == false) {
				throw new IOException("Directory '" + "' is not a directory.");
			}
			return;
		}
		if (dirs.mkdirs() == false) {
			throw new IOException("Unable to create directory '" + dirs + "'.");
		}
	}

	/**
	 * Creates single folder.
	 */
	public static void mkdir(String dir) throws IOException {
		mkdir(new File(dir));
	}
	/**
	 * Creates single folders.
	 */
	public static void mkdir(File dir) throws IOException {
		if (dir.exists()) {
			if (dir.isDirectory() == false) {
				throw new IOException("Destination '" + "' is not a directory.");
			}
			return;
		}
		if (dir.mkdir() == false) {
			throw new IOException("Unable to create directory '" + dir + "'.");
		}
	}

	// ---------------------------------------------------------------- touch

	public static void touch(String file) throws IOException {
		touch(new File(file));
	}

	/**
	 * Implements the Unix "touch" utility. It creates a new file
	 * with size 0 or, if the file exists already, it is opened and
	 * closed without modifying it, but updating the file date and time.
	 */
	public static void touch(File file) throws IOException {
		if (file.exists() == false) {
			StreamUtil.close(new FileOutputStream(file));
		}
		file.setLastModified(System.currentTimeMillis());
	}


	// ---------------------------------------------------------------- params

	// default global FileUtilParams
	public static FileUtilParams defaultParams = new FileUtilParams();

	/**
	 * Creates new {@link FileUtilParams} instance by cloning current default params.
	 */
	public static FileUtilParams cloneParams() {
		try {
			return defaultParams.clone();
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

	public static void copyFile(String src, String dest) throws IOException {
		copyFile(new File(src), new File(dest), defaultParams);
	}

	public static void copyFile(String src, String dest, FileUtilParams params) throws IOException {
		copyFile(new File(src), new File(dest), params);
	}

	public static void copyFile(File src, File dest) throws IOException {
		copyFile(src, dest, defaultParams);
	}

	/**
	 * Copies a file to another file with specified copy params.
	 */
	public static void copyFile(File src, File dest, FileUtilParams params) throws IOException {
		checkFileCopy(src, dest, params);
		doCopyFile(src, dest, params);
	}

	private static void checkFileCopy(File src, File dest, FileUtilParams params) throws IOException {
		if (src.exists() == false) {
			throw new FileNotFoundException("Source '" + src + "' does not exist.");
		}
		if (src.isFile() == false) {
			throw new IOException("Source '" + src + "' is not a file.");
		}
		if (equals(src, dest) == true) {
			throw new IOException("Source '" + src + "' and destination '" + dest + "' are the same.");
		}

		File destParent = dest.getParentFile();
		if (destParent != null && destParent.exists() == false) {
			if (params.createDirs == false) {
				throw new IOException("Destination directory '" + destParent + "' doesn't exist.");
			}
			if (destParent.mkdirs() == false) {
				throw new IOException("Destination directory '" + destParent + "' cannot be created.");
			}
		}
	}

	/**
	 * Internal file copy when most of the pre-checking has passed.
	 */
	private static void doCopyFile(File src, File dest, FileUtilParams params) throws IOException {
		if (dest.exists()) {
			if (dest.isDirectory()) {
				throw new IOException("Destination '" + dest + "' is a directory.");
			}
			if (params.overwrite == false) {
				throw new IOException("Destination '" + dest + "' already exists.");
			}
		}

		doCopy(src, dest);

		if (src.length() != dest.length()) {
			throw new IOException("Copying of '" + src + "' to '" + dest + "' failed due to different sizes.");
		}
		if (params.preserveDate) {
			dest.setLastModified(src.lastModified());
		}
	}

	// ---------------------------------------------------------------- simple copy file

	/**
	 * Copies one file to another without any checking.
	 * @see #doCopy(java.io.File, java.io.File)
	 */
	protected static void doCopy(String src, String dest) throws IOException {
		doCopy(new File(src), new File(dest));
	}

	/**
	 * Copies one file to another without any checking. It is assumed that
	 * both parameters represents valid files.
	 */
	protected static void doCopy(File src, File dest) throws IOException {
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
	}


	// ---------------------------------------------------------------- copy file to directory

	/**
	 * @see #copyFileToDir(java.io.File, java.io.File, FileUtilParams)
	 */
	public static File copyFileToDir(String src, String destDir) throws IOException {
		return copyFileToDir(new File(src), new File(destDir), defaultParams);
	}
	/**
	 * @see #copyFileToDir(java.io.File, java.io.File, FileUtilParams)
	 */
	public static File copyFileToDir(String src, String destDir, FileUtilParams params) throws IOException {
		return copyFileToDir(new File(src), new File(destDir), params);
	}
	/**
	 * @see #copyFileToDir(java.io.File, java.io.File, FileUtilParams)
	 */
	public static File copyFileToDir(File src, File destDir) throws IOException {
		return copyFileToDir(src, destDir, defaultParams);
	}
	/**
	 * Copies a file to folder with specified copy params and returns copied destination.
	 */
	public static File copyFileToDir(File src, File destDir, FileUtilParams params) throws IOException {
		if (destDir.exists() && destDir.isDirectory() == false) {
			throw new IOException("Destination '" + destDir + "' is not a directory.");
		}
		File dest = new File(destDir, src.getName());
		copyFile(src, dest, params);
		return dest;
	}


	// ---------------------------------------------------------------- copy dir


	public static void copyDir(String srcDir, String destDir) throws IOException {
		copyDir(new File(srcDir), new File(destDir), defaultParams);
	}

	public static void copyDir(String srcDir, String destDir, FileUtilParams params) throws IOException {
		copyDir(new File(srcDir), new File(destDir), params);
	}

	public static void copyDir(File srcDir, File destDir) throws IOException {
		copyDir(srcDir, destDir, defaultParams);
	}

	/**
	 * Copies directory with specified copy params.
	 */
	public static void copyDir(File srcDir, File destDir, FileUtilParams params) throws IOException {
		checkDirCopy(srcDir, destDir);
		doCopyDirectory(srcDir, destDir, params);
	}

	private static void checkDirCopy(File srcDir, File destDir) throws IOException {
		if (srcDir.exists() == false) {
			throw new FileNotFoundException("Source '" + srcDir + "' does not exist.");
		}
		if (srcDir.isDirectory() == false) {
			throw new IOException("Source '" + srcDir + "' is not a directory.");
		}
		if (equals(srcDir, destDir) == true) {
			throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same.");
		}
	}

	private static void doCopyDirectory(File srcDir, File destDir, FileUtilParams params) throws IOException {
		if (destDir.exists()) {
			if (destDir.isDirectory() == false) {
				throw new IOException("Destination '" + destDir + "' is not a directory.");
			}
		} else {
			if (params.createDirs == false) {
				throw new IOException("Destination '" + destDir + "' doesn't exists.");
			}
			if (destDir.mkdirs() == false) {
				throw new IOException("Destination '" + destDir + "' directory cannot be created.");
			}
			if (params.preserveDate) {
				destDir.setLastModified(srcDir.lastModified());
			}
		}

		File[] files = srcDir.listFiles();
		if (files == null) {
			throw new IOException("Failed to list contents of '" + srcDir + '\'');
		}

		IOException exception = null;
		for (File file : files) {
			File destFile = new File(destDir, file.getName());
			try {
				if (file.isDirectory()) {
					if (params.recursive == true) {
						doCopyDirectory(file, destFile, params);
					}
				} else {
					doCopyFile(file, destFile, params);
				}
			} catch (IOException ioex) {
				if (params.continueOnError == true) {
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

	public static void moveFile(String src, String dest) throws IOException {
		moveFile(new File(src), new File(dest), defaultParams);
	}

	public static void moveFile(String src, String dest, FileUtilParams params) throws IOException {
		moveFile(new File(src), new File(dest), params);
	}

	public static void moveFile(File src, File dest) throws IOException {
		moveFile(src, dest, defaultParams);
	}

	public static void moveFile(File src, File dest, FileUtilParams params) throws IOException {
		checkFileCopy(src, dest, params);
		doMoveFile(src, dest, params);
	}

	private static void doMoveFile(File src, File dest, FileUtilParams params) throws IOException {
		if (dest.exists()) {
			if (dest.isFile() == false) {
				throw new IOException("Destination '" + dest + "' is not a file.");
			}
			if (params.overwrite == false) {
				throw new IOException("Destination '" + dest + "' already exists.");
			}
			dest.delete();
		}

		if (src.renameTo(dest) == false) {
			throw new IOException("Moving of '" + src + "' to '" + dest + "' failed.");
		}
	}

	// ---------------------------------------------------------------- move file to dir


	public static void moveFileToDir(String src, String destDir) throws IOException {
		moveFileToDir(new File(src), new File(destDir), defaultParams);
	}
	public static void moveFileToDir(String src, String destDir, FileUtilParams params) throws IOException {
		moveFileToDir(new File(src), new File(destDir), params);
	}

	public static void moveFileToDir(File src, File destDir) throws IOException {
		moveFileToDir(src, destDir, defaultParams);
	}
	public static void moveFileToDir(File src, File destDir, FileUtilParams params) throws IOException {
		if (destDir.exists() && destDir.isDirectory() == false) {
			throw new IOException("Destination '" + destDir + "' is not a directory.");
		}
		moveFile(src, new File(destDir, src.getName()), params);
	}




	// ---------------------------------------------------------------- move dir

	public static void moveDir(String srcDir, String destDir) throws IOException {
		moveDir(new File(srcDir), new File(destDir));
	}

	public static void moveDir(File srcDir, File destDir) throws IOException {
		checkDirCopy(srcDir, destDir);
		doMoveDirectory(srcDir, destDir);
	}

	private static void doMoveDirectory(File src, File dest) throws IOException {
		if (dest.exists()) {
			if (dest.isDirectory() == false) {
				throw new IOException("Destination '" + dest + "' is not a directory.");
			}
			dest = new File(dest, dest.getName());
			dest.mkdir();
		}

		if (src.renameTo(dest) == false) {
			throw new IOException("Moving of '" + src + "' to '" + dest + "' failed.");
		}
	}



	// ---------------------------------------------------------------- delete file

	public static void deleteFile(String dest) throws IOException {
		deleteFile(new File(dest));
	}

	public static void deleteFile(File dest) throws IOException {
		if (dest.exists() == false) {
			throw new FileNotFoundException("Destination '" + dest + "' doesn't exist");
		}
		if (dest.isFile() == false) {
			throw new IOException("Destination '" + dest + "' is not a file.");
		}
		if (dest.delete() == false) {
			throw new IOException("Unable to delete '" + dest + "'.");
		}
	}


	// ---------------------------------------------------------------- delete dir

	public static void deleteDir(String dest) throws IOException {
		deleteDir(new File(dest), defaultParams);
	}
	public static void deleteDir(String dest, FileUtilParams params) throws IOException {
		deleteDir(new File(dest), params);
	}
	public static void deleteDir(File dest) throws IOException {
		deleteDir(dest, defaultParams);
	}
	/**
	 * Deletes a directory.
	 */
	public static void deleteDir(File dest, FileUtilParams params) throws IOException {
		cleanDir(dest, params);
		if (dest.delete() == false) {
			throw new IOException("Unable to delete '" + dest + "'.");
		}
	}



	public static void cleanDir(String dest) throws IOException {
		cleanDir(new File(dest), defaultParams);
	}

	public static void cleanDir(String dest, FileUtilParams params) throws IOException {
		cleanDir(new File(dest), params);
	}

	public static void cleanDir(File dest) throws IOException {
		cleanDir(dest, defaultParams);
	}

	/**
	 * Cleans a directory without deleting it.
	 */
	public static void cleanDir(File dest, FileUtilParams params) throws IOException {
		if (dest.exists() == false) {
			throw new FileNotFoundException("Destination '" + dest + "' doesn't exists.");
		}

		if (dest.isDirectory() == false) {
			throw new IOException("Destination '" + dest + "' is not a directory.");
		}

		File[] files = dest.listFiles();
		if (files == null) {
			throw new IOException("Failed to list contents of '" + dest + "'.");
		}

		IOException exception = null;
		for (File file : files) {
			try {
				if (file.isDirectory()) {
					if (params.recursive == true) {
						deleteDir(file, params);
					}
				} else {
					file.delete();
				}
			} catch (IOException ioex) {
				if (params.continueOnError == true) {
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

	// ---------------------------------------------------------------- read/write string


	public static String readString(String source) throws IOException {
		return readString(new File(source), defaultParams.encoding);
	}

	public static String readString(String source, String encoding) throws IOException {
		return readString(new File(source), encoding);
	}

	public static String readString(File source) throws IOException {
		return readString(source, defaultParams.encoding);
	}

	/**
	 * Reads file content as string.
	 */
	public static String readString(File source, String encoding) throws IOException {
		if (source.exists() == false) {
			throw new FileNotFoundException("Source '" + source + "' doesn't exist.");
		}
		if (source.isFile() == false) {
			throw new IOException("Source '" + source + "' is not a file.");
		}
		long len = source.length();
		if (len >= Integer.MAX_VALUE) {
			len = Integer.MAX_VALUE;
		}
		FileInputStream in = null;
		try {
			in = new FileInputStream(source);
			StringWriter sw = new StringWriter((int) len);
			StreamUtil.copy(in, sw, encoding);
			return sw.toString();
		} finally {
			StreamUtil.close(in);
		}
	}


	public static void writeString(String dest, String data) throws IOException {
		outString(new File(dest), data, defaultParams.encoding, false);
	}

	public static void writeString(String dest, String data, String encoding) throws IOException {
		outString(new File(dest), data, encoding, false);
	}

	public static void writeString(File dest, String data) throws IOException {
		outString(dest, data, defaultParams.encoding, false);
	}

	public static void writeString(File dest, String data, String encoding) throws IOException {
		outString(dest, data, encoding, false);
	}


	public static void appendString(String dest, String data) throws IOException {
		outString(new File(dest), data, defaultParams.encoding, true);
	}

	public static void appendString(String dest, String data, String encoding) throws IOException {
		outString(new File(dest), data, encoding, true);
	}

	public static void appendString(File dest, String data) throws IOException {
		outString(dest, data, defaultParams.encoding, true);
	}

	public static void appendString(File dest, String data, String encoding) throws IOException {
		outString(dest, data, encoding, true);
	}

	protected static void outString(File dest, String data, String encoding, boolean append) throws IOException {
		if (dest.exists() == true) {
			if (dest.isFile() == false) {
				throw new IOException("Destination '" + dest + "' exist, but it is not a file.");
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
		return readLines(new File(source), defaultParams.encoding);
	}
	public static String[] readLines(String source, String encoding) throws IOException {
		return readLines(new File(source), encoding);
	}
	public static String[] readLines(File source) throws IOException {
		return readLines(source, defaultParams.encoding);
	}

	/**
	 * Reads lines from source files.
	 */
	public static String[] readLines(File source, String encoding) throws IOException {
		if (source.exists() == false) {
			throw new FileNotFoundException("Source '" + source + "' doesn't exist.");
		}
		if (source.isFile() == false) {
			throw new IOException("Source '" + source + "' is not a file.");
		}
		List<String> list = new ArrayList<String>();
		FileInputStream in = null;
		try {
			in = new FileInputStream(source);
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
		return readBytes(new File(file));
	}

	public static byte[] readBytes(File source) throws IOException {
		if (source.exists() == false) {
			throw new FileNotFoundException("Source '" + source + "' doesn't exist.");
		}
		if (source.isFile() == false) {
			throw new IOException("Source '" + source + "' exists, but it is not a file.");
		}
		long len = source.length();
		if (len >= Integer.MAX_VALUE) {
			throw new IOException("Source size is greater then max array size.");
		}
		FileInputStream in = null;
		try {
			in = new FileInputStream(source);
			return StreamUtil.readBytes(in);
		} finally {
			StreamUtil.close(in);
		}
	}



	public static void writeBytes(String dest, byte[] data) throws IOException {
		outBytes(new File(dest), data, 0, data.length, false);
	}

	public static void writeBytes(String dest, byte[] data, int off, int len) throws IOException {
		outBytes(new File(dest), data, off, len, false);
	}

	public static void writeBytes(File dest, byte[] data) throws IOException {
		outBytes(dest, data, 0, data.length, false);
	}

	public static void writeBytes(File dest, byte[] data, int off, int len) throws IOException {
		outBytes(dest, data, off, len, false);
	}


	public static void appendBytes(String dest, byte[] data) throws IOException {
		outBytes(new File(dest), data, 0, data.length, true);
	}

	public static void appendBytes(String dest, byte[] data, int off, int len) throws IOException {
		outBytes(new File(dest), data, off, len, true);
	}

	public static void appendBytes(File dest, byte[] data) throws IOException {
		outBytes(dest, data, 0, data.length, true);
	}

	public static void appendBytes(File dest, byte[] data, int off, int len) throws IOException {
		outBytes(dest, data, off, len, true);
	}

	protected static void outBytes(File dest, byte[] data, int off, int len, boolean append) throws IOException {
		if (dest.exists() == true) {
			if (dest.isFile() == false) {
				throw new IOException("Destination '" + dest + "' exist but it is not a file.");
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
		return compare(new File(file1), new File(file2));
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

		if (file1Exists == false) {
			return true;
		}

		if ((file1.isFile() == false) || (file2.isFile() == false)) {
			throw new IOException("Only files can be compared.");
		}

		if (file1.length() != file2.length()) {
			return false;
		}

		if (equals(file1, file1)) {
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
		return isNewer(new File(file), new File(reference));
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
		if (reference.exists() == false) {
			throw new IllegalArgumentException("The reference file '" + file + "' doesn't exist");
		}
		return isNewer(file, reference.lastModified());
	}


	public static boolean isOlder(String file, String reference) {
		return isOlder(new File(file), new File(reference));
	}

	public static boolean isOlder(File file, File reference) {
		if (reference.exists() == false) {
			throw new IllegalArgumentException("The reference file '" + file + "' doesn't exist");
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
		return isNewer(new File(file), timeMillis);
	}


	public static boolean isOlder(File file, long timeMillis) {
		if (!file.exists()) {
			return false;
		}
		return file.lastModified() < timeMillis;
	}

	public static boolean isOlder(String file, long timeMillis) {
		return isOlder(new File(file), timeMillis);
	}


	// ---------------------------------------------------------------- smart copy

	public static void copy(String src, String dest) throws IOException {
		copy(new File(src), new File(dest), defaultParams);
	}

	public static void copy(String src, String dest, FileUtilParams params) throws IOException {
		copy(new File(src), new File(dest), params);
	}

	public static void copy(File src, File dest) throws IOException {
		copy(src, dest, defaultParams);
	}
	/**
	 * Smart copy. If source is a directory, copy it to destination.
	 * Otherwise, if destination is directory, copy source file to it.
	 * Otherwise, try to copy source file to destination file.
	 */
	public static void copy(File src, File dest, FileUtilParams params) throws IOException {
		if (src.isDirectory() == true) {
			copyDir(src, dest, params);
			return;
		}
		if (dest.isDirectory() == true) {
			copyFileToDir(src, dest, params);
			return;
		}
		copyFile(src, dest, params);
	}

	// ---------------------------------------------------------------- smart move

	public static void move(String src, String dest) throws IOException {
		move(new File(src), new File(dest), defaultParams);
	}

	public static void move(String src, String dest, FileUtilParams params) throws IOException {
		move(new File(src), new File(dest), params);
	}

	public static void move(File src, File dest) throws IOException {
		move(src, dest, defaultParams);
	}
	/**
	 * Smart move. If source is a directory, move it to destination.
	 * Otherwise, if destination is directory, move source file to it.
	 * Otherwise, try to move source file to destination file.
	 */
	public static void move(File src, File dest, FileUtilParams params) throws IOException {
		if (src.isDirectory() == true) {
			moveDir(src, dest);
			return;
		}
		if (dest.isDirectory() == true) {
			moveFileToDir(src, dest, params);
			return;
		}
		moveFile(src, dest, params);
	}


	// ---------------------------------------------------------------- smart delete

	public static void delete(String dest) throws IOException {
		delete(new File(dest), defaultParams);
	}

	public static void delete(String dest, FileUtilParams params) throws IOException {
		delete(new File(dest), params);
	}

	public static void delete(File dest) throws IOException {
		delete(dest, defaultParams);
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

	public static File createTempDirectory(String prefix, String suffix) throws IOException {
		return createTempDirectory(prefix, suffix, (File) null);
	}
	public static File createTempDirectory(String prefix, String suffix, String tempDirName) throws IOException {
		return createTempDirectory(prefix, suffix, new File(tempDirName));
	}
	/**
	 * Creates temporary directory.
	 */
	public static File createTempDirectory(String prefix, String suffix, File tempDir) throws IOException {
		File file = doCreateTempFile(prefix, suffix, tempDir);
		file.delete();
		file.mkdir();
		return file;
	}

	public static File createTempFile() throws IOException {
		return createTempFile(true);
	}
	public static File createTempFile(boolean create) throws IOException {
		return createTempFile(JoddDefault.tempFilePrefix, ".tmp", (File) null, create);
	}
	public static File createTempFile(String prefix, String suffix) throws IOException {
		return createTempFile(prefix, suffix, (File) null, true);
	}
	public static File createTempFile(String prefix, String suffix, boolean create) throws IOException {
		return createTempFile(prefix, suffix, (File) null, create);
	}
	public static File createTempFile(String prefix, String suffix, String tempDirName) throws IOException {
		return createTempFile(prefix, suffix, new File(tempDirName), true);
	}
	public static File createTempFile(String prefix, String suffix, File tempDir) throws IOException {
		return createTempFile(prefix, suffix, tempDir, true);
	}
	public static File createTempFile(String prefix, String suffix, String tempDirName, boolean create) throws IOException {
		return createTempFile(prefix, suffix, new File(tempDirName), create);
	}
	/**
	 * Creates temporary file.
	 * If <code>create</code> is set to <code>true</code> file will be
	 * physically created on the file system. Otherwise, it will be created and then
	 * deleted - trick that will make temp file exist only if they are used.
	 */
	public static File createTempFile(String prefix, String suffix, File tempDir, boolean create) throws IOException {
		File file = doCreateTempFile(prefix, suffix, tempDir);
		file.delete();
		if (create) {
			file.createNewFile();
		}
		return file;
	}

	private static File doCreateTempFile(String prefix, String suffix, File dir) throws IOException {
		int exceptionsCount = 0;
		while (true) {
			try {
				return File.createTempFile(prefix, suffix, dir).getCanonicalFile();
			} catch (IOException ioex) {	// fixes java.io.WinNTFileSystem.createFileExclusively access denied
				if (++exceptionsCount >= 100) {
					throw ioex;
				}
			}
		}
	}

}
