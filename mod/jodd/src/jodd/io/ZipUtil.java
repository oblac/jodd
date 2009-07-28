// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

	public static OutputStream createSingleEntryOutputStream(String zipEntryFileName) throws IOException {
		return createSingleEntryOutputStream(new File(zipEntryFileName));
	}

	public static OutputStream createSingleEntryOutputStream(File zipEntryFile) throws IOException {
		String entryName = zipEntryFile.getName();
		if (entryName.endsWith(ZIP_EXT)) {
			entryName = entryName.substring(0, entryName.length() - ZIP_EXT.length());
		}
		return createSingleEntryOutputStream(entryName, zipEntryFile);
	}

	public static OutputStream createSingleEntryOutputStream(String entryName, String zipEntryFileName) throws IOException {
		return createSingleEntryOutputStream(entryName, new File(zipEntryFileName));
	}

	/**
	 * Creates an OutputStream to zip file with single entry.
	 */
	public static OutputStream createSingleEntryOutputStream(String entryName, File zipEntryFile) throws IOException {
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
