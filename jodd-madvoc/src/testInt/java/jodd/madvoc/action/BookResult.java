// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.meta.RenderWith;

/**
 * Just a wrapper over Book, since we may not put UI
 * annotation on it.
 */
@RenderWith(BookActionResult.class)
public class BookResult extends Book {
}