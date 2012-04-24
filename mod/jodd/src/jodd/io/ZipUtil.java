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
 * These are just tools over existing java.util.zip classes,
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
	public static void zlib(String file) throws IOException {
		zlib(new File(file));
	}

	/**
	 * Compresses a file into zlib archive.
	 */
	public static void zlib(File file) throws IOException {
		if (file.isDirectory() == true) {
			throw new IOException("zlib does not work on a folder");
		}
		FileInputStream fis = new FileInputStream(file);
		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
		DeflaterOutputStream dos = new DeflaterOutputStream(new FileOutputStream(file.getAbsolutePath() + ZLIB_EXT), deflater);
		try {
			StreamUtil.copy(fis, dos);
		} finally {
			StreamUtil.close(dos);
			StreamUtil.close(fis);
		}
	}

	// ---------------------------------------------------------------- gzip
	
	/**
	 * Compresses a file into gzip archive.
	 */
	public static void gzip(String fileName) throws IOException {
		gzip(new File(fileName));
	}

	/**
	 * Compresses a file into gzip archive.
	 */
	public static void gzip(File file) throws IOException {
		if (file.isDirectory() == true) {
			throw new IOException("gzip does not work on a folder");
		}
		FileInputStream fis = new FileInputStream(file);
		GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(file.getAbsolutePath() + GZIP_EXT));
		try {
			StreamUtil.copy(fis, gzos);
		} finally {
			StreamUtil.close(gzos);
			StreamUtil.close(fis);
		}
	}

	/**
	 * Decompress gzip archive.
	 */
	public static void ungzip(String file) throws IOException {
		ungzip(new File(file));
	}

	/**
	 * Decompress gzip archive.
	 */
	public static void ungzip(File file) throws IOException {
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
	}

	// ---------------------------------------------------------------- zip

	/**
	 * Zips a file or a folder.
	 */
	public static void zip(String file) throws IOException {
		zip(new File(file));
	}

	/**
	 * Zips a file or a folder.
	 */
	public static void zip(File file) throws IOException {
		String zipFile = file.getAbsolutePath() + ZIP_EXT;

		ZipOutputStream zos = null;
		try {
			zos = createZip(zipFile);
			addToZip(zos, file);
		} finally {
			StreamUtil.close(zos);
		}
	}

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

		close(zip);
	}

	// ---------------------------------------------------------------- zip stream

	/**
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

	/*
	 * Adds a new file entry to the ZIP output stream.
	 */
	public static void addToZip(ZipOutputStream zos, File file) throws IOException {
		addToZip(zos, file, file.getName(), null);
	}
	public static void addToZip(ZipOutputStream zos, String file) throws IOException {
		addToZip(zos, new File(file));
	}

	public static void addToZip(ZipOutputStream zos, File file, String relativeName) throws IOException {
		addToZip(zos, file, relativeName, null);
	}
	public static void addToZip(ZipOutputStream zos, String fileName, String relativeName) throws IOException {
		addToZip(zos, new File(fileName), relativeName, null);
	}

	public static void addToZip(ZipOutputStream zos, String fileName, String relativeName, String comment) throws IOException {
		addToZip(zos, new File(fileName), relativeName, comment);
	}

	/*
	 * Adds new entry to the ZIP output stream. The source may be either a file or a folder. If it is a folder,
	 * it will be recursively scanned and all its content added to the zip.
	 */
	public static void addToZip(ZipOutputStream zos, File file, String relativeName, String comment) throws IOException {
		while (relativeName.length() != 0 && relativeName.charAt(0) == '/') {
			relativeName = relativeName.substring(1);
		}
		if (file.exists() == false) {
			throw new FileNotFoundException(file.toString());
		}
		boolean isDir = file.isDirectory();
		boolean addEntry = file.isFile();

		if (isDir) {
			boolean noRelativePath = StringUtil.isEmpty(relativeName);
			final File[] children = file.listFiles();
			if (children.length != 0) {
				for (File child : children) {
					String childRelativePath = (noRelativePath ? StringPool.EMPTY : relativeName + '/') + child.getName();
					addToZip(zos, child, childRelativePath);
				}
			} else {
				// add empty folder
				if (!StringUtil.endsWithChar(relativeName, '/')) {
					relativeName += '/';
				}
				addEntry = true;
			}
		}

		if (addEntry) {
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
				InputStream is = new FileInputStream(file);
				try {
					StreamUtil.copy(is, zos);
				} finally {
					StreamUtil.close(is);
				}
			}
			zos.closeEntry();
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
