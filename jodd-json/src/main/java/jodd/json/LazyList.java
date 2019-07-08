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

package jodd.json;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;

/**
 * Similar to {@link LazyMap}, the list elements are resolved only when required.
 */
public class LazyList extends AbstractList<Object> implements List<Object> {

	private final List<Object> list = new ArrayList<>(5);
	private boolean converted = false;

	@Override
	public Object get(final int index) {
		Object obj = list.get(index);

		if (obj instanceof Supplier) {
			obj = ((Supplier)obj).get();
			list.set(index, obj);
		}
		return obj;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public Iterator<Object> iterator() {
		convertAllIfNeeded();
		return list.iterator();
	}

	private void convertAllIfNeeded() {
		if (!converted) {
			converted = true;
			for (int index = 0; index < list.size(); index++) {
				this.get(index);
			}
		}
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public boolean add(final Object obj) {
		return list.add(obj);
	}

	@Override
	public Object set(final int index, final Object element) {
		return list.set(index, element);
	}

	@Override
	public ListIterator<Object> listIterator() {
		convertAllIfNeeded();
		return list.listIterator();
	}

	public List<Object> list() {
		return this.list;
	}
}