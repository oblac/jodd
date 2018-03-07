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

package jodd.json;

import jodd.util.StringUtil;

/**
 * Path to a property from JSON root.
 */
public final class Path implements Cloneable {

	protected CharSequence[] paths = new CharSequence[8];
	protected int index = 0;
	protected Path altPath;

	/**
	 * Parses input dot-separated string that represents a path.
	 */
	public static Path parse(final String path) {
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
	public Path(final CharSequence... fields) {
		if (fields.length >= paths.length) {
			paths = fields;
		}
		else {
			System.arraycopy(fields, 0, paths, 0, fields.length);
			index = fields.length;
		}
	}

	private Path(CharSequence[] paths, int index, Path altPath) {
		this.paths = paths;
		this.index = index;
		this.altPath = altPath;
	}

	/**
	 * Returns alternative path.
	 */
	public Path getAltPath() {
		return altPath;
	}

	/**
	 * Push element to the path.
	 */
	public Path push(final CharSequence field) {
		_push(field);

		if (altPath != null) {
			altPath.push(field);
		}
		return this;
	}

	public Path push(final CharSequence field, final CharSequence altField) {
		_push(field);

		if (altPath != null) {
			altPath.push(altField);
		}
		return this;
	}

	private void _push(final CharSequence field) {
		if (index == paths.length) {	// ensure size
			CharSequence[] newPaths = new CharSequence[paths.length << 1];
			System.arraycopy(paths, 0, newPaths, 0, paths.length);
			paths = newPaths;
		}

		paths[index] = field;
		index++;
	}

	/**
	 * Pop last element from the path.
	 */
	public CharSequence pop(){
		if (altPath != null) {
			altPath.pop();
		}
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
	public CharSequence get(final int i) {
		if (i >= index) {
			throw new IndexOutOfBoundsException();
		}
		return paths[i];
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append('[');
		for (int i = 0; i < index; i++) {
			CharSequence current = paths[i];
			if (i > 0) {
				builder.append('.');
			}
			builder.append(current);
		}

		builder.append(']');
		return builder.toString();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		// correct, but aint gonna happen
//		if (o == null || getClass() != o.getClass()) {
//			return false;
//		}

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

	@Override
	public int hashCode() {
		int result = 1;

		for (int i = 0; i < index; i++) {
			CharSequence element = paths[i];
			result = 31 * result + (element == null ? 0 : element.hashCode());
		}

		return result;
	}

	@Override
	public Path clone() {
		CharSequence[] clonedPaths = new CharSequence[paths.length];

		System.arraycopy(paths, 0, clonedPaths, 0, paths.length);

		return new Path(clonedPaths, index, altPath != null ? altPath.clone() : null);
	}
}