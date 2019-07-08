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

package jodd.petite.def;

import java.util.Objects;

/**
 * Set of names that represent bean reference for the single injection point.
 * Each injection point can have more then one definition of bean references.
 * They are used when reference name is not explicitly defined.
 */
public class BeanReferences {

	private final String[] names;

	/**
	 * Creates new bean reference.
	 */
	public static BeanReferences of(final String... names) {
		Objects.requireNonNull(names);
		return new BeanReferences(names);
	}

	BeanReferences(final String... names) {
		this.names = names;
	}

	/**
	 * Returns {@code} true if BeanReferences is empty.
	 */
	public boolean isEmpty() {
		return names.length == 0;
	}

	/**
	 * Returns the number of name references in this set.
	 */
	public int size() {
		return names.length;
	}

	/**
	 * Returns the name on the index.
	 */
	public String name(final int ndx) {
		return names[ndx];
	}

	/**
	 * Removes later duplicated references in an array.
	 * Returns new instance of BeanReferences if there was changes,
	 * otherwise returns the same instance.
	 */
	public BeanReferences removeDuplicateNames() {
		if (names.length < 2) {
			return this;
		}

		int nullCount = 0;

		for (int i = 1; i < names.length; i++) {
			String thisRef = names[i];

			if (thisRef == null) {
				nullCount++;
				continue;
			}

			for (int j = 0; j < i; j++) {
				if (names[j] == null) {
					continue;
				}
				if (thisRef.equals(names[j])) {
					names[i] = null;
					break;
				}
			}
		}

		if (nullCount == 0) {
			return this;
		}

		String[] newRefs = new String[names.length - nullCount];
		int ndx = 0;

		for (String name : names) {
			if (name == null) {
				continue;
			}
			newRefs[ndx] = name;
			ndx++;
		}

		return new BeanReferences(newRefs);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append('[');
		for (int i = 0; i < names.length; i++) {
			if (i != 0) {
				sb.append(',');
			}
			sb.append(names[i]);
		}
		sb.append(']');

		return sb.toString();
	}
}
