// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.util.StringUtil;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Path to a property from JSON root.
 */
public class Path {

	protected LinkedList<String> path = new LinkedList<String>();

	/**
	 * Parses input dot-separated string that represents a path.
	 */
	public static Path parse(String path) {
		if (path == null) {
			return new Path();
		}
		return new Path(StringUtil.splitc(path, '.'));
	}

	public Path() {
	}

	/**
	 * Creates path from given path elements.
	 */
	public Path(String... fields) {
		Collections.addAll(path, fields);
	}

	/**
	 * Push element to the path.
	 */
	public Path push(String field) {
		path.add(field);
		return this;
	}

	/**
	 * Pop last element from the path.
	 */
	public String pop() {
		return path.removeLast();
	}

	/**
	 * Returns path length.
	 */
	public int length() {
		return path.size();
	}

	public String toString() {
		StringBuilder builder = new StringBuilder("[");

		boolean afterFirst = false;

		for (String current : path) {
			if (afterFirst) {
				builder.append('.');
			}
			builder.append(current);
			afterFirst = true;
		}

		builder.append(']');
		return builder.toString();
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Path path1 = (Path) o;

		if (!path.equals(path1.path)) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return path.hashCode();
	}

}