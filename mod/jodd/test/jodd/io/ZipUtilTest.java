package jodd.io;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.zip.ZipOutputStream;

public class ZipUtilTest extends TestCase {

	protected String dataRoot;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (dataRoot != null) {
			return;
		}
		URL data = FileUtilTest.class.getResource("data");
		dataRoot = data.getFile();
	}

	public void testGzip() throws IOException {
		ZipUtil.gzip(new File(dataRoot, "sb.data"));
		File gzipFile = new File(dataRoot, "sb.data.gz");
		assertTrue(gzipFile.exists());

		FileUtil.move(gzipFile, new File(dataRoot, "sb2.data.gz"));
		ZipUtil.ungzip(new File(dataRoot, "sb2.data.gz"));
		File data = new File(dataRoot, "sb2.data");
		assertTrue(data.exists());
		
		byte[] data2Bytes = FileUtil.readBytes(data);
		byte[] data1Bytes = FileUtil.readBytes(new File(dataRoot, "sb.data"));

		assertTrue(Arrays.equals(data1Bytes, data2Bytes));

		// cleanup
		FileUtil.delete(new File(dataRoot, "sb2.data"));
		FileUtil.delete(new File(dataRoot, "sb2.data.gz"));
	}

	public void testZlib() throws IOException {
		ZipUtil.zlib(new File(dataRoot, "sb.data"));
		File zlibFile = new File(dataRoot, "sb.data.zlib");
		assertTrue(zlibFile.exists());

		// cleanup
		FileUtil.delete(zlibFile);
	}
	
	public void testZip() throws IOException {
		ZipUtil.zip(new File(dataRoot, "sb.data"));
		File zipFile = new File(dataRoot, "sb.data.zip");
		assertTrue(zipFile.exists());
		FileUtil.delete(zipFile);

		ZipUtil.zip(new File(dataRoot, "file"));
		zipFile = new File(dataRoot, "file.zip");
		assertTrue(zipFile.exists());

		// cleanup
		FileUtil.delete(zipFile);
	}
	
	public void testZipStreams() throws IOException {
		File zipFile = new File(dataRoot, "test.zip");
		
		ZipOutputStream zos = ZipUtil.createZip(zipFile);
		
		ZipUtil.addToZip(zos, new File(dataRoot, "sb.data"), "sbdata", "This is sb data file");

		ZipUtil.addToZip(zos, new File(dataRoot, "file"), "folder", "This is a folder and all its files");

		StreamUtil.close(zos);
		
		assertTrue(zipFile.exists());

		ZipUtil.unzip(zipFile, new File(dataRoot));

		assertTrue(new File(dataRoot, "sbdata").exists());
		assertTrue(new File(dataRoot, "folder").exists());
		assertTrue(new File(new File(dataRoot, "folder"), "a.png").exists());

		// cleanup
		FileUtil.delete(new File(dataRoot, "sbdata"));
		FileUtil.deleteDir(new File(dataRoot, "folder"));
		FileUtil.delete(zipFile);
	}

}
