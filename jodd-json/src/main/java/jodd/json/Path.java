// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.util.StringUtil;

/**
 * Path to a property from JSON root.
 */
public class Path {

	protected String[] paths = new String[8];
	protected int index = 0;

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
		if (fields.length >= paths.length) {
			paths = fields;
		}
		else {
			System.arraycopy(fields, 0, paths, 0, fields.length);
			index = fields.length;
		}
	}

	/**
	 * Push element to the path.
	 */
	public Path push(String field) {
		if (index == paths.length) {	// ensure size
			String[] newPaths = new String[paths.length << 1];
			System.arraycopy(paths, 0, newPaths, 0, paths.length);
			paths = newPaths;
		}

		paths[index] = field;
		index++;

		return this;
	}

	/**
	 * Pop last element from the path.
	 */
	public String pop() {
		return paths[--index];
	}

	/**
	 * Returns path length.
	 */
	public int length() {
		return index;
	}

	/**
	 * Returns path chunk at given index.
	 */
	public String get(int i) {
		if (i >= index) {
			throw new IndexOutOfBoundsException();
		}
		return paths[i];
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append('[');
		for (int i = 0; i < index; i++) {
			String current = paths[i];
			if (i > 0) {
				builder.append('.');
			}
			builder.append(current);
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

		int length = path1.length();

		if (this.length() != length) {
			return false;
		}

		for (int i = 0; i < length; i++) {
			Object o1 = path1.paths[i];
			Object o2 = paths[i];

			if (!(o1 == null ? o2 == null : o1.equals(o2))) {
				return false;
			}
		}

		return true;
	}

	public int hashCode() {
		int result = 1;

		for (int i = 0; i < index; i++) {
			String element = paths[i];
			result = 31 * result + (element == null ? 0 : element.hashCode());
		}

		return result;
	}

}