// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.servlet.tag;

import jodd.typeconverter.Converter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.util.Collection;
import java.util.Iterator;

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
	public void setItems(final Object items) {
		this.items = items;
	}

	/**
	 * Specifies variable name that will be used for item during iteration.
	 */
	public void setVar(final String var) {
		this.var = var;
	}

	/**
	 * Specifies status variable name. If omitted, status will not be used.
	 */
	public void setStatus(final String status) {
		this.status = status;
	}

	/**
	 * Specifies modulus value for the iterator status
	 */
	public void setModulus(final int modulus) {
		this.modulus = modulus;
	}

	/**
	 * Sets scope for all variables.
	 */
	public void setScope(final String scope) {
		this.scope = scope;
	}

	/**
	 * Sets starting index.
	 */
	public void setFrom(final int from) {
		this.from = from;
	}

	/**
	 * Sets count as total number of items to iterate.
	 */
	public void setCount(final int count) {
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
			iterateArray(Converter.get().toStringArray(items), from, count, pageContext);
		} else {
			throw new JspException("Provided items are not iterable");
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
	protected int calculateTo(final int from, final int count, final int size) {
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
	protected void iterateCollection(final Collection collection, final int from, final int count, final PageContext pageContext) throws JspException {
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
	protected void iterateArray(final Object[] array, final int from, final int count, final PageContext pageContext) throws JspException {
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
