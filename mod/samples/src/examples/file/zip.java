// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.file;

import jodd.io.ZipUtil;
import jodd.io.FileUtil;
import jodd.io.StreamUtil;

import java.io.IOException;
import java.io.File;
import java.util.zip.ZipOutputStream;

public class zip {

	public static void main(String[] args) throws IOException {
		ZipOutputStream zos = ZipUtil.createZip("d:\\test.zip");
		ZipUtil.addToZip(zos, "d:\\b.jpg", "xxx1.jpg", "first");
		ZipUtil.addToZip(zos, "d:\\a.jpg", "xxx2.jpg");
		ZipUtil.addToZip(zos, "d:\\a.jpg");
		ZipUtil.addToZip(zos, "d:\\x");
		StreamUtil.close(zos);
//		one();
	}

	static void one() throws IOException {
		File file = new File("d:\\a.jpg");
		ZipUtil.zip(file);
		ZipUtil.zip("d:\\x");
//		ZipUtil.gzip("d:\\xxxxxxxxxxxxx");
//		ZipUtil.gzip("d:\\x");
		ZipUtil.gzip(file);
		ZipUtil.zlib(file);
//		ZipUtil.zlib("d:\\x");
		FileUtil.copy(new File("d:\\a.jpg.gz"), new File("d:\\aa.jpg.gz"));
		ZipUtil.ungzip("d:\\aa.jpg.gz");
	}

}