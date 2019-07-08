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

package jodd.props;

import jodd.util.CollectionUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Props iterator builder. Should be used with: {@link jodd.props.Props#entries()}.
 */
public final class PropsEntries {

	private final PropsIterator propsIterator;
	private final Props props;

	public PropsEntries(final Props props) {
		this.props = props;
		this.propsIterator = new PropsIterator();
	}

	/**
	 * Enables profile to iterate.
	 */
	public PropsEntries profile(final String profile) {
		addProfiles(profile);
		return this;
	}
	/**
	 * Enables profiles to iterate.
	 */
	public PropsEntries profile(final String... profiles) {
		if (profiles == null) {
			return this;
		}
		for (String profile : profiles) {
			addProfiles(profile);
		}
		return this;
	}

	/**
	 * Enables active profiles to iterate over.
	 */
	public PropsEntries activeProfiles() {
		profile(props.activeProfiles);
		return this;
	}

	private void addProfiles(final String profile) {
		if (propsIterator.profiles == null) {
			propsIterator.profiles = new ArrayList<>();
		}
		propsIterator.profiles.add(profile);
	}

	/**
	 * Enables section to iterate.
	 */
	public PropsEntries section(final String section) {
		addSection(section);
		return this;
	}
	/**
	 * Enables sections to iterate.
	 */
	public PropsEntries section(final String... section) {
		for (String s : section) {
			addSection(s);
		}
		return this;
	}

	private void addSection(final String section) {
		if (propsIterator.sections == null) {
			propsIterator.sections = new ArrayList<>();
		}
		propsIterator.sections.add(section + '.');
	}

	/**
	 * Skips duplicate keys (defined in different profiles) which value is not
	 * used for setting current key value.
	 */
	public PropsEntries skipDuplicatesByValue() {
		propsIterator.skipDuplicatesByValue = true;
		propsIterator.skipDuplicatesByPosition = false;
		return this;
	}

	/**
	 * Skips all keys after first definition, even if value is set later.
	 */
	public PropsEntries skipDuplicatesByPosition() {
		propsIterator.skipDuplicatesByPosition = true;
		propsIterator.skipDuplicatesByValue = false;
		return this;
	}

	/**
	 * Returns populated iterator.
	 */
	public Iterator<PropsEntry> iterator() {
		return propsIterator;
	}

	/**
	 * Consumer all properties.
	 */
	public void forEach(final Consumer<PropsEntry> propsDataConsumer) {
		CollectionUtil.streamOf(propsIterator).forEach(propsDataConsumer);
	}

	// ---------------------------------------------------------------- iterator

	/**
	 * Props iterator.
	 */
	private class PropsIterator implements Iterator<PropsEntry> {
		private PropsEntry next = props.data.first;
		private boolean firstTime = true;
		private List<String> profiles;
		private List<String> sections;
		private boolean skipDuplicatesByValue;
		private boolean skipDuplicatesByPosition;
		private Set<String> keys;

		@Override
		public boolean hasNext() {
			if (firstTime) {
				start();
			}
			return next != null;
		}

		/**
		 * Starts with the iterator.
		 */
		private void start() {
			firstTime = false;

			while (!accept(next)) {
				if (next == null) {
					break;
				}
				next = next.next;		// funny :)))
			}
		}

		/**
		 * Accepts an entry and returns <code>true</code>
		 * if entry should appear in this iteration.
		 */
		private boolean accept(final PropsEntry entry) {
			if (entry == null) {
				return false;
			}
			if (profiles != null) {
				if (entry.getProfile() != null) {
					boolean found = false;
					for (String profile : profiles) {
						if (entry.getProfile().equals(profile)) {
							found = true;
							break;
						}
					}
					if (!found) {
						return false;
					}
				}
			} else {
				// ignore all profile keys if only a base profile is active
				if (entry.getProfile() != null) {
					return false;
				}
			}

			if (sections != null) {
				boolean found = false;
				for (String section : sections) {
					if (entry.getKey().startsWith(section)) {
						found = true;
						break;
					}
				}
				if (!found) {
					return false;
				}
			}

			if (profiles != null) {
				if (skipDuplicatesByValue) {
					String thisProfile = entry.getProfile();

					// iterate all profiles before this one
					for (String profile : profiles) {
						if (profile.equals(thisProfile)) {
							// if we came to this point, there is no
							// property defined in higher profile
							// therefore this one is the most important
							return true;
						}

						// check if key exist in higher profile
						Map<String, PropsEntry> profileMap = props.data.profileProperties.get(profile);
						if (profileMap == null) {
							continue;
						}
						if (profileMap.containsKey(entry.getKey())) {
							// duplicate key exist in higher profile, therefore this one is less important
							return false;
						}
					}
				}
				if (skipDuplicatesByPosition) {
					if (keys == null) {
						keys = new HashSet<>();
					}
					if (!keys.add(entry.getKey())) {
						return false;		// the key was already there
					}
				}
			}

			return true;
		}

		@Override
		public PropsEntry next() {
			if (firstTime) {
				start();
			}

			if (next == null) {
				throw new NoSuchElementException();
			}

			PropsEntry returnValue =  next;

			while (next != null) {
				next = next.next;

				if (accept(next)) {
					break;
				}
			}

			return returnValue;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}