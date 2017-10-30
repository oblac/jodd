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

package jodd.db.oom;

import jodd.bean.BeanUtil;
import jodd.db.JoddDb;
import jodd.util.StringUtil;

/**
 * Some utilities.
 */
public class DbOomUtil {

	/**
	 * Populates entity with generated column values from executed query.
	 */
	public static void populateGeneratedKeys(Object entity, DbOomQuery query) {
		String[] generatedColumns = query.getGeneratedColumnNames();
		if (generatedColumns == null) {
			return;
		}
		DbEntityDescriptor ded = JoddDb.runtime().dbEntityManager().lookupType(entity.getClass());

		// prepare key types
		Class[] keyTypes = new Class[generatedColumns.length];
		String[] properties = new String[generatedColumns.length];
		for (int i = 0; i < generatedColumns.length; i++) {
			String column = generatedColumns[i];
			DbEntityColumnDescriptor decd = ded.findByColumnName(column);
			if (decd != null) {
				keyTypes[i] = decd.getPropertyType();
				properties[i] = decd.getPropertyName();
			}
		}

		Object keyValues = query.findGeneratedColumns(keyTypes);
		if (!keyValues.getClass().isArray()) {
			BeanUtil.declared.setProperty(entity, properties[0], keyValues);
		} else {
			for (int i = 0; i < properties.length; i++) {
				BeanUtil.declared.setProperty(entity, properties[i], ((Object[]) keyValues)[i]);
			}
		}
	}

	/**
	 * Returns initial collections size when <code>max</code>
	 * value is provided.
	 */
	public static int initialCollectionSize(int max) {
		return max > 0 ? max : 10;
	}

	/**
	 * Returns <code>true</code> if a value is considered empty i.e. not existing.
	 */
	public static boolean isEmptyColumnValue(DbEntityColumnDescriptor dec, Object value) {
		if (value == null) {
			return true;
		}

		// special case for ID column
		if (dec.isId() && value instanceof Number) {
			if (((Number) value).intValue() == 0) {
				return true;
			}
		}

		// special case for primitives
		if (dec.getPropertyType().isPrimitive()) {
			int n = ((Number) value).intValue();
			if (n == 0) {
				return true;
			}
		}

		// special case for strings
		if (value instanceof CharSequence) {
			if (StringUtil.isBlank((CharSequence) value)) {
				return true;
			}
		}

		return false;
	}

}