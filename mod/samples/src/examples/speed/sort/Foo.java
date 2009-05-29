// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.speed.sort;

import java.util.Random;

public class Foo implements Comparable{

	public static final int MAX_NAME_SIZE = 100;
	public static final int MIN_NAME_SIZE = 5;
	private static String randomChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static int randomCharsLen = randomChars.length();

	static Random r = new Random();

	public Foo() {
		setId(new Integer(r.nextInt()));
		int size = MIN_NAME_SIZE + r.nextInt(MAX_NAME_SIZE - MIN_NAME_SIZE);
		StringBuilder n = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
            n.append(randomChars.charAt(r.nextInt(randomCharsLen)));
		}
		name = n.toString();
	}

	@Override
	public String toString() {
		return id + " " + name;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	private Integer id;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
		idd = id.intValue();
	}
	int idd;

	/*public int compareTo(Object g) {
		int ii = ((Foo)g).getId().intValue();
		//int idd = id.intValue();
		if (idd < ii) {
			return 1;
		}
		if (idd > ii) {
			return -1;
		}
		return 0;
	}*/
	
	public int compareTo(Object g) {
		return name.compareTo(((Foo)g).getName());
	}

}
