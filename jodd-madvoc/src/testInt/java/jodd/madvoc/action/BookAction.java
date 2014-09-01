// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.meta.In;
import jodd.madvoc.meta.MadvocAction;

@MadvocAction
public class BookAction {

	@jodd.madvoc.meta.RestAction("${iban}")
	public Book get(@In long iban) {
		// use BookResult to render a book (@RenderWith)
		Book book = new BookResult();

		book.setIban(iban);
		book.setName("Songs of Distant Earth");

		return book;
	}

	@jodd.madvoc.meta.RestAction("${iban}")
	public Book post(@In long iban) {
		Book book = new Book();

		book.setIban(iban);
		book.setName("Songs of Distant Earth");

		// BookActionResult will render a book
		return book;
	}

}