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

import jodd.io.FastByteArrayOutputStream;
import jodd.io.StreamUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Various object utilities.
 */
public class ObjectUtil {

	// ---------------------------------------------------------------- clone

	/**
	 * Clone an object by invoking it's <code>clone()</code> method, even if it is not overridden.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clone(final T source) throws CloneNotSupportedException {
		if (source == null) {
			return null;
		}
		try {
			Method cloneMethod = source.getClass().getDeclaredMethod("clone");
			cloneMethod.setAccessible(true);
			return (T) cloneMethod.invoke(source);
		} catch (Exception ex) {
			throw new CloneNotSupportedException("Can't clone() the object: " + ex.getMessage());
		}
	}


	/**
	 * Create object copy using serialization mechanism.
	 */
	public static <T extends Serializable> T cloneViaSerialization(final T obj) throws IOException, ClassNotFoundException {
		FastByteArrayOutputStream bos = new FastByteArrayOutputStream();
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		Object objCopy = null;

		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(obj);
			out.flush();

			byte[] bytes = bos.toByteArray();

			in = new ObjectInputStream(new ByteArrayInputStream(bytes));
			objCopy = in.readObject();
		} finally {
			StreamUtil.close(out);
			StreamUtil.close(in);
		}
		return (T) objCopy;
	}


	// ---------------------------------------------------------------- serialization to file


	/**
	 * @see #writeObject(java.io.File, Object)
	 */
	public static void writeObject(final String dest, final Object object) throws IOException {
		writeObject(new File(dest), object);
	}

	/**
	 * Writes serializable object to a file. Existing file will be overwritten.
	 */
	public static void writeObject(final File dest, final Object object) throws IOException {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ObjectOutputStream oos = null;

		try {
			fos = new FileOutputStream(dest);
			bos = new BufferedOutputStream(fos);
			oos = new ObjectOutputStream(bos);

			oos.writeObject(object);
		} finally {
			StreamUtil.close(oos);
			StreamUtil.close(bos);
			StreamUtil.close(fos);
		}
	}

	/**
	 * @see #readObject(java.io.File)
	 */
	public static Object readObject(final String source) throws IOException, ClassNotFoundException {
		return readObject(new File(source));
	}

	/**
	 * Reads serialized object from the file.
	 */
	public static Object readObject(final File source) throws IOException, ClassNotFoundException {
		Object result = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ObjectInputStream ois = null;

		try {
			fis = new FileInputStream(source);
			bis = new BufferedInputStream(fis);
			ois = new ObjectInputStream(bis);

			result = ois.readObject();
		} finally {
			StreamUtil.close(ois);
			StreamUtil.close(bis);
			StreamUtil.close(fis);
		}
		return result;
	}

	// ---------------------------------------------------------------- serialization to byte array

	/**
	 * Serialize an object to byte array.
	 */
	public static byte[] objectToByteArray(final Object obj) throws IOException {
		FastByteArrayOutputStream bos = new FastByteArrayOutputStream();
		ObjectOutputStream oos = null;

		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
		} finally {
			StreamUtil.close(oos);
		}
		return bos.toByteArray();
	}

	/**
	 * De-serialize an object from byte array.
	 */
	public static Object byteArrayToObject(final byte[] data) throws IOException, ClassNotFoundException {
		Object retObj = null;
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ObjectInputStream ois = null;

		try {
			ois = new ObjectInputStream(bais);
			retObj = ois.readObject();
		} finally {
			StreamUtil.close(ois);
		}
		return retObj;
	}



}