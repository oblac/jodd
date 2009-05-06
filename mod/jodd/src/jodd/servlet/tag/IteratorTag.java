// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import jodd.typeconverter.StringArrayConverter;

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
			iterateCollection((Collection) items, from, pageContext);
		} else if (items.getClass().isArray()) {
			iterateArray((Object[]) items, from, pageContext);
		} else if (items instanceof String) {
			iterateArray(StringArrayConverter.valueOf(items), from, pageContext);
		} else {
			throw new JspException("Provided items are not iterable (neither java.util.Collection, Objects array...).");
		}

		// cleanup
		if (status != null) {
			TagUtil.removeScopeAttribute(status, scope, pageContext);
		}
		TagUtil.removeScopeAttribute(var, scope, pageContext);
	}

	/**
	 * Iterates collection.
	 */
	protected void iterateCollection(Collection collection, int from, PageContext pageContext) throws JspException {
		JspFragment body = getJspBody();
		Iterator iter = collection.iterator();
		int i = 0;
		while (iter.hasNext()) {
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
	protected void iterateArray(Object[] array, int from, PageContext pageContext) throws JspException {
		JspFragment body = getJspBody();
		int len = array.length;
		int last = len - 1;
		for (int i = from; i < len; i++) {
			Object item = array[i];
			if (status != null) {
				iteratorStatus.next(i == last);
			}
			TagUtil.setScopeAttribute(var, item, scope, pageContext);
			TagUtil.invokeBody(body);
		}
	}

}
