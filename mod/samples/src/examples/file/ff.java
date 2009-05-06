// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.file;

import jodd.io.findfile.FindFile;
import jodd.io.findfile.WildcardFindFile;
import jodd.util.SystemUtil;

import java.io.File;

public class ff {
	public static void main(String args[]) {
		System.out.println("---start---");
		System.out.println("searching classpath");

		FindFile ff = new WildcardFindFile("*").recursive(true).includeDirs(true).searchPath(SystemUtil.getClassPath());
//		FindFile ff = new WildcardFindFile("c:/temp/temp","*").includeDirs().recursive();

		int i = 1;
		File f;
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
