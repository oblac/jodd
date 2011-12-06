// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Performs zip/unzip operations on files and directories.
 */
public class ZipUtil {

	private static final String ZIP_EXT = ".zip";

	public static InputStream createFirstEntryInputStream(String zipFileName) throws IOException {
		return createFirstEntryInputStream(new File(zipFileName));
	}

	/**
	 * Creates an InputStream of first entry on a given zip file.
	 */
	public static InputStream createFirstEntryInputStream(File zipFile) throws IOException {
		ZipFile zf = new ZipFile(zipFile);
		Enumeration entries = zf.entries();
		if (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			return zf.getInputStream(entry);
		}
		return null;
	}

	public static ZipOutputStream createSingleEntryOutputStream(String zipEntryFileName) throws IOException {
		return createSingleEntryOutputStream(new File(zipEntryFileName));
	}

	public static ZipOutputStream createSingleEntryOutputStream(File zipEntryFile) throws IOException {
		String entryName = zipEntryFile.getName();
		if (entryName.endsWith(ZIP_EXT)) {
			entryName = entryName.substring(0, entryName.length() - ZIP_EXT.length());
		}
		return createSingleEntryOutputStream(entryName, zipEntryFile);
	}

	public static ZipOutputStream createSingleEntryOutputStream(String entryName, String zipEntryFileName) throws IOException {
		return createSingleEntryOutputStream(entryName, new File(zipEntryFileName));
	}

	/**
	 * Creates an <code>ZipOutputStream</zip> to zip file with single entry.
	 */
	public static ZipOutputStream createSingleEntryOutputStream(String entryName, File zipEntryFile) throws IOException {
		String zipFileName = zipEntryFile.getAbsolutePath();
		if (zipFileName.endsWith(ZIP_EXT) == false) {
			zipFileName += ZIP_EXT;
		}
		FileOutputStream fos = new FileOutputStream(new File(zipFileName));
		ZipOutputStream zos = new ZipOutputStream(fos);
		ZipEntry ze = new ZipEntry(entryName);
		try {
			zos.putNextEntry(ze);
		} catch (IOException ioex) {
			StreamUtil.close(fos);
			throw ioex;
		}
		return zos;
	}

	/**
	 * Creates and opens zip output stream of a zip file. If zip file exist it will be recreated.
	 */
	public static ZipOutputStream createZip(File zip) throws FileNotFoundException {
		return new ZipOutputStream(new FileOutputStream(zip));
	}

	/**
	 * @see #createZip(java.io.File)
	 */
	public static ZipOutputStream createZip(String zipFile) throws FileNotFoundException {
		return createZip(new File(zipFile));
	}


	// ---------------------------------------------------------------- unzip

	/**
	 * Extracts zip file content to the target directory.
	 */
	public static void unzip(String zipFile, String destDir) throws IOException {
		unzip(new File(zipFile), new File(destDir));
	}

	/**
	 * Extracts zip file content to the target directory.
	 *
	 * @param zipFile zip file
	 * @param destDir destination directory
	 */
	public static void unzip(File zipFile, File destDir) throws IOException {
		ZipFile zip = new ZipFile(zipFile);
		Enumeration en = zip.entries();

		while (en.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) en.nextElement();
			File file = (destDir != null) ? new File(destDir, entry.getName()) : new File(entry.getName());
			if (entry.isDirectory()) {
				if (!file.mkdirs()) {
					if (file.isDirectory() == false) {
						throw new IOException("Error creating directory: " + file);
					}
				}
			} else {
				File parent = file.getParentFile();
				if (parent != null && !parent.exists()) {
					if (!parent.mkdirs()) {
						if (file.isDirectory() == false) {
							throw new IOException("Error creating directory: " + parent);
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
	}

	// ---------------------------------------------------------------- zip

	/*
	 * Adds a new file entry to the ZIP output stream.
	 */
	public static void addFileToZip(ZipOutputStream zos, File file, String relativeName) throws IOException {
		addFileToZip(zos, file, relativeName, null);
	}
	public static void addFileToZip(ZipOutputStream zos, String fileName, String relativeName) throws IOException {
		addFileToZip(zos, new File(fileName), relativeName, null);
	}
	public static void addFileToZip(ZipOutputStream zos, String fileName, String relativeName, String comment) throws IOException {
		addFileToZip(zos, new File(fileName), relativeName, comment);
	}
	public static void addFileToZip(ZipOutputStream zos, File file, String relativeName, String comment) throws IOException {
		while (relativeName.length() != 0 && relativeName.charAt(0) == '/') {
			relativeName = relativeName.substring(1);
		}

		boolean isDir = file.isDirectory();
		if (isDir && !StringUtil.endsWithChar(relativeName, '/')) {
			relativeName += "/";
		}

		long size = isDir ? 0 : file.length();
		ZipEntry e = new ZipEntry(relativeName);
		e.setTime(file.lastModified());
		e.setComment(comment);
		if (size == 0) {
			e.setMethod(ZipEntry.STORED);
			e.setSize(0);
			e.setCrc(0);
		}
		zos.putNextEntry(e);
		if (!isDir) {
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			try {
				StreamUtil.copy(is, zos);
			} finally {
				StreamUtil.close(is);
			}
		}
		zos.closeEntry();
	}

	public static void addDirToZip(ZipOutputStream out, String dirName) throws IOException {
		String path = FileNameUtil.getName(dirName);
		addDirToZip(out, new File(dirName), path);
	}

	public static void addDirToZip(ZipOutputStream out, String dirName, String relativePath) throws IOException {
		addDirToZip(out, new File(dirName), relativePath);
	}

	/**
	 * Adds a folder to the zip recursively using its name as relative zip path.
	 */
	public static void addDirToZip(ZipOutputStream out, File dir) throws IOException {
		String path = FileNameUtil.getName(dir.getAbsolutePath());
		addDirToZip(out, dir, path);
	}

	/**
	 * Adds a folder to the zip recursively.
	 */
	public static void addDirToZip(ZipOutputStream out, File dir, String relativePath) throws IOException {
		boolean noRelativePath = StringUtil.isEmpty(relativePath);
		if (noRelativePath == false) {
			addFileToZip(out, dir, relativePath);
		}
		final File[] children = dir.listFiles();
		if (children != null) {
			for (File child : children) {
				final String childRelativePath = (noRelativePath ? StringPool.EMPTY : relativePath + '/') + child.getName();
				if (child.isDirectory()) {
					addDirToZip(out, child, childRelativePath);
				} else {
					addFileToZip(out, child, childRelativePath);
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
