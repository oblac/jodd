// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.mock;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Surfer {

	private long id;
	private String name;
	private Double skill;
	private String split;
	private InputStream pipe;
	private List<Phone> phones;

	public static Surfer createSurfer() throws FileNotFoundException {
		Surfer surfer = new Surfer();
		surfer.id = 674;
		surfer.name = "jodd";
		surfer.skill = null;
		surfer.split = "half";
		surfer.pipe = new ByteArrayInputStream("jodd".getBytes());
		surfer.phones = new ArrayList<Phone>();
		surfer.phones.add(new Phone(PhoneNumberType.HOME, "123 456-7894"));
		return surfer;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getSkill() {
		return skill;
	}

	public void setSkill(Double skill) {
		this.skill = skill;
	}

	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
	}

	public List<Phone> getPhones() {
		return phones;
	}

	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}

	public InputStream getPipe() {
		return pipe;
	}

	public void setPipe(InputStream pipe) {
		this.pipe = pipe;
	}
}