// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.StringTokenizer;

import jodd.bean.BeanUtil;
import jodd.util.StringPool;

/**
 * Populate java bean from ResultSet objects.
 */
public class ResultSetBeanLoader implements BeanLoader {

	public static void loadBean(Object bean, Object rs) {
		if (rs instanceof ResultSet) {
			try {
				ResultSetMetaData rsmd = ((ResultSet)rs).getMetaData();
				int numberOfColumns = rsmd.getColumnCount();
				for (int i = 1; i <= numberOfColumns; i++) {
					StringTokenizer st = new StringTokenizer(rsmd.getColumnName(i), StringPool.UNDERSCORE);
					StringBuilder columnName = new StringBuilder();
					while (st.hasMoreTokens()) {
						String token = st.nextToken().toLowerCase();
						columnName.append(token.substring(0, 1).toUpperCase()).append(token.substring(1));
					}
					BeanUtil.setPropertyForcedSilent(bean, columnName.toString(), ((ResultSet)rs).getObject(i));
				}
			} catch (Exception ex) {
				// ignore
			}
		}
	}

	public void load(Object bean, Object rs) {
		loadBean(bean, rs);
	}

}

