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

package jodd.petite;

import jodd.util.PropertiesUtil;
import jodd.util.StringPool;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Parameter storage and resolver.
 */
public class ParamManager {

	protected final Map<String, Object> params;

	public ParamManager() {
		params = new HashMap<>();
	}

	/**
	 * Adds a parameter.
	 */
	public void put(String name, Object value) {
		params.put(name, value);
	}

	/**
	 * Returns parameter for given name or <code>null</code>
	 * if not found.
	 */
	public Object get(String name) {
		return params.get(name);
	}

	/**
	 * Returns an array of param keys that belongs to provided bean.
	 * Optionally resolves the value of returned parameters.
	 */
	public String[] resolve(String beanName, boolean resolveReferenceParams) {
		beanName = beanName + '.';

		List<String> list = new ArrayList<>();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			if (key.startsWith(beanName) == false) {
				continue;
			}
			list.add(key);
			if (resolveReferenceParams == false) {
				continue;
			}
			// resolve all references
			String value = PropertiesUtil.resolveProperty(params, key);
			entry.setValue(value);
		}
		if (list.isEmpty()) {
			return StringPool.EMPTY_ARRAY;
		} else {
			return list.toArray(new String[list.size()]);
		}
	}

}