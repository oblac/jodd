// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.watch;

import jodd.io.FileUtil;
import jodd.util.ThreadUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

		dirWatcher.start(250);

		File destFile = new File(dataRoot, "jodd.md");

		FileUtil.writeString(destFile, "#Jodd");
		ThreadUtil.sleep(500);

		FileUtil.touch(destFile);
		ThreadUtil.sleep(500);

		FileUtil.delete(destFile);
		ThreadUtil.sleep(500);

		dirWatcher.stop();

		assertEquals(
				DirWatcher.Event.CREATED + ":jodd.md\n" +
				DirWatcher.Event.MODIFIED + ":jodd.md\n" +
				DirWatcher.Event.DELETED + ":jodd.md\n",
				sb.toString());
	}

	@Test
	public void testDirWatcherWithFile() throws IOException {
		DirWatcher dirWatcher = new DirWatcher(dataRoot, "*.md").useWatchFile("watch.txt");

		final StringBuilder sb = new StringBuilder();

		dirWatcher.register(new DirWatcherListener() {
			public void onChange(File file, DirWatcher.Event event) {
				sb.append(event.name() + ":" + file.getName() + "\n");
			}
		});

		dirWatcher.start(250);

		File watchFile = new File(dataRoot, "watch.txt");
		File destFile = new File(dataRoot, "jodd.md");

		FileUtil.writeString(destFile, "#Jodd");
		FileUtil.touch(watchFile);
		ThreadUtil.sleep(500);

		FileUtil.touch(destFile);
		ThreadUtil.sleep(500);

		FileUtil.delete(destFile);
		FileUtil.touch(watchFile);
		ThreadUtil.sleep(500);

		dirWatcher.stop();

		assertEquals(
				DirWatcher.Event.CREATED + ":jodd.md\n" +
				//DirWatcher.Event.MODIFIED + ":jodd.md\n" +
				DirWatcher.Event.DELETED + ":jodd.md\n",
				sb.toString());
	}

}