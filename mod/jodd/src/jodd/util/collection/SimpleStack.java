// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import java.util.LinkedList;

/**
 * Simple Stack (LIFO) class.
 */
public class SimpleStack<E> {

	private LinkedList<E> list = new LinkedList<E>();

	/**
	 * Stack push.
	 */
	public void push(E o) {
		list.addLast(o);
	}

	/**
	 * Stack pop.
	 *
	 * @return poped object from stack
	 */
	public E pop() {
		if (list.isEmpty()) {
			return null;
		}
		return list.removeLast();
	}


	public Object[] popAll() {
		Object[] res = new Object[list.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = list.get(i);
		}
		list.clear();
		return res;
	}

	/**
	 * Peek element from stack.
	 *
	 * @return peeked object
	 */
	public E peek() {
		return list.getLast();
	}


	/**
	 * Is stack empty?
	 *
	 * @return true if stack is empty
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/**
	 * Returns stack size.
	 *
	 * @return	stack size
	 */
	public int size() {
		return list.size();
	}

}
