// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import jodd.typeconverter.Convert;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.util.Iterator;
import java.util.Collection;

/**
 * Iterator tag for iterating collections.
 *
 * @see jodd.servlet.tag.IteratorStatus
 */
public class IteratorTag extends SimpleTagSupport {

	protected Object items;
	protected String var;
	protected String status;
	protected int modulus = 2;
	protected String scope;
	protected int from;
	protected int count = -1;

	protected IteratorStatus iteratorStatus;

	/**
	 * Specifies item collection.
	 */
	public void setItems(Object items) {
		this.items = items;
	}

	/**
	 * Specifies variable name that will be used for item during iteration.
	 */
	public void setVar(String var) {
		this.var = var;
	}

	/**
	 * Specifies status variable name. If omitted, status will not be used.
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Specifies modulus value for the iterator status
	 */
	public void setModulus(int modulus) {
		this.modulus = modulus;
	}

	/**
	 * Sets scope for all variables.
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * Sets starting index.
	 */
	public void setFrom(int from) {
		this.from = from;
	}

	/**
	 * Sets count as total number of items to iterate.
	 */
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public void doTag() throws JspException {
		if (items == null) {
			return;
		}
		JspFragment body = getJspBody();
		if (body == null) {
			return;
		}
		PageContext pageContext = (PageContext) getJspContext();

		// create an iterator status if the status attribute was set.
		if (status != null) {
			iteratorStatus = new IteratorStatus(this.modulus);
			TagUtil.setScopeAttribute(status, iteratorStatus, scope, pageContext);
		}

		if (items instanceof Collection) {
			iterateCollection((Collection) items, from, count, pageContext);
		} else if (items.getClass().isArray()) {
			iterateArray((Object[]) items, from, count, pageContext);
		} else if (items instanceof String) {
			iterateArray(Convert.toStringArray(items), from, count, pageContext);
		} else {
			throw new JspException("Provided items are not iterable (neither Collection, Objects array...).");
		}

		// cleanup
		if (status != null) {
			TagUtil.removeScopeAttribute(status, scope, pageContext);
		}
		TagUtil.removeScopeAttribute(var, scope, pageContext);
	}

	/**
	 * Calculates 'TO'.
	 */
	protected int calculateTo(int from, int count, int size) {
		int to = size;
		if (count != -1) {
			to = from + count;
			if (to > size) {
				to = size;
			}
		}
		return to;
	}

	/**
	 * Iterates collection.
	 */
	protected void iterateCollection(Collection collection, int from, int count, PageContext pageContext) throws JspException {
		JspFragment body = getJspBody();
		Iterator iter = collection.iterator();
		int i = 0;
		int to = calculateTo(from, count, collection.size());
		while (i < to) {
			Object item = iter.next();
			if (i >= from) {
				if (status != null) {
					iteratorStatus.next(!iter.hasNext());
				}
				TagUtil.setScopeAttribute(var, item, scope, pageContext);
				TagUtil.invokeBody(body);
			}
			i++;
		}
	}

	/**
	 * Iterates arrays.
	 */
	protected void iterateArray(Object[] array, int from, int count, PageContext pageContext) throws JspException {
		JspFragment body = getJspBody();
		int len = array.length;
		int to = calculateTo(from, count, len);
		int last = to - 1;
		for (int i = from; i < to; i++) {
			Object item = array[i];
			if (status != null) {
				iteratorStatus.next(i == last);
			}
			TagUtil.setScopeAttribute(var, item, scope, pageContext);
			TagUtil.invokeBody(body);
		}
	}

}
