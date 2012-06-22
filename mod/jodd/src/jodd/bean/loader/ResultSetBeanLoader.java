// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.StringTokenizer;

import jodd.util.StringPool;
import jodd.util.StringUtil;

/**
 * Populate java bean from ResultSet objects.
 */
public class ResultSetBeanLoader extends BaseBeanLoader {

	public void load(Object bean, Object source) {
		if (source instanceof ResultSet) {
			try {
				ResultSet resultSet = (ResultSet) source;

				ResultSetMetaData rsmd = resultSet.getMetaData();
				
				int numberOfColumns = rsmd.getColumnCount();
				
				for (int i = 1; i <= numberOfColumns; i++) {
					// todo add parameter for conversion, once when we add a method for different string conversions

					StringTokenizer st = new StringTokenizer(rsmd.getColumnName(i), StringPool.UNDERSCORE);
					StringBuilder columnName = new StringBuilder();
					while (st.hasMoreTokens()) {
						String token = st.nextToken().toLowerCase();
						
						columnName.append(StringUtil.capitalize(token));
					}

					setProperty(bean, columnName.toString(), resultSet.getObject(i));
				}
			} catch (Exception ex) {
				// ignore
			}
		}
	}

}