// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.sqlgen;

import jodd.db.DbSqlException;
import jodd.db.orm.ColumnData;
import jodd.db.orm.DbEntityDescriptor;
import jodd.db.orm.DbOrmManager;
import jodd.db.orm.DbSqlGenerator;
import jodd.db.orm.DbEntityColumnDescriptor;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Simple entity searcher. It may be applied directly on entity objects, but developers
 * may create so-called search objects - that extends entity objects and adds more fields
 */
public class DbEntitySearcher implements DbSqlGenerator {

	protected final Object entity;
	protected final ClassDescriptor entityClassDescriptor;
	protected final DbOrmManager dbOrmManager;
	protected final DbEntityDescriptor ded;

	public DbEntitySearcher(Object entity) {
		this.entity = entity;
		this.dbOrmManager = DbOrmManager.getInstance();
		this.ded = dbOrmManager.lookupType(entity.getClass());
		if (ded == null) {
			throw new DbSqlException("Type '" + entity.getClass() + "' is not an database entity.");
		}
		entityClassDescriptor = ClassIntrospector.lookup(entity.getClass());
	}

	protected Map<String, ParameterValue> queryParameters = new HashMap<String, ParameterValue>();

	/**
	 * {@inheritDoc}
	 */
	public String generateQuery() {
		StringBuilder query = new StringBuilder("select * from ");
		query.append(ded.getTableName());

		Field[] fields = entityClassDescriptor.getAllFields(true);
		boolean firstCondition = true;
		boolean hasCondition = false;
		for (Field field : fields) {
			Object value;
			try {
				value = field.get(entity);
			} catch (IllegalAccessException iaex) {
				throw new DbSqlException("Unable to read value of property '" + field.getName() + "'.", iaex);
			}
			if (value != null) {
				if (firstCondition) {
					query.append(" where ");
					firstCondition = false;
				}
				if (hasCondition) {
					query.append(" and ");
				}
				hasCondition = forEachField(query, field, value);
			}
		}
		return query.toString();
	}

	/**
	 * Builds condition for single non-null field. By default, all <code>String</code>
	 * values are using <code>like</like> operator. All collections are using <code>in</code>
	 * operator. All other type are using equals.
	 * @return <code>true</code> if condition query is generated, <code>false</code> otherwise.
	 */
	protected boolean forEachField(StringBuilder query, Field field, Object value) {
		String columnName = ded.getColumnName(field.getName());
		DbEntityColumnDescriptor dec = ded.findByColumnName(columnName);
		if (value instanceof String) {
			query.append(columnName).append(" like :").append(columnName);
			queryParameters.put(columnName, new ParameterValue('%' + ((String) value) + '%', dec));
			return true;
		}
		if (value instanceof Collection) {
			Collection collection = (Collection) value;
			if (collection.isEmpty() == true) {
				return false;
			}
			Iterator iterator = collection.iterator();
			query.append(columnName).append(" in (");
			int c = 0;
			while (iterator.hasNext()) {
				value = iterator.next();
				if (c != 0) {
					query.append(',');
				}
				String name = columnName + c;
				query.append(':').append(name);
				queryParameters.put(name, new ParameterValue(value, dec));
				c++;
			}
			query.append(')');
			return true;
		}
		query.append(columnName).append("=:").append(columnName);
		queryParameters.put(columnName, new ParameterValue(value, dec));
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, ParameterValue> getQueryParameters() {
		return queryParameters;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, ColumnData> getColumnData() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getJoinHints() {
		return null;
	}
}

