// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.file;

import jodd.io.findfile.FindFile;

import java.io.File;

public class FindBigFiles {
	public static void main(String[] args) {

			FindFile ff = new FindFile()
				.setRecursive(true)
				.setIncludeDirs(true)
				.searchPath("c:/");

		File f;
		while ((f = ff.nextFile()) != null) {
			if (f.isFile()) {
				if (f.length() / 1024.0 / 1024 > 100) {
					System.out.println(f.getAbsolutePath() + "=>" + f.length() / 1024 / 1024 + "MB");
				}
			}
		}
		System.out.println("search is over");
	}
}
