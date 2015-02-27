// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Network {
	String name;
	List<Person> people;

	public Network() {
	}

	public Network(String name, Person... peeps) {
		this.name = name;
		people = new ArrayList<Person>();
		people.addAll(Arrays.asList(peeps));
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List getPeople() {
		return people;
	}

	public void setPeople(List<Person> people) {
		this.people = people;
	}
}
