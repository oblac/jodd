// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.pathref;

import org.junit.Test;

import java.util.List;

import static jodd.pathref.Pathref.ALL;
import static org.junit.Assert.assertEquals;

public class PathRefTest {

	public static class User {
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Address getAddress() {
			return address;
		}

		public void setAddress(Address address) {
			this.address = address;
		}

		public List<User> getFriends() {
			return friends;
		}

		public void setFriends(List<User> friends) {
			this.friends = friends;
		}

		public User getBestFriend() {
			return bestFriend;
		}

		public void setBestFriend(User bestFriend) {
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

		public void setStreet(String street) {
			this.street = street;
		}

		protected String street;
	}

	@Test
	public void testPathCollection() {
		Pathref<User> p = Pathref.on(User.class);

		assertEquals("address", p.path(p.to().getAddress()));
		assertEquals("address.street", p.path(p.to().getAddress().getStreet()));
		assertEquals("friends[0].address.street", p.path(p.to().getFriends().get(0).getAddress().getStreet()));
		assertEquals("bestFriend.bestFriend.bestFriend.friends[123].address.street",
			p.path(p.to().getBestFriend().getBestFriend().getBestFriend().getFriends().get(123).getAddress().getStreet()));
	}

	@Test
	public void testPathAll() {
		Pathref<User> p = Pathref.on(User.class);

		assertEquals("address", p.path(p.to().getAddress()));
		assertEquals("address.street", p.path(p.to().getAddress().getStreet()));
		assertEquals("friends.address.street", p.path(p.to().getFriends().get(ALL).getAddress().getStreet()));

	}

}