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

package jodd.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Constructs pretty string representation of object value.
 */
public class PrettyStringBuilder {

	protected int maxItemsToShow = 10;
	protected int maxDeep = 3;
	protected int deep;
	protected String nullValue = "<null>";
	protected String moreValue = ",...";

	public int getMaxItemsToShow() {
		return maxItemsToShow;
	}

	/**
	 * Sets the max number of items of arrays, collections and maps to show.
	 */
	public void setMaxItemsToShow(int maxItemsToShow) {
		this.maxItemsToShow = maxItemsToShow;
	}

	public int getMaxDeep() {
		return maxDeep;
	}

	/**
	 * Sets how deep to examine inner objects.
	 */
	public void setMaxDeep(int maxDeep) {
		this.maxDeep = maxDeep;
	}

	public String getNullValue() {
		return nullValue;
	}

	/**
	 * Sets <code>null</code> value representation.
	 */
	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
	}

	public String getMoreValue() {
		return moreValue;
	}

	/**
	 * Sets string for 'more'.
	 */
	public void setMoreValue(String moreValue) {
		this.moreValue = moreValue;
	}

	/**
	 * Returns pretty value from object value.
	 */
	protected String toPrettyString(Object obj) {
		deep++;
		if (obj == null) {
			deep--;
			return nullValue;
		}
		if (deep == maxDeep) {
			deep--;
			return obj.toString();
		}
		StringBuilder s = new StringBuilder();
		Class c = obj.getClass();
		if (c.isArray()) {
			int arrayLen = Array.getLength(obj);
			int len = Math.min(arrayLen, maxItemsToShow);
			s.append('[');
			for (int i = 0; i < len; i++) {
				s.append(toPrettyString(Array.get(obj, i)));
				if (i != len - 1) {
					s.append(',');
				}
			}
			if (len < arrayLen) {
				s.append(moreValue);
			}
			s.append(']');
		} else if (obj instanceof Collection) {
			Collection coll = (Collection) obj;
			int len = Math.min(coll.size(), maxItemsToShow);
			Iterator it = coll.iterator();
			int i = 0;
			s.append('(');
			while ((it.hasNext() && (i < maxItemsToShow))) {
				s.append(toPrettyString(it.next()));
				if (i != len - 1) {
					s.append(',');
				}
				i++;
			}
			if (i < coll.size()) {
				s.append(moreValue);
			}
			s.append(')');
		} else if (obj instanceof Map) {
			Map map = (Map) obj;
			int len = Math.min(map.size(), maxItemsToShow);
			Iterator it = map.keySet().iterator();
			int i = 0;
			s.append('{');
			while ((it.hasNext() && (i < maxItemsToShow))) {
				Object key = it.next();
				s.append(key).append(':');
				s.append(toPrettyString(map.get(key)));
				if (i != len - 1) {
					s.append(',');
				}
				i++;
			}
			if (i < map.size()) {
				s.append(moreValue);
			}
			s.append('}');
		} else {
			s.append(obj.toString());
		}
		deep--;
		return s.toString();
	}

	/**
	 * Returns pretty string representation of the object.
	 */
	public String toString(Object value) {
		return toPrettyString(value);
	}

	/**
	 * Static version for quick access.
	 */
	public static String str(Object value) {
		return new PrettyStringBuilder().toPrettyString(value);
	}

}
