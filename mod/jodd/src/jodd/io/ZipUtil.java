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

	/**
	 * Creates an OutputStream to zip file with single entry.
	 */
	public static OutputStream createSingleEntryOutputStream(File zipFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream zos = new ZipOutputStream(fos);
		ZipEntry ze = new ZipEntry(zipFile.getName());
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
	 * Unpacks a zip file to the target directory.
	 *
	 * @param zipFile zip file
	 * @param destDir destination directory
	 *
	 * @throws IOException
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
