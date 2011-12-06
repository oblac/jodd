// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import java.util.LinkedList;

/**
 * Simple Queue (FIFO) based on LinkedList.
 */
public class SimpleQueue<E> {

	private LinkedList<E> list = new LinkedList<E>();

	/**
	 * Puts object in queue.
	 */
	public void put(E o) {
		list.addLast(o);
	}

	/**
	 * Returns an element (object) from queue.
	 *
	 * @return element from queue or <code>null</code> if queue is empty
	 */
	public E get() {
		if (list.isEmpty()) {
			return null;
		}
		return list.removeFirst();
	}

	/**
	 * Returns all elements from the queue and clears it.
	 */
	public Object[] getAll() {
		Object[] res = new Object[list.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = list.get(i);
		}
		list.clear();
		return res;
	}


	/**
	 * Peeks an element in the queue. Returned elements is not removed from the queue.
	 */
	public E peek() {
		return list.getFirst();
	}

	/**
	 * Returns <code>true</code> if queue is empty, otherwise <code>false</code>
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/**
	 * Returns queue size.
	 */
	public int size() {
		return list.size();
	}
}
