// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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

/**
 * Various object utilities.
 */
public class ObjectUtil {


	// ---------------------------------------------------------------- clone

	/**
	 * Clone an object by invoking it's <code>clone()</code> method, even if it is not overridden.
	 */
	public static Object clone(Object source) throws CloneNotSupportedException {
		if (source == null) {
			return null;
		}
		try {
			return ReflectUtil.invokeDeclared(source, "clone", new Class[]{}, new Object[] {});
		} catch (Exception ex) {
			throw new CloneNotSupportedException("Can't clone() the object: " + ex.getMessage());
		}
	}


	/**
	 * Create object copy using serialization mechanism.
	 */
	public static <T extends Serializable> T cloneViaSerialization(T obj) throws IOException, ClassNotFoundException {
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
	public static void writeObject(String dest, Object object) throws IOException {
		writeObject(new File(dest), object);
	}

	/**
	 * Writes serializable object to a file. Existing file will be overwritten.
	 */
	public static void writeObject(File dest, Object object) throws IOException {
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
	public static Object readObject(String source) throws IOException, ClassNotFoundException {
		return readObject(new File(source));
	}

	/**
	 * Reads serialized object from the file.
	 */
	public static Object readObject(File source) throws IOException, ClassNotFoundException {
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
	public static byte[] objectToByteArray(Object obj) throws IOException {
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
	public static Object byteArrayToObject(byte[] data) throws IOException, ClassNotFoundException {
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