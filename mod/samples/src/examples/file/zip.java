// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.file;

import jodd.io.ZipUtil;
import jodd.io.FileUtil;
import jodd.io.StreamUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.util.zip.ZipOutputStream;

public class zip {

	public static void main(String[] args) throws IOException {
		ZipOutputStream zos = ZipUtil.createZip("d:\\test.zip");
		ZipUtil.addFileToZip(zos, "d:\\g.jpg", "xxx1.jpg", "first");
		ZipUtil.addFileToZip(zos, "d:\\a.jpg", "xxx2.jpg");
		ZipUtil.addDirToZip(zos, "d:\\npp");
		StreamUtil.close(zos);

	}

	static void one() throws IOException {
		File file = new File("d:\\Picture 001.jpg");
		OutputStream out = ZipUtil.createSingleEntryOutputStream(file);
		out.write(FileUtil.readBytes(file));
		out.close();
		ZipUtil.unzip("d:\\Picture 001.jpg.zip", "d:\\temp\\temp");
	}
}
