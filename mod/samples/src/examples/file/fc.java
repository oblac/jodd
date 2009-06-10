// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.file;

import jodd.io.findfile.FindClass;
import jodd.io.findfile.ClasspathScanner;
import jodd.io.FileUtil;
import jodd.io.StreamUtil;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

public class fc {

	public static void main(String args[]) throws Exception {
		System.out.println("start");

		ClasspathScanner cs = new ClasspathScanner() {
			@Override
			protected void onEntryName(String entryName, InputStreamProvider inputStreamProvider) throws Exception {
				InputStream inputStream = inputStreamProvider.get();
				byte[] bytes = StreamUtil.readAvailableBytes(inputStream);
				System.out.println("---> " + entryName + "\t\t" + bytes.length);
			}
		};
		cs.includeResources(true).scan("d:\\Projects\\java\\apps\\jarminator\\out");
		System.out.println("end");
	}

}
