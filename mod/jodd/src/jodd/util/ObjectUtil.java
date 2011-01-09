// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

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
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Iterator;
import java.util.Enumeration;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.lang.reflect.Array;

/**
 * Various object utilities.
 */
public class ObjectUtil {

	/**
	 * Safely compares two objects just like <code>equals()</code> would, except
	 * it allows any of the 2 objects to be <code>null</code>.
	 *
	 * @return <code>true</code> if arguments are equal, otherwise <code>false</code>
	 */
	public static boolean equals(Object obj1, Object obj2) {
		return (obj1 != null) ? (obj1.equals(obj2)) : (obj2 == null);
	}


	/**
	 * Compares two objects or two object arrays. Useful for {@link Object#equals(Object)}.
	 * @see #equals(Object, Object)
	 */
	public static boolean equalsEx(Object obj1, Object obj2) {
		if (obj1 == null) {
			return (obj2 == null);
		}
		if (obj2 == null) {
			return false;
		}
		if (obj1.getClass().isArray()) {
			if (obj2.getClass().isArray() == false) {
				return false;
			}
			return Arrays.equals((Object[])obj1, (Object[])obj2);
		} else {
			return obj1.equals(obj2);
		}
	}

	/**
	 * Non-symmetric utility for comparing the types of two objects. Might be useful for {@link Object#equals(Object)}
	 * if <code>instanceof</code> is not used.
	 * @param object <code>equals()</code> argument
	 * @param thiz current class that overrides <code>equals()</code>
	 */
	public static boolean equalsType(Object object, Object thiz) {
		return (object != null) && (object.getClass().equals(thiz.getClass()));
	}


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
			throw new CloneNotSupportedException("Can't invoke clone() on object due to: " + ex.getMessage());
		}
	}


	/**
	 * Create object copy using serialization mechanism.
	 */
	public static Object cloneViaSerialization(Serializable obj) throws IOException, ClassNotFoundException {
		FastByteArrayOutputStream bytes = new FastByteArrayOutputStream();
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		Object objCopy = null;
		try {
			out = new ObjectOutputStream(bytes);
			out.writeObject(obj);
			in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
			objCopy = in.readObject();
		} finally {
			StreamUtil.close(out);
			StreamUtil.close(in);
		}
		return objCopy;
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
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(dest);
			oos = new ObjectOutputStream(new BufferedOutputStream(fos));
			oos.writeObject(object);
		} finally {
			StreamUtil.close(fos);
			StreamUtil.close(oos);
		}
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
	 * @see #writeObjectAsXml(java.io.File, Object) 
	 */
	public static void writeObjectAsXml(String dest, Object object) throws IOException {
		writeObjectAsXml(new File(dest), object);
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
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(source);
			ois = new ObjectInputStream(new BufferedInputStream(fis));
			result = ois.readObject();
		} finally {
			StreamUtil.close(fis);
			StreamUtil.close(ois);
		}
		return result;
	}

	/**
	 * Reads serialized object from the XML file.
	 */
	public static Object readObjectAsXml(File source) throws IOException {
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
	 * @see #readObjectAsXml(java.io.File) 
	 */
	public static Object readObjectAsXml(String source) throws IOException {
		return readObjectAsXml(new File(source));
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
			oos.flush();
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


	// ---------------------------------------------------------------- misc

	/**
	 * Returns length of the object.
	 */
	public static int length(Object obj) {
		if (obj == null) {
			return 0;
		}
		if (obj instanceof String) {
			return ((String) obj).length();
		}
		if (obj instanceof Collection) {
			return ((Collection) obj).size();
		}
		if (obj instanceof Map) {
			return ((Map) obj).size();
		}

		int count;
		if (obj instanceof Iterator) {
			Iterator iter = (Iterator) obj;
			count = 0;
			while (iter.hasNext()) {
				count++;
				iter.next();
			}
			return count;
		}
		if (obj instanceof Enumeration) {
			Enumeration enumeration = (Enumeration) obj;
			count = 0;
			while (enumeration.hasMoreElements()) {
				count++;
				enumeration.nextElement();
			}
			return count;
		}
		if (obj.getClass().isArray() == true) {
			return Array.getLength(obj);
		}
		return -1;
	}

	/**
	 * Returns true if first argument contains provided element.
s	 */
	public static boolean containsElement(Object obj, Object element) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof String) {
			if (element == null) {
				return false;
			}
			return ((String)obj).indexOf(element.toString()) != -1;
		}
		if (obj instanceof Collection) {
			return ((Collection) obj).contains(element);
		}
		if (obj instanceof Map) {
			return ((Map) obj).values().contains(element);
		}

		if (obj instanceof Iterator) {
			Iterator iter = (Iterator) obj;
			while (iter.hasNext()) {
				Object o = iter.next();
				if (equals(o, element)) {
					return true;
				}
			}
			return false;
		}
		if (obj instanceof Enumeration) {
			Enumeration enumeration = (Enumeration) obj;
			while (enumeration.hasMoreElements()) {
				Object o = enumeration.nextElement();
				if (equals(o, element)) {
					return true;
				}
			}
			return false;
		}
		if (obj.getClass().isArray() == true) {
			int len = Array.getLength(obj);
			for (int i = 0; i < len; i++) {
				Object o = Array.get(obj, i);
				if (equals(o, element)) {
					return true;
				}
			}
		}
		return false;
	}

}
