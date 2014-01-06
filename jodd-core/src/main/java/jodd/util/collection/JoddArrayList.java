// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * Faster drop-in replacement for <code>ArrayList</code> and <code>LinkedList</code>.
 * <code>ArrayList</code> performs slow when elements are added in the middle of the list.
 * Adding element to the first position if slow as the whole buffer has to be moved.
 * <code>LinkedList</code> is slow for random access and always takes more memory then
 * <code>ArrayList</code>.
 * <p>
 * This implementation gives better performances as it can grow both sides: left and right.
 * Its not circular, like some other list implementations out there, so the implementation
 * can remain rather simple. Moreover, if that make more sense, list will not grow,
 * but data will be moved internally to fill up the gaps.
 * <p>
 * This implementation can be fine-tuned. There are the following parameters:
 * <ul>
 *     <li><code>pivotType</code> - pivot splits the list on the left and right side. There
 *     are 3 types of pivots, depending how do you expect the list will be populated
 *     </li>
 *     <li><code>minimalGrowSize</code> - font grow for less then this number.</li>
 *     <li><code>maxFreeSpaceBeforeNormalize</code> - if list has at least this much of space,
 *		don't grow it, but move the buffer data.
 *     </li>
 * </ul>
 */
public class JoddArrayList<E> extends AbstractList<E> implements RandomAccess, Cloneable {

	/**
	 * Defines pivot point types.
	 */
	public enum PIVOT_TYPE {
		FIRST_QUARTER {
			@Override
			public int calculate(int value) {
				return value >> 2;
			}
		},
		HALF {
			@Override
			public int calculate(int value) {
				return value >> 1;
			}
		},
		LAST_QUARTER {
			@Override
			public int calculate(int value) {
				return value - (value >> 2);
			}
		};

		public abstract int calculate(int value);
	}

	private static final int DEFAULT_CAPACITY = 16;		// default capacity
	private static final Object[] EMPTY_BUFFER = {};	// shared empty array instance used for empty instances

	protected Object[] buffer;	// buffer of elements
	protected int size;			// size of the list (number of elements it contains)
	protected int start;		// start index of the first element (inclusive)
	protected int end;			// end index of the last element (exclusive)
	protected int pivotIndex;	// pivot index and initial capacity of the buffer (i.e. buffer.length when buffer is initialized)
	protected PIVOT_TYPE pivotType = PIVOT_TYPE.FIRST_QUARTER;
	protected int minimalGrowSize = 10;			// don't do small grows
	protected int maxFreeSpaceBeforeNormalize = 32;	// max number of free space before we normalize on grow demand

	// ---------------------------------------------------------------- ctor

	/**
	 * Constructs an empty list with the specified initial capacity.
	 */
	public JoddArrayList(int initialCapacity) {
		init(initialCapacity);
	}

	/**
	 * Constructs fine-tuned list.
	 */
	public JoddArrayList(int initialCapacity, PIVOT_TYPE pivot_type, int minimalGrowSize, int maxFreeSpaceBeforeNormalize) {
		init(initialCapacity);
		this.pivotType = pivot_type;
		this.minimalGrowSize = minimalGrowSize;
		this.maxFreeSpaceBeforeNormalize = maxFreeSpaceBeforeNormalize;
	}

	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	public JoddArrayList() {
		init(DEFAULT_CAPACITY);
	}

	/**
	 * Constructs a list containing the elements of the specified
	 * collection, in the order they are returned by the collection's
	 * iterator.
	 */
	public JoddArrayList(Collection<? extends E> collection) {
		buffer = collection.toArray();
		size = buffer.length;
		// c.toArray might (incorrectly) not return Object[] (see 6260652)
		if (buffer.getClass() != Object[].class) {
			Object[] copy = new Object[size];
			System.arraycopy(buffer, 0, copy, 0, size);
			buffer = copy;
		}

		start = 0;
		end = size;
		pivotIndex = pivotType.calculate(size);
	}

	/**
	 * Constructs a list containing the elements of provided array.
	 */
	public JoddArrayList(E... array) {
		buffer = array.clone();
		size = buffer.length;
		start = 0;
		end = size;
		pivotIndex = pivotType.calculate(size);
	}

	/**
	 * Initializes the list.
	 */
	protected void init(int capacity) {
		this.pivotIndex = capacity;
		this.buffer = EMPTY_BUFFER;
		this.size = 0;
		this.start = 0;
		this.end = 0;
	}

	// ---------------------------------------------------------------- size

	/**
	 * Trims the capacity of this <code>ArrayList</code> instance to be the
	 * list's current size.  An application can use this operation to minimize
	 * the storage of an <code>ArrayList</code> instance.
	 */
	public void trimToSize() {
		modCount++;
		if (size < buffer.length) {
			Object[] newBuffer = new Object[size];
			System.arraycopy(buffer, start, newBuffer, 0, size);
			buffer = newBuffer;
			start = 0;
			size = buffer.length;
			end = size;
			pivotIndex = pivotType.calculate(size);
		}
	}

	/**
	 * Normalizes list by moving the content into ideal position
	 * balanced over pivot. It is called to prevent growth
	 * of the buffer when there is enough empty space.
	 */
	protected void normalize() {
		int newPivotIndex = pivotType.calculate(buffer.length);
		int newStart = newPivotIndex - pivotType.calculate(size);
		int newEnd = newStart + size;

		System.arraycopy(buffer, start, buffer, newStart, size);

		if (newStart > start) {
			for (int i = start; i < newStart; i++) {
				buffer[i] = null;
			}
		} else {
			for (int i = Math.max(start, newEnd); i < end; i++) {
				buffer[i] = null;
			}
		}

		start = newStart;
		end = newEnd;
		pivotIndex = newPivotIndex;
	}

	/**
	 * Ensures that buffer size will handle addition of <code>elementsToAdd</code> elements
	 * on provided <code>index</code> position.
	 */
	protected void ensureCapacity(int index, int elementsToAdd) {
		if (buffer == EMPTY_BUFFER) {
			// first time
			if (elementsToAdd <= pivotIndex) {
				// initial addition, fits the initial size
				buffer = new Object[pivotIndex];
			} else {
				// initial addition, does not fit the initial size
				buffer = new Object[elementsToAdd];
			}

			pivotIndex = pivotType.calculate(buffer.length);
			start = pivotIndex;
			end = start;
			size = 0;
			return;
		}

		modCount++;

		int realIndex = start + index;

		if ((realIndex <= pivotIndex) && (realIndex < (end - 1))) {
			// check left side
			int gap = start;
			if (gap < elementsToAdd) {
				// we need to grow left

				if (buffer.length - size - elementsToAdd > maxFreeSpaceBeforeNormalize) {
					// don't grow, we already have enough room, just normalize
					normalize();
					return;
				}

				int currentSize = pivotIndex;
				int newSize = currentSize + (currentSize >> 1);
				int delta = newSize - currentSize;
				if (delta < minimalGrowSize) {
					delta = minimalGrowSize;
				}

				int newGap = gap + delta;

				// is the new size fit for all new elements?
				if (newGap < elementsToAdd) {
					// no, then grow more
					delta = elementsToAdd - gap;
				}

				// grow left for delta places
				int totalSize = buffer.length + delta;

				Object[] newBuffer = new Object[totalSize];
				System.arraycopy(buffer, start, newBuffer, newGap, size);

				// update pointers
				start += delta;
				end += delta;
				pivotIndex += delta;

				buffer = newBuffer;
			}
		} else {
			// check right side
			int gap = buffer.length - end;
			if (gap < elementsToAdd) {
				// we need to grow right

				if (buffer.length - size - elementsToAdd > maxFreeSpaceBeforeNormalize) {
					// don't grow, we already have enough room, just normalize
					normalize();
					return;
				}

				int currentSize = buffer.length - pivotIndex;
				int newSize = currentSize + (currentSize >> 1);
				int delta = newSize - currentSize;
				if (delta < minimalGrowSize) {
					delta = minimalGrowSize;
				}

				int newGap = gap + delta;

				// is the new size fit for all new elements?
				if (newGap < elementsToAdd) {
					// no, then grow more
					delta = elementsToAdd - gap;
				}

				// grow right for delta places
				int totalSize = buffer.length + delta;

				Object[] newBuffer = new Object[totalSize];
				System.arraycopy(buffer, start, newBuffer, start, size);

				// no pointers to update

				buffer = newBuffer;
			}
		}
	}

	// ---------------------------------------------------------------- methods

	/**
	 * Returns the number of elements in this list.
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Returns <code>true</code> if this list contains no elements.
	 */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns <code>true</code> if this list contains the specified element.
	 */
	@Override
	public boolean contains(Object o) {
		return indexOf(o) >= 0;
	}

	/**
	 * Returns the index of the first occurrence of the specified element
	 * in this list, or -1 if this list does not contain the element.
	 * More formally, returns the lowest index <code>i</code> such that
	 * <code>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</code>,
	 * or -1 if there is no such index.
	 */
	@Override
	public int indexOf(Object o) {
		if (o == null) {
			for (int i = start; i < end; i++) {
				if (buffer[i] == null) {
					return i - start;
				}
			}
		} else {
			for (int i = start; i < end; i++) {
				if (o.equals(buffer[i])) {
					return i - start;
				}
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified element
	 * in this list, or -1 if this list does not contain the element.
	 */
	@Override
	public int lastIndexOf(Object o) {
		if (o == null) {
			for (int i = end - 1; i >= start; i--) {
				if (buffer[i] == null) {
					return i - start;
				}
			}
		} else {
			for (int i = end - 1; i >= start; i--) {
				if (o.equals(buffer[i])) {
					return i - start;
				}
			}
		}
		return -1;
	}

	// ---------------------------------------------------------------- clone

	/**
	 * Returns a shallow copy of this <code>ArrayList</code> instance.  (The
	 * elements themselves are not copied.)
	 */
	@Override
	public Object clone() {
		try {
			@SuppressWarnings("unchecked")
			JoddArrayList<E> v = (JoddArrayList<E>) super.clone();
			v.buffer = (buffer == EMPTY_BUFFER ? buffer : buffer.clone());
			v.modCount = 0;
			v.start = start;
			v.end = end;
			v.size = size;
			v.pivotIndex = pivotIndex;
			v.pivotType = pivotType;
			v.minimalGrowSize = minimalGrowSize;
			v.maxFreeSpaceBeforeNormalize = maxFreeSpaceBeforeNormalize;
			return v;
		} catch (CloneNotSupportedException ignore) {
			throw new InternalError();	// this shouldn't happen, since we are Cloneable
		}
	}

	// ---------------------------------------------------------------- to array

	/**
	 * Returns an array containing all of the elements in this list
	 * in proper sequence (from first to last element).
	 */
	@Override
	public Object[] toArray() {
		Object[] copy = new Object[size];
		System.arraycopy(buffer, start, copy, 0, size);
		return copy;
	}

	/**
	 * Returns an array containing all of the elements in this list in proper
	 * sequence (from first to last element); the runtime type of the returned
	 * array is that of the specified array.  If the list fits in the
	 * specified array, it is returned therein.  Otherwise, a new array is
	 * allocated with the runtime type of the specified array and the size of
	 * this list.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] array) {
		if (array.length < size) {
			Class arrayType = array.getClass();
			T[] copy = (arrayType == Object[].class)
			            ? (T[]) new Object[size]
			            : (T[]) Array.newInstance(arrayType.getComponentType(), size);
			System.arraycopy(buffer, start, copy, 0, size);
			return copy;
		}
		System.arraycopy(buffer, start, array, 0, size);
		if (array.length > size) {
			array[size] = null;
		}
		return array;
	}

	// ---------------------------------------------------------------- get/set

	/**
	 * Returns the element at the specified position in this list.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public E get(int index) {
		rangeCheck(index);

		return (E) buffer[start + index];
	}

	/**
	 * Returns the first element in the list.
	 */
	public E getFirst() {
		return get(0);
	}

	/**
	 * Returns the last element of the list.
	 */
	public E getLast() {
		return get(size - 1);
	}

	/**
	 * Replaces the element at the specified position in this list with
	 * the specified element.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public E set(int index, E element) {
		rangeCheck(index);

		index += start;

		E oldValue = (E) buffer[index];
		buffer[index] = element;
		return oldValue;
	}

	// ---------------------------------------------------------------- add

	/**
	 * Appends the specified element to the end of this list.
	 */
	@Override
	public boolean add(E e) {
		int index = size;
		ensureCapacity(index, 1);  // increments modCount!!
		buffer[end] = e;
		end++;
		size++;
		return true;
	}

	/**
	 * Adds the specified element to the beginning of this list.
	 */
	public boolean addFirst(E e) {
		int index = 0;
		ensureCapacity(index, 1);  // increments modCount!!
		if (size > 0) {
			start--;
		} else {
			end++;
		}
		buffer[start] = e;
		size++;
		return true;
	}

	/**
	 * Appends element to the list.
	 */
	public boolean addLast(E e) {
		return add(e);
	}

	/**
	 * Inserts the specified element at the specified position in this
	 * list. Shifts the element currently at that position (if any) and
	 * any subsequent elements to the right (adds one to their indices).
	 */
	@Override
	public void add(int index, E element) {
		if (index == 0) {
			addFirst(element);
			return;
		}
		if (index == size) {
			add(element);
			return;
		}
		rangeCheck(index);

		ensureCapacity(index, 1);		// increments modCount!!

		int realIndex = start + index;

		if (realIndex <= pivotIndex && (realIndex < (end - 1))) {
			// move left
			System.arraycopy(buffer, start, buffer, start - 1, realIndex - start);
			start--;
			realIndex--;
		} else {
			// move right
			System.arraycopy(buffer, realIndex, buffer, realIndex + 1, end - realIndex);
			end++;
		}

		buffer[realIndex] = element;
		size++;
	}

	/**
	 * Appends all of the elements in the specified collection to the end of
	 * this list, in the order that they are returned by the
	 * specified collection's Iterator.
	 */
	@Override
	public boolean addAll(Collection<? extends E> collection) {
		if (collection.isEmpty()) {
			return false;
		}

		Object[] array = collection.toArray();
		return doAddAll(array);
	}

	/**
	 * Appends all elements of given array.
	 */
	public boolean addAll(E... array) {
		if (array.length == 0) {
			return false;
		}

		return doAddAll(array);
	}

	protected boolean doAddAll(Object[] array) {
		int numNew = array.length;
		ensureCapacity(end, numNew);		// increments modCount

		System.arraycopy(array, 0, buffer, end, numNew);
		size += numNew;
		end += numNew;

		return true;
	}

	/**
	 * Inserts all of the elements in the specified collection into this
	 * list, starting at the specified position.  Shifts the element
	 * currently at that position (if any) and any subsequent elements.
	 * The new elements will appear in the list in the order that they are returned by the
	 * specified collection's iterator.
	 */
	@Override
	public boolean addAll(int index, Collection<? extends E> collection) {
		rangeCheck(index);
		Object[] array = collection.toArray();
		return doAddAll(index, array);
	}

	/**
	 * Inserts all array elements to this list.
	 */
	public boolean addAll(int index, E... array) {
		rangeCheck(index);
		return doAddAll(index, array);
	}

	protected boolean doAddAll(int index, Object[] array) {
		int numNew = array.length;
		ensureCapacity(index, numNew);		// increments modCount

		int realIndex = start + index;

		if (realIndex <= pivotIndex) {
			// add left
			int numMoved = index;
			if (numMoved > 0) {
				System.arraycopy(buffer, start, buffer, start - numNew, numMoved);
			}
			realIndex -= numNew;
			System.arraycopy(array, 0, buffer, realIndex, numNew);
			start -= numNew;
		} else {
			// add right
			int numMoved = end - realIndex;
			if (numMoved > 0) {
				System.arraycopy(buffer, realIndex, buffer, realIndex + numNew, numMoved);
			}
			System.arraycopy(array, 0, buffer, realIndex, numNew);
			end += numNew;
		}

		size += numNew;
		return numNew != 0;
	}

	// ---------------------------------------------------------------- clear

	/**
	 * Removes all of the elements from this list.  The list will
	 * be empty after this call returns.
	 */
	@Override
	public void clear() {
		modCount++;

		// clear to let GC do its work
		for (int i = start; i < end; i++) {
			buffer[i] = null;
		}

		pivotIndex = pivotType.calculate(buffer.length);
		start = pivotIndex;
		end = start;
		size = 0;
	}


	// ---------------------------------------------------------------- remove

	/**
	 * Removes first element of the list.
	 */
	public E removeFirst() {
		return remove(0);
	}

	/**
	 * Removes last element of the list.
	 */
	public E removeLast() {
		return remove(size - 1);
	}

	/**
	 * Removes the element at the specified position in this list.
	 * Shifts any subsequent elements.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public E remove(int index) {
		rangeCheck(index);

		modCount++;

		return doRemove(index);
	}

	protected E doRemove(int index) {
		int realIndex = start + index;

		E oldValue = (E) buffer[realIndex];

		if ((realIndex <= pivotIndex) && (realIndex < (end - 1))) {
			// remove left
			int numMoved = index;
			if (numMoved > 0) {
				System.arraycopy(buffer, start, buffer, start + 1, numMoved);
			}
			buffer[start] = null;
			start++;
			size--;
			if (start > pivotIndex) {
				pivotIndex = start;
			}
		} else {
			// remove right
			int numMoved = end - realIndex - 1;
			if (numMoved > 0) {
				System.arraycopy(buffer, realIndex + 1, buffer, realIndex, numMoved);
			}
			end--;
			size--;
			buffer[end] = null;
			if (end <= pivotIndex) {
				pivotIndex = end - 1;
				if (pivotIndex < start) {
					pivotIndex = start;		// make sure that pivot is always >= start
				}
			}
		}

		return oldValue;
	}

	/**
	 * Removes the first occurrence of the specified element from this list,
	 * if it is present.
	 */
	@Override
	public boolean remove(Object o) {
		if (o == null) {
			for (int index = start; index < end; index++) {
				if (buffer[index] == null) {
					doRemove(index - start);
					return true;
				}
			}
		} else {
			for (int index = start; index < end; index++) {
				if (o.equals(buffer[index])) {
					doRemove(index - start);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Removes from this list all of the elements whose index is between
	 * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
	 * Shifts any succeeding elements to the left (reduces their index).
	 */
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		modCount++;

		int numMoved = size - toIndex;

		System.arraycopy(buffer, start + toIndex, buffer, start + fromIndex, numMoved);

		// clear to let GC do its work
		int newSize = size - (toIndex - fromIndex);
		for (int i = start + newSize; i < start + size; i++) {
			buffer[i] = null;
		}

		size = newSize;
		end = start + size;
		pivotIndex = start + pivotType.calculate(size);
	}


	/**
	 * Removes from this list all of its elements that are contained in the
	 * specified collection.
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		return batchRemove(c, false);
	}

	/**
	 * Retains only the elements in this list that are contained in the
	 * specified collection.  In other words, removes from this list all
	 * of its elements that are not contained in the specified collection.
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return batchRemove(c, true);
	}

	protected boolean batchRemove(Collection<?> collection, boolean complement) {
		int r = 0, w = 0;
		boolean modified = false;
		try {
			for (; r < size; r++) {
				Object element = buffer[start + r];
				if (collection.contains(element) == complement) {
					buffer[start + w++] = buffer[start + r];
				}
			}
		} finally {
			// Preserve behavioral compatibility with AbstractCollection,
			// even if c.contains() throws.

			// copy leftovers
			if (r != size) {
				System.arraycopy(buffer, start + r, buffer, start + w, size - r);
				w += size - r;
			}

			// is there a change in the buffer?
			if (w != size) {
				// clear to let GC do its work
				for (int i = w; i < size; i++) {
					buffer[start + i] = null;
				}

				modCount += size - w;
				size = w;
				modified = true;

				end = start + size;
				pivotIndex = start + pivotType.calculate(size);
			}
		}

		return modified;
	}

	// ---------------------------------------------------------------- checks

	/**
	 * A version of rangeCheck used by add and addAll.
	 */
	private void rangeCheck(int index) {
		if (index < 0 || index > size) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}
	}


	// ---------------------------------------------------------------- iterators

	/**
	 * Returns a list iterator over the elements in this list (in proper
	 * sequence), starting at the specified position in the list.
	 */
	@Override
	public ListIterator<E> listIterator(int index) {
		rangeCheck(index);
		return new ListItr(index);
	}

	/**
	 * Returns a list iterator over the elements in this list (in proper
	 * sequence).
	 * @see #listIterator(int)
	 */
	@Override
	public ListIterator<E> listIterator() {
		return new ListItr(0);
	}

	/**
	 * Returns an iterator over the elements in this list in proper sequence.
	 */
	@Override
	public Iterator<E> iterator() {
		return new Itr();
	}

	/**
	 * An optimized version of <code>AbstractList.Itr</code>.
	 */
	private class Itr implements Iterator<E> {
		int cursor;       // index of next element to return
		int lastRet = -1; // index of last element returned; -1 if no such
		int expectedModCount = modCount;

		public boolean hasNext() {
			return cursor != size;
		}

		@SuppressWarnings("unchecked")
		public E next() {
			checkForComodification();
			int i = cursor;
			if (i >= size) {
				throw new NoSuchElementException();
			}

			cursor = i + 1;
			lastRet = i;
			return (E) buffer[start + i];
		}

		public void remove() {
			if (lastRet < 0) {
				throw new IllegalStateException();
			}
			checkForComodification();

			try {
				JoddArrayList.this.remove(lastRet);
				cursor = lastRet;
				lastRet = -1;
				expectedModCount = modCount;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}

		final void checkForComodification() {
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
		}
	}

	/**
	 * An optimized version of <code>AbstractList.ListItr</code>.
	 */
	private class ListItr extends Itr implements ListIterator<E> {
		ListItr(int index) {
			super();
			cursor = index;
		}

		public boolean hasPrevious() {
			return cursor != 0;
		}

		public int nextIndex() {
			return cursor;
		}

		public int previousIndex() {
			return cursor - 1;
		}

		@SuppressWarnings("unchecked")
		public E previous() {
			checkForComodification();
			int i = cursor - 1;
			if (i < 0) {
				throw new NoSuchElementException();
			}
			if (i >= size) {
				throw new ConcurrentModificationException();
			}
			cursor = i;
			lastRet = i;
			return (E) buffer[start + i];
		}

		public void set(E e) {
			if (lastRet < 0) {
				throw new IllegalStateException();
			}
			checkForComodification();

			try {
				JoddArrayList.this.set(lastRet, e);
			} catch (IndexOutOfBoundsException ignore) {
				throw new ConcurrentModificationException();
			}
		}

		public void add(E e) {
			checkForComodification();

			try {
				int i = cursor;
				JoddArrayList.this.add(i, e);
				cursor = i + 1;
				lastRet = -1;
				expectedModCount = modCount;
			} catch (IndexOutOfBoundsException ignore) {
				throw new ConcurrentModificationException();
			}
		}
	}

	// ---------------------------------------------------------------- to string

	/**
	 * Returns string representation of this array.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		if (buffer != EMPTY_BUFFER) {
			for (int i = start; i < end; i++) {
				if (i != start) {
					sb.append(',');
				}
				sb.append(buffer[i]);
			}
		}
		sb.append(']');
		return sb.toString();
	}

}