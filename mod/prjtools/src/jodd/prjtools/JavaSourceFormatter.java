// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.prjtools;

import jodd.io.FileUtil;
import jodd.io.findfile.FindFile;
import jodd.io.findfile.WildcardFindFile;
import jodd.util.StringUtil;

import java.io.File;
import java.io.IOException;

/**
 * Formats java sources.
 */
public class JavaSourceFormatter {
	
	private static final String COPYRIGHT = "// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.";

	private static final boolean WRITE_MODE = false;

	public static void main(String[] args) throws IOException {
		JavaSourceFormatter formatter = new JavaSourceFormatter();

		formatter.format("mod/jodd");
	}

	public void format(String sourceRoot) throws IOException {

		System.out.println("*** format: " + sourceRoot);

		FindFile ff = new WildcardFindFile("**/*.java", true)
				.setRecursive(true)
				.setIncludeDirs(false)
				.searchPath(sourceRoot);

		File f;
		while ((f = ff.nextFile()) != null) {
			format(f);
		}
	}

	/**
	 * Formats java file.
	 */
	private void format(File file) throws IOException {

		// reads original content
		String originalContent = FileUtil.readString(file);


		// COPYRIGHT
		String content = StringUtil.trimLeft(originalContent);
		
		int index = StringUtil.indexOfChars(content, "\r\n");
		if (index != -1) {
			content = COPYRIGHT + content.substring(index);
		}

		// the end, detects changes
		if (!originalContent.equals(content)) {
			System.out.println("copyright: " + file.getName());
			if (WRITE_MODE) {
				FileUtil.writeString(file, content);
			}
		}
	}

}
