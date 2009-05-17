// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import jodd.util.StringPool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

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
		} catch (IOException ioex) {
			return false;
		}
		return file1.equals(file2);
	}

	/**
	 * Converts file URLs to file. Ignores other schemes and returns <code>null</code>.
	 */
	public static File toFile(URL url) {
		return new File(toFileName(url));
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



	// ---------------------------------------------------------------- settings

	/**
	 * Inner settings describe behaviour of the FileUtil class.
	 */
	public static class Settings implements Cloneable {
		private Settings() {}

		// should destination file have the same timestamp as source
		protected boolean preserveDate = true;
		// overwrite existing destination
		protected boolean overwrite = true;
		// create missing subdirectories of destination
		protected boolean createDirs = true;
		// use recursive directory copying and deleting
		protected boolean recursive = true;
		// don't stop on error and continue job as much as possible
		protected boolean continueOnError = true;
		// default encoding for reading/writing strings
		protected String encoding = StringPool.UTF_8;


		public boolean isPreserveDate() {
			return preserveDate;
		}
		public void setPreserveDate(boolean preserveDate) {
			this.preserveDate = preserveDate;
		}
		public Settings preserveDate(boolean preserveDate) {
			this.preserveDate = preserveDate;
			return this;
		}

		public boolean isOverwrite() {
			return overwrite;
		}
		public void setOverwrite(boolean overwrite) {
			this.overwrite = overwrite;
		}
		public Settings overwrite(boolean overwrite) {
			this.overwrite = overwrite;
			return this;
		}

		public boolean isCreateDirs() {
			return createDirs;
		}
		public void setCreateDirs(boolean createDirs) {
			this.createDirs = createDirs;
		}
		public Settings createDirs(boolean createDirs) {
			this.createDirs = createDirs;
			return this;
		}

		public boolean isRecursive() {
			return recursive;
		}
		public void setRecursive(boolean recursive) {
			this.recursive = recursive;
		}
		public Settings recursive(boolean recursive) {
			this.recursive = recursive;
			return this;
		}

		public boolean isContinueOnError() {
			return continueOnError;
		}
		public void setContinueOnError(boolean continueOnError) {
			this.continueOnError = continueOnError;
		}
		public Settings continueOnError(boolean continueOnError) {
			this.continueOnError = continueOnError;
			return this;
		}


		public String getEncoding() {
			return encoding;
		}
		public void setEncoding(String encoding) {
			this.encoding = encoding;
		}
		public Settings encoding(String encoding) {
			this.encoding = encoding;
			return this;
		}

		// ------------------------------------------------------------ clone

		@Override
		public Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			((Settings) clone).encoding = this.encoding;
			return clone;
		}
	}

	// default global settings
	public static Settings settings = new Settings();

	/**
	 * Creates new {@link Settings} instance by cloning current default settings.
	 */
	public static Settings cloneSettings() {
		try {
			return (Settings) settings.clone();
		} catch (CloneNotSupportedException cnsex) {
			return null;
		}
	}

	/**
	 * Creates new {@link Settings} instance with default values.
	 */
	public static Settings newSettings() {
		return new Settings();
	}

	// ---------------------------------------------------------------- copy file to file

	public static void copyFile(String src, String dest) throws IOException {
		copyFile(new File(src), new File(dest), settings);
	}

	public static void copyFile(String src, String dest, Settings settings) throws IOException {
		copyFile(new File(src), new File(dest), settings);
	}

	public static void copyFile(File src, File dest) throws IOException {
		copyFile(src, dest, settings);
	}

	/**
	 * Copies a file to another file with specified copy settings.
	 */
	public static void copyFile(File src, File dest, Settings settings) throws IOException {
		checkFileCopy(src, dest, settings);
		doCopyFile(src, dest, settings);
	}

	private static void checkFileCopy(File src, File dest, Settings settings) throws IOException {
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
			if (settings.createDirs == false) {
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
	private static void doCopyFile(File src, File dest, Settings settings) throws IOException {
		if (dest.exists()) {
			if (dest.isDirectory()) {
				throw new IOException("Destination '" + dest + "' is a directory.");
			}
			if (settings.overwrite == false) {
				throw new IOException("Destination '" + dest + "' already exists.");
			}
		}

		doCopy(src, dest);

		if (src.length() != dest.length()) {
			throw new IOException("Copying of '" + src + "' to '" + dest + "' failed due to different sizes.");
		}
		if (settings.preserveDate) {
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

	public static void copyFileToDir(String src, String destDir) throws IOException {
		copyFileToDir(new File(src), new File(destDir), settings);
	}
	public static void copyFileToDir(String src, String destDir, Settings settings) throws IOException {
		copyFileToDir(new File(src), new File(destDir), settings);
	}

	public static void copyFileToDir(File src, File destDir) throws IOException {
		copyFileToDir(src, destDir, settings);
	}
	/**
	 * Copies a file to folder with specified copy settings.
	 */
	public static void copyFileToDir(File src, File destDir, Settings settings) throws IOException {
		if (destDir.exists() && destDir.isDirectory() == false) {
			throw new IOException("Destination '" + destDir + "' is not a directory.");
		}
		copyFile(src, new File(destDir, src.getName()), settings);
	}


	// ---------------------------------------------------------------- copy dir


	public static void copyDir(String srcDir, String destDir) throws IOException {
		copyDir(new File(srcDir), new File(destDir), settings);
	}

	public static void copyDir(String srcDir, String destDir, Settings settings) throws IOException {
		copyDir(new File(srcDir), new File(destDir), settings);
	}

	public static void copyDir(File srcDir, File destDir) throws IOException {
		copyDir(srcDir, destDir, settings);
	}

	/**
	 * Copies directory with specified copy settings.
	 */
	public static void copyDir(File srcDir, File destDir, Settings settings) throws IOException {
		checkDirCopy(srcDir, destDir);
		doCopyDirectory(srcDir, destDir, settings);
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

	private static void doCopyDirectory(File srcDir, File destDir, Settings settings) throws IOException {
		if (destDir.exists()) {
			if (destDir.isDirectory() == false) {
				throw new IOException("Destination '" + destDir + "' is not a directory.");
			}
		} else {
			if (settings.createDirs == false) {
				throw new IOException("Destination '" + destDir + "' doesn't exists.");
			}
			if (destDir.mkdirs() == false) {
				throw new IOException("Destination '" + destDir + "' directory cannot be created.");
			}
			if (settings.preserveDate) {
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
					if (settings.recursive == true) {
						doCopyDirectory(file, destFile, settings);
					}
				} else {
					doCopyFile(file, destFile, settings);
				}
			} catch (IOException ioex) {
				if (settings.continueOnError == true) {
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
		moveFile(new File(src), new File(dest), settings);
	}

	public static void moveFile(String src, String dest, Settings settings) throws IOException {
		moveFile(new File(src), new File(dest), settings);
	}

	public static void moveFile(File src, File dest) throws IOException {
		moveFile(src, dest, settings);
	}

	public static void moveFile(File src, File dest, Settings settings) throws IOException {
		checkFileCopy(src, dest, settings);
		doMoveFile(src, dest, settings);
	}

	private static void doMoveFile(File src, File dest, Settings settings) throws IOException {
		if (dest.exists()) {
			if (dest.isFile() == false) {
				throw new IOException("Destination '" + dest + "' is not a file.");
			}
			if (settings.overwrite == false) {
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
		moveFileToDir(new File(src), new File(destDir), settings);
	}
	public static void moveFileToDir(String src, String destDir, Settings settings) throws IOException {
		moveFileToDir(new File(src), new File(destDir), settings);
	}

	public static void moveFileToDir(File src, File destDir) throws IOException {
		moveFileToDir(src, destDir, settings);
	}
	public static void moveFileToDir(File src, File destDir, Settings settings) throws IOException {
		if (destDir.exists() && destDir.isDirectory() == false) {
			throw new IOException("Destination '" + destDir + "' is not a directory.");
		}
		moveFile(src, new File(destDir, src.getName()), settings);
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
		deleteDir(new File(dest), settings);
	}
	public static void deleteDir(String dest, Settings settings) throws IOException {
		deleteDir(new File(dest), settings);
	}
	public static void deleteDir(File dest) throws IOException {
		deleteDir(dest, settings);
	}
	/**
	 * Deletes a directory.
	 */
	public static void deleteDir(File dest, Settings settings) throws IOException {
		cleanDir(dest, settings);
		if (dest.delete() == false) {
			throw new IOException("Unable to delete '" + dest + "'.");
		}
	}



	public static void cleanDir(String dest) throws IOException {
		cleanDir(new File(dest), settings);
	}

	public static void cleanDir(String dest, Settings settings) throws IOException {
		cleanDir(new File(dest), settings);
	}

	public static void cleanDir(File dest) throws IOException {
		cleanDir(dest, settings);
	}

	/**
	 * Cleans a directory without deleting it.
	 */
	public static void cleanDir(File dest, Settings settings) throws IOException {
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
					if (settings.recursive == true) {
						deleteDir(file, settings);
					}
				} else {
					file.delete();
				}
			} catch (IOException ioex) {
				if (settings.continueOnError == true) {
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
		return readString(new File(source), settings.encoding);
	}

	public static String readString(String source, String encoding) throws IOException {
		return readString(new File(source), encoding);
	}

	public static String readString(File source) throws IOException {
		return readString(source, settings.encoding);
	}

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
		writeString(new File(dest), data, settings.encoding);
	}

	public static void writeString(String dest, String data, String encoding) throws IOException {
		writeString(new File(dest), data, encoding);
	}

	public static void writeString(File dest, String data) throws IOException {
		writeString(dest, data, settings.encoding);
	}

	public static void writeString(File dest, String data, String encoding) throws IOException {
		if (dest.exists() == true) {
			if (dest.isFile() == false) {
				throw new IOException("Destination '" + dest + "' exist, but it is not a file.");
			}
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(dest);
			out.write(data.getBytes(encoding));
		} finally {
			StreamUtil.close(out);
		}
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
		writeBytes(new File(dest), data, 0, data.length);
	}

	public static void writeBytes(String dest, byte[] data, int off, int len) throws IOException {
		writeBytes(new File(dest), data, off, len);
	}

	public static void writeBytes(File dest, byte[] data) throws IOException {
		writeBytes(dest, data, 0, data.length);
	}

	public static void writeBytes(File dest, byte[] data, int off, int len) throws IOException {
		if (dest.exists() == true) {
			if (dest.isFile() == false) {
				throw new IOException("Destination '" + dest + "' exist but it is not a file.");
			}
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(dest);
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
		copy(new File(src), new File(dest), settings);
	}

	public static void copy(String src, String dest, Settings settings) throws IOException {
		copy(new File(src), new File(dest), settings);
	}

	public static void copy(File src, File dest) throws IOException {
		copy(src, dest, settings);
	}
	/**
	 * Smart copy. If source is a directory, copy it to destination.
	 * Otherwise, if destination is directory, copy source file to it.
	 * Otherwise, try to copy source file to destination file.
	 */
	public static void copy(File src, File dest, Settings settings) throws IOException {
		if (src.isDirectory() == true) {
			copyDir(src, dest, settings);
			return;
		}
		if (dest.isDirectory() == true) {
			copyFileToDir(src, dest, settings);
			return;
		}
		copyFile(src, dest, settings);
	}

	// ---------------------------------------------------------------- smart move

	public static void move(String src, String dest) throws IOException {
		move(new File(src), new File(dest), settings);
	}

	public static void move(String src, String dest, Settings settings) throws IOException {
		move(new File(src), new File(dest), settings);
	}

	public static void move(File src, File dest) throws IOException {
		move(src, dest, settings);
	}
	/**
	 * Smart move. If source is a directory, move it to destination.
	 * Otherwise, if destination is directory, move source file to it.
	 * Otherwise, try to move source file to destination file.
	 */
	public static void move(File src, File dest, Settings settings) throws IOException {
		if (src.isDirectory() == true) {
			moveDir(src, dest);
			return;
		}
		if (dest.isDirectory() == true) {
			moveFileToDir(src, dest, settings);
			return;
		}
		moveFile(src, dest, settings);
	}


	// ---------------------------------------------------------------- smart delete

	public static void delete(String dest) throws IOException {
		delete(new File(dest), settings);
	}

	public static void delete(String dest, Settings settings) throws IOException {
		delete(new File(dest), settings);
	}

	public static void delete(File dest) throws IOException {
		delete(dest, settings);
	}

	/**
	 * Smart delete of destination file or directory.
	 */
	public static void delete(File dest, Settings settings) throws IOException {
		if (dest.isDirectory()) {
			deleteDir(dest, settings);
			return;
		}
		deleteFile(dest);
	}
}


