// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.file;

import jodd.io.findfile.FindClass;
import jodd.io.findfile.ClasspathScanner;
import jodd.io.FileUtil;
import jodd.io.StreamUtil;

import java.io.File;
import java.io.InputStream;

public class fc {

	public static void main(String args[]) throws Exception {
		System.out.println("start");

		ClasspathScanner cs = new ClasspathScanner() {
			@Override
			protected void onClassName(String className, InputStream inputStream) throws Exception {
				byte[] bytes = StreamUtil.readAvailableBytes(inputStream);
				System.out.println("---> " + className + "\t\t" + bytes.length);
			}
		};
		cs.setIncludeResources(true);
		cs.setCreateInputStream(true);
		cs.scan(new File("d:\\Projects\\java\\apps\\jarminator\\out").toURL());
		System.out.println("end");
	}

}
