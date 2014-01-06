// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.io.StreamUtil;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Object to XML (de)serialization.
 * Uses internal classes of <code>java.beans</code> package.
 */
public class ObjectXmlUtil {


	/**
	 * @see #writeObjectAsXml(java.io.File, Object)
	 */
	public static void writeObjectAsXml(String dest, Object object) throws IOException {
		writeObjectAsXml(new File(dest), object);
	}

	/**
	 * Writes serializable object to a XML file. Existing file will be overwritten.
	 */
	public static void writeObjectAsXml(File dest, Object object) throws IOException {
		FileOutputStream fos = null;
		XMLEncoder xmlenc = null;
		try {
			fos = new FileOutputStream(dest);
			xmlenc = new XMLEncoder(new BufferedOutputStream(fos));
			xmlenc.writeObject(object);
		} finally {
			StreamUtil.close(fos);
			if (xmlenc != null) {
				xmlenc.close();
			}
		}
	}

	/**
	 * Reads serialized object from the XML file.
	 */
	public static Object readObjectFromXml(File source) throws IOException {
		Object result = null;
		FileInputStream fis = null;
		XMLDecoder xmldec = null;
		try {
			fis = new FileInputStream(source);
			xmldec = new XMLDecoder(new BufferedInputStream(fis));
			result = xmldec.readObject();
		} finally {
			StreamUtil.close(fis);
			if (xmldec != null) {
				xmldec.close();
			}
		}
		return result;
	}

	/**
	 * @see #readObjectFromXml(java.io.File)
	 */
	public static Object readObjectFromXml(String source) throws IOException {
		return readObjectFromXml(new File(source));
	}


}
