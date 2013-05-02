// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.bean.BeanUtil;

import java.util.Arrays;
import java.util.List;

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
	 * Compares if new element is equals to existing element at given index.
	 */
	public static <T> boolean equalsToElement(List<T> list, int elementIndex, T newElement) {
		if (elementIndex < 0) {
			return false;
		}

		T existingElement = list.get(elementIndex);

		if (existingElement == null) {
			return newElement != null;
		}

		if (newElement.getClass().isArray()) {
			return Arrays.equals((Object[]) existingElement, (Object[]) newElement);
		} else {
			return existingElement.equals(newElement);
		}
	}


}
