// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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
