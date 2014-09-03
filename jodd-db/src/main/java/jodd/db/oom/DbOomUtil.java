// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.bean.BeanUtil;
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
		DbEntityDescriptor ded = query.getManager().lookupType(entity.getClass());

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
		if (keyValues.getClass().isArray() == false) {
			BeanUtil.setDeclaredProperty(entity, properties[0], keyValues);
		} else {
			for (int i = 0; i < properties.length; i++) {
				BeanUtil.setDeclaredProperty(entity, properties[i], ((Object[]) keyValues)[i]);
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