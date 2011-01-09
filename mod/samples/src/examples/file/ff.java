// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.file;

import jodd.io.findfile.FindFile;
import jodd.io.findfile.WildcardFindFile;
import jodd.io.findfile.FilepathScanner;
import jodd.io.filter.WildcardFileFilter;
import jodd.util.SystemUtil;

import java.io.File;

public class ff {
	public static void main(String args[]) {
		System.out.println("---start---");

		File f = new File(".");
		String[] list = f.list(new WildcardFileFilter("*"));
		for (String l : list) {
			System.out.println(l);
		}

		System.out.println("\n\n\nsearching classpath");

		FilepathScanner fs = new FilepathScanner() {
			@Override
			protected void onFile(File file) {
				System.out.println("***" + file.getName());
			}
		}.includeDirs(true).recursive(true).includeFiles(false);
		fs.scan("d:\\temp\\temp\\");

		System.out.println("\n\n\nsearching classpath");

		FindFile ff = new WildcardFindFile("*").recursive(true).includeDirs(true).searchPath(SystemUtil.getClassPath());
//		FindFile ff = new WildcardFindFile("c:/temp/temp","*").includeDirs().recursive();

		int i = 1;

		while ((f = ff.nextFile()) != null) {
			if (f.isDirectory() == true) {
				System.out.println(i + ". >" + f.getName());
			} else {
				System.out.println(i + ". " + f.getName());
			}
			i++;
		}
		System.out.println("---end---");
	}
}
