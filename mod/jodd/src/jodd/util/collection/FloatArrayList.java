// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.IOException;

/**
 * ArrayList of float primitives.
 */
public class FloatArrayList implements Serializable {

	private float[] array;
	private int size;

	public static int initialCapacity = 10;

	/**
	 * Constructs an empty list with an initial capacity.
	 */
	public FloatArrayList() {
		this(initialCapacity);
	}

	/**
	 * Constructs an empty list with the specified initial capacity.
	 */
	public FloatArrayList(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("Capacity can't be negative: " + initialCapacity);
		}
		array = new float[initialCapacity];
		size = 0;
	}

	/**
	 * Constructs a list containing the elements of the specified array.
	 * The list instance has an initial capacity of 110% the size of the specified array.
	 */
	public FloatArrayList(float[] data) {
		array = new float[(int) (data.length * 1.1) + 1];
		size = data.length;
		System.arraycopy(data, 0, array, 0, size);
	}

	// ---------------------------------------------------------------- conversion

	/**
	 * Returns an array containing all of the elements in this list in the correct order.
	 */
	public float[] toArray() {
		float[] result = new float[size];
		System.arraycopy(array, 0, result, 0, size);
		return result;
	}

	// ---------------------------------------------------------------- methods

	/**
	 * Returns the element at the specified position in this list.
	 */
	public float get(int index) {
		checkRange(index);
		return array[index];
	}

	/**
	 * Returns the number of elements in this list.
	 */
	public int size() {
		return size;
	}

	/**
	 * Removes the element at the specified position in this list.
	 * Shifts any subsequent elements to the left (subtracts
	 * one from their indices).
	 *
	 * @param index the index of the element to remove
	 * @return the value of the element that was removed
	 * @throws UnsupportedOperationException when this operation is not
	 *                                       supported
	 * @throws IndexOutOfBoundsException	 if the specified index is out of range
	 */
	public float remove(int index) {
		checkRange(index);
		float oldval = array[index];
		int numtomove = size - index - 1;
		if (numtomove > 0) {
			System.arraycopy(array, index + 1, array, index, numtomove);
		}
		size--;
		return oldval;
	}
	/**
	 * Removes from this list all of the elements whose index is between fromIndex,
	 * inclusive and toIndex, exclusive. Shifts any succeeding elements to the left (reduces their index).
	 */
	public void removeRange(int fromIndex, int toIndex) {
		checkRange(fromIndex);
		checkRange(toIndex);
		if (fromIndex >= toIndex) {
			return;
		}
		int numtomove = size - toIndex;
		if (numtomove > 0) {
			System.arraycopy(array, toIndex, array, fromIndex, numtomove);
		}
		size -= (toIndex - fromIndex);
	}

	/**
	 * Replaces the element at the specified position in this list with the specified element.
	 *
	 * @param index   the index of the element to change
	 * @param element the value to be stored at the specified position
	 * @return the value previously stored at the specified position
	 */
	public float set(int index, float element) {
		checkRange(index);
		float oldval = array[index];
		array[index] = element;
		return oldval;
	}

	/**
	 * Appends the specified element to the end of this list.
	 */
	public void add(float element) {
		ensureCapacity(size + 1);
		array[size++] = element;
	}

	/**
	 * Inserts the specified element at the specified position in this list.
	 * Shifts the element currently at that position (if any) and any subsequent
	 * elements to the right (adds one to their indices).
	 *
	 * @param index   the index at which to insert the element
	 * @param element the value to insert
	 */
	public void add(int index, float element) {
		checkRangeIncludingEndpoint(index);
		ensureCapacity(size + 1);
		int numtomove = size - index;
		System.arraycopy(array, index, array, index + 1, numtomove);
		array[index] = element;
		size++;
	}

	/**
	 * Appends all of the elements in the specified array to the end of this list.
	 */
	public void addAll(float[] data) {
		int dataLen = data.length;
		if (dataLen == 0) {
			return;
		}
		int newcap = size + (int) (dataLen * 1.1) + 1;
		ensureCapacity(newcap);
		System.arraycopy(data, 0, array, size, dataLen);
		size += dataLen;
	}

	/**
	 * Appends all of the elements in the specified array at the specified position in this list.
	 */
	public void addAll(int index, float[] data) {
		int dataLen = data.length;
		if (dataLen == 0) {
			return;
		}
		int newcap = size + (int) (dataLen * 1.1) + 1;
		ensureCapacity(newcap);
		System.arraycopy(array, index, array, index + dataLen, size - index);
		System.arraycopy(data, 0, array, index, dataLen);
		size += dataLen;
	}

	/**
	 * Removes all of the elements from this list.
	 * The list will be empty after this call returns.
	 */
	public void clear() {
		size = 0;
	}

	// ---------------------------------------------------------------- search

	/**
	 * Returns true if this list contains the specified element.
	 */
	public boolean contains(float data, float delta) {
		for (int i = 0; i < size; i++) {
			if (Math.abs(array[i] - data) <= delta) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Searches for the first occurence of the given argument.
	 */
	public int indexOf(float data, float delta) {
		for (int i = 0; i < size; i++) {
			if (Math.abs(array[i] - data) <= delta) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified object in this list.
	 */
	public int lastIndexOf(float data, float delta) {
		for (int i = size - 1; i >= 0; i--) {
			if (Math.abs(array[i] - data) <= delta) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Tests if this list has no elements.
	 */
	public boolean isEmpty() {
		return size == 0;
	}



	// ---------------------------------------------------------------- capacity

	/**
	 * Increases the capacity of this ArrayList instance, if necessary,
	 * to ensure that it can hold at least the number of elements specified by
	 * the minimum capacity argument.
	 */
	public void ensureCapacity(int mincap) {
		if (mincap > array.length) {
			int newcap = ((array.length * 3) >> 1) + 1;
			float[] olddata = array;
			array = new float[newcap < mincap ? mincap : newcap];
			System.arraycopy(olddata, 0, array, 0, size);
		}
	}

	/**
	 * Trims the capacity of this instance to be the list's current size.
	 * An application can use this operation to minimize the storage of some instance.
	 */
	public void trimToSize() {
		if (size < array.length) {
			float[] olddata = array;
			array = new float[size];
			System.arraycopy(olddata, 0, array, 0, size);
		}
	}

	// ---------------------------------------------------------------- serializable

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeInt(array.length);
		for (int i = 0; i < size; i++) {
			out.writeFloat(array[i]);
		}
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		array = new float[in.readInt()];
		for (int i = 0; i < size; i++) {
			array[i] = in.readFloat();
		}
	}

	// ---------------------------------------------------------------- privates

	private void checkRange(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index should be at least 0 and less than " + size + ", found " + index);
		}
	}

	private void checkRangeIncludingEndpoint(int index) {
		if (index < 0 || index > size) {
			throw new IndexOutOfBoundsException("Index should be at least 0 and at most " + size + ", found " + index);
		}
	}

}
