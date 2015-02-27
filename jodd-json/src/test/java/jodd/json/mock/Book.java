// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.mock;

public class Book extends Periodical {

	private String isbn;

	public Book(String isbn, String name) {
		super(name);
		this.isbn = isbn;
	}

	@Override
	public String getID() {
		return isbn;
	}

	@Override
	public void setID(String id) {
		isbn = id;
	}

	public void setID(Integer id) {
		isbn = Integer.toString(id);
	}

	public boolean isA() {
		return false;
	}

	public void setName(String name) {
	}
}
