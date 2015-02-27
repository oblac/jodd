// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import jodd.io.FastCharArrayWriter;
import jodd.io.StreamUtil;
import jodd.util.ClassLoaderUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

public abstract class BasePropsTest {

	protected String readDataFile(String fileName) throws IOException {
		String dataFolder = this.getClass().getPackage().getName() + ".data.";
		dataFolder = dataFolder.replace('.', '/');

		InputStream is = ClassLoaderUtil.getResourceAsStream(dataFolder + fileName);
		Writer out = new FastCharArrayWriter();
		String encoding = "UTF-8";
		if (fileName.endsWith(".properties")) {
			encoding = "ISO-8859-1";
		}
		StreamUtil.copy(is, out, encoding);
		StreamUtil.close(is);
		return out.toString();
	}

	protected Props loadProps(Props p, String fileName) throws IOException {
		String dataFolder = this.getClass().getPackage().getName() + ".data.";
		dataFolder = dataFolder.replace('.', '/');

		InputStream is = ClassLoaderUtil.getResourceAsStream(dataFolder + fileName);
		String encoding = "UTF-8";
		if (fileName.endsWith(".properties")) {
			encoding = "ISO-8859-1";
		}
		p.load(is, encoding);
		return p;
	}

	protected Props loadProps(String fileName) throws IOException {
		Props p = new Props();
		return loadProps(p, fileName);
	}
}