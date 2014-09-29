// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.watch;

import jodd.io.FileUtil;
import jodd.util.ThreadUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

// test is ignored as it does not give consistent results, but still can be used locally
@Ignore
public class DirWatcherTest {

	protected String dataRoot;

	@Before
	public void setUp() throws Exception {
		if (dataRoot != null) {
			return;
		}
		URL data = DirWatcherTest.class.getResource(".");
		dataRoot = data.getFile();
	}

	@Test
	public void testDirWatcher() throws IOException {
		DirWatcher dirWatcher = new DirWatcher(dataRoot, "*.md");

		final StringBuilder sb = new StringBuilder();

		dirWatcher.register(new DirWatcherListener() {
			public void onChange(File file, DirWatcher.Event event) {
				sb.append(event.name() + ":" + file.getName() + "\n");
			}
		});

		dirWatcher.start(100);

		File destFile = new File(dataRoot, "jodd.md");

		FileUtil.writeString(destFile, "#Jodd");
		ThreadUtil.sleep(600);

		FileUtil.writeString(destFile, "#Jodd2");
		ThreadUtil.sleep(600);

		FileUtil.delete(destFile);
		ThreadUtil.sleep(600);

		dirWatcher.stop();

		assertEquals(
				DirWatcher.Event.CREATED + ":jodd.md\n" +
				DirWatcher.Event.MODIFIED + ":jodd.md\n" +
				DirWatcher.Event.DELETED + ":jodd.md\n",
				sb.toString());
	}

	@Test
	public void testDirWatcherWithFile() throws IOException {
		DirWatcher dirWatcher = new DirWatcher(dataRoot)
				.monitor("*.md")
				.useWatchFile("watch.txt");

		final StringBuilder sb = new StringBuilder();

		dirWatcher.register(new DirWatcherListener() {
			public void onChange(File file, DirWatcher.Event event) {
				sb.append(event.name() + ":" + file.getName() + "\n");
			}
		});

		dirWatcher.start(100);

		File watchFile = new File(dataRoot, "watch.txt");
		File destFile = new File(dataRoot, "jodd.md");

		FileUtil.writeString(destFile, "#Jodd");
		FileUtil.touch(watchFile);
		ThreadUtil.sleep(600);

		FileUtil.writeString(destFile, "#Jodd2");
		ThreadUtil.sleep(600);

		FileUtil.delete(destFile);
		FileUtil.touch(watchFile);
		ThreadUtil.sleep(600);

		dirWatcher.stop();

		assertEquals(
				DirWatcher.Event.CREATED + ":jodd.md\n" +
				//DirWatcher.Event.MODIFIED + ":jodd.md\n" +
				DirWatcher.Event.DELETED + ":jodd.md\n",
				sb.toString());
	}

	@Test
	public void testBlankStart() throws IOException {
		DirWatcher dirWatcher = new DirWatcher(dataRoot)
				.monitor("*.txt")
				.startBlank(true);

		final StringBuilder sb = new StringBuilder();

		dirWatcher.register(new DirWatcherListener() {
			public void onChange(File file, DirWatcher.Event event) {
				sb.append(event.name() + ":" + file.getName() + "\n");
			}
		});

		dirWatcher.start(100);

		ThreadUtil.sleep(600);

		dirWatcher.stop();

		assertEquals(
				DirWatcher.Event.CREATED + ":watch.txt\n",
				sb.toString());

	}

}