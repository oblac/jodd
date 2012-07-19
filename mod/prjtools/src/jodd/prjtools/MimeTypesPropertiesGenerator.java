// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.prjtools;

import jodd.io.FileUtil;
import jodd.io.NetUtil;
import jodd.util.StringUtil;

import java.io.IOException;

/**
 * Downloads the most recent "mime.types" Apache file
 * and generated the property file that will be used in Jodd
 */
public class MimeTypesPropertiesGenerator {

	public static final String URL = "http://svn.apache.org/viewvc/httpd/httpd/trunk/docs/conf/mime.types?view=co";

	public static void main(String[] args) throws IOException {
		String mimeTypes = NetUtil.downloadString(URL);

		String[] lines = StringUtil.split(mimeTypes, "\n");

		System.out.println("File downloaded, " + lines.length + " lines.");

		String result = "";

		int count = 0;
		for (String line : lines) {
			line = line.trim();
			if (line.startsWith("#")) {
				continue;
			}

			int ndx = line.indexOf('\t');
			if (ndx == -1) {
				continue;
			}

			String mimeType = line.substring(0, ndx);
			String extensions = line.substring(ndx).trim();

			count++;

			result += mimeType + '=' + extensions;
			result += "\r\n";
		}

		result = result.trim();

		System.out.println(count + " mime types used.");

		FileUtil.writeString("mod\\jodd\\src\\jodd\\util\\MimeTypes.properties", result);

		System.out.println("Done.");
	}
}
