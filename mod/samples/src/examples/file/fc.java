// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.file;

import jodd.io.findfile.ClassScanner;
import jodd.io.StreamUtil;

import java.io.InputStream;

public class fc {

	public static void main(String args[]) throws Exception {
		System.out.println("start");

		ClassScanner cs = new ClassScanner() {
			@Override
			protected void onEntry(EntryData entryData) throws Exception {
				InputStream inputStream = entryData.openInputStream();
				byte[] bytes = StreamUtil.readAvailableBytes(inputStream);
				System.out.println("---> " + entryData.getName() + ':' + entryData.getArchiveName() + "\t\t" + bytes.length);
			}
		};
		cs.setIncludeResources(true);
		cs.setIgnoreException(true);
		cs.scan("foo.jar", "d:\\Projects\\java\\apps\\jarminator\\out");
		System.out.println("end");
	}

}
