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

package jodd.pathref;

import org.junit.jupiter.api.Test;

import java.util.List;

import static jodd.pathref.Pathref.ALL;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PathRefTest {

	public static class User {
		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public Address getAddress() {
			return address;
		}

		public void setAddress(final Address address) {
			this.address = address;
		}

		public List<User> getFriends() {
			return friends;
		}

		public void setFriends(final List<User> friends) {
			this.friends = friends;
		}

		public User getBestFriend() {
			return bestFriend;
		}

		public void setBestFriend(final User bestFriend) {
			this.bestFriend = bestFriend;
		}

		protected String name;
		protected Address address;
		protected List<User> friends;
		protected User bestFriend;
	}

	public static class Address {
		public String getStreet() {
			return street;
		}

		public void setStreet(final String street) {
			this.street = street;
		}

		protected String street;
	}

	@Test
	void testPathCollection() {
		final Pathref<User> p = Pathref.of(User.class);

		assertEquals("address", p.path(User::getAddress));
		assertEquals("address.street", p.path((u) -> u.getAddress().getStreet()));
		assertEquals("friends[0].address.street", p.path((u) -> u.getFriends().get(0).getAddress().getStreet()));
		assertEquals("bestFriend.bestFriend.bestFriend.friends[123].address.street",
			p.path((u) -> u.getBestFriend().getBestFriend().getBestFriend().getFriends().get(123).getAddress().getStreet()));
	}

	@Test
	void testPathAll() {
		final Pathref<User> p = Pathref.of(User.class);

		assertEquals("address", p.path((u) -> u.getAddress()));
		assertEquals("address.street", p.path((u) -> u.getAddress().getStreet()));
		assertEquals("friends.address.street", p.path((u) -> u.getFriends().get(ALL).getAddress().getStreet()));
	}

}
