// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.sqlgen;

import jodd.db.DbSqlException;
import jodd.db.orm.ColumnData;
import jodd.db.orm.DbEntityDescriptor;
import jodd.db.orm.DbOrmManager;
import jodd.db.orm.DbSqlGenerator;
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
	protected final DbEntityDescriptor descriptor;

	public DbEntitySearcher(Object entity) {
		this.entity = entity;
		this.dbOrmManager = DbOrmManager.getInstance();
		this.descriptor = dbOrmManager.lookupType(entity.getClass());
		if (descriptor == null) {
			throw new DbSqlException("Type '" + entity.getClass() + "' is not an database entity.");
		}
		entityClassDescriptor = ClassIntrospector.lookup(entity.getClass());
	}

	protected Map<String, Object> queryParameters = new HashMap<String, Object>();

	/**
	 * {@inheritDoc}
	 */
	public String generateQuery() {
		StringBuilder query = new StringBuilder("select * from ");
		query.append(descriptor.getTableName());

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
		String columnName = descriptor.getColumnName(field.getName());
		if (value instanceof String) {
			query.append(columnName).append(" like :").append(columnName);
			queryParameters.put(columnName, '%' + ((String) value) + '%');
		} else if (value instanceof Collection) {
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
				queryParameters.put(name, value);
				c++;
			}
			query.append(')');
		} else {
			query.append(columnName).append("=:").append(columnName);
			queryParameters.put(columnName, value);
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> getQueryParameters() {
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

