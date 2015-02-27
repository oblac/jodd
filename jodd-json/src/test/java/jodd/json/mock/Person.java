// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.mock;

import jodd.json.meta.JSON;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Person {

	private String firstname;
	private String lastname;
	private Date birthdate;
	private Timestamp firstBaseBallGame;
	private Address home;
	private Address work;
	private List<Phone> phones = new ArrayList<Phone>();
	private List<String> hobbies = new ArrayList<String>();

	public Person() {
	}

	public Person(String firstname, String lastname, Date birthdate, Address home, Address work) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.birthdate = birthdate;
		setHome(home);
		setWork(work);
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public Address getHome() {
		return home;
	}

	public void setHome(Address home) {
		this.home = home;
		if (home != null) {
			this.home.setPerson(this);
		}
	}

	public Address getWork() {
		return work;
	}

	public void setWork(Address work) {
		this.work = work;
		if (work != null) {
			this.work.setPerson(this);
		}
	}

	public List<Phone> getPhones() {
		return phones;
	}

	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}

	@JSON(include = false)
	public List<String> getHobbies() {
		return hobbies;
	}

	public void setHobbies(List<String> hobbies) {
		this.hobbies = hobbies;
	}

	public Timestamp getFirstBaseBallGame() {
		return firstBaseBallGame;
	}

	public void setFirstBaseBallGame(Timestamp firstBaseBallGame) {
		this.firstBaseBallGame = firstBaseBallGame;
	}
}
