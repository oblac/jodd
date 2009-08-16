// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.mapper;

import jodd.bean.BeanUtil;
import jodd.db.orm.ColumnData;
import jodd.db.orm.DbEntityDescriptor;
import jodd.db.orm.DbOrmManager;
import jodd.db.orm.DbOrmException;
import jodd.db.type.SqlTypeManager;
import jodd.db.type.SqlType;
import jodd.util.ReflectUtil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * Maps all columns of database result set (RS) row to objects.
 * It does it in two steps: preparation (reading table and column names)
 * and parsing (parsing one result set row to resulting objects).
 * <p>
 * <b>Preparation</b><br>
 * Default mapper reads RS column and table names from RS meta-data and external maps, if provided.
 * Since column name is always available in RS meta-data, it may be used to hold table name information.
 * Column names may contain table code separator ({@link jodd.db.orm.DbOrmManager#getColumnAliasSeparator()} that
 * divides column name to table reference and column name. Here, table reference may be either table name or
 * table alias. When it is table alias, external alias-to-name map must be provided.
 * Hence, this defines the table name, and there is no need to read it from RS meta-data.
 * <p>
 * When column name doesn't contain a separator, it may be either an actual column name, or a column code.
 * For column codes, both table and column name is lookup-ed from external map. If column name is an actual column name,
 * table information is read from the RS meta data. Unfortunately, some DBs (such Oracle) doesn't implements
 * this simple JDBC feature. Therefore, it must be expected that column table name is not available.
 * <p>
 * Table name is also not available for columns which are not directly table columns:
 * e.g. some calculations, counts etc.
 * <p>
 * <b>Parsing</b><br>
 * Parser takes types array and tries to populate their instances in best possible way. It assumes that provided
 * types list matches selected columns. That is very important, and yet very easy and natural to follow.
 * So, parser will try to inject columns value into the one result instance. Now, there are two types of instances:
 * simple types (numbers and strings) and entities (pojo objects). Simple types are always mapped to
 * one and only one column. Entities will be mapped to all possible columns that can be matched starting from
 * current column. So, simple types are not column-hungry, entity types are column-hungry:)
 *
 * <p>
 * A column can be injected in one entities property only once. If one column is already mapped to current result,
 * RS mapper will assume that current result is finished with mapping and will proceed to the next one.
 * Similarly, if property name is not found for a column, RS mapper will proceed to the next result.
 * Therefore, entity types are column precise and hungry;) - all listed columns must be mapped somewhere.
 *
 * <p>
 * Results that are not used during parsing will be set to <code>null</code>.
 */
public class DefaultResultSetMapper implements ResultSetMapper {

	protected final DbOrmManager dbOrmManager;
	protected final ResultSet rs;

	protected final int totalColumns;         // total number of columns
	protected final String[] columnNames;     // list of all column names
	protected final String[] tableNames;      // list of table names for each column, table name may be null

	private final Set<String> resultColumns;   // internal columns per entity cache

	// ---------------------------------------------------------------- ctor

	public DefaultResultSetMapper(ResultSet rs, DbOrmManager dbOrmManager) {
		this(rs, null, dbOrmManager);
	}

	/**
	 * Reads RS meta-data for column and table names.
	 */
	public DefaultResultSetMapper(ResultSet rs, Map<String, ColumnData> columnAliases, DbOrmManager ormManager) {
		this.dbOrmManager = ormManager;
		this.rs = rs;
		this.resultColumns = new HashSet<String>();
		try {
			ResultSetMetaData rsMetaData = rs.getMetaData();
			if (rsMetaData == null) {
				throw new DbOrmException("JDBC driver does not provide meta-data.");
			}
			totalColumns = rsMetaData.getColumnCount();
			columnNames = new String[totalColumns];
			tableNames = new String[totalColumns];

			for (int i = 0; i < totalColumns; i++) {
				String columnName = rsMetaData.getColumnName(i + 1);
				String tableName = null;

				// resolve column and table name
				int sepNdx = columnName.indexOf(dbOrmManager.getColumnAliasSeparator());
				if (sepNdx != -1) {
					// column alias exist, result set is ignored and columnAliases contains table data.
					tableName = columnName.substring(0, sepNdx);
					if (columnAliases != null) {
						ColumnData columnData = columnAliases.get(tableName);
						if (columnData != null) {
							tableName = columnData.getTableName();
						}
					}
					columnName = columnName.substring(sepNdx + 1);
				} else {
					// column alias does not exist, table name is read from columnAliases and result set (if available).
					if (columnAliases != null) {
						ColumnData columnData = columnAliases.get(columnName.toLowerCase());
						if (columnData != null) {
							tableName = columnData.getTableName();
							columnName = columnData.getColumnName();
						}
					}
					if (tableName == null) {
						try {
							tableName = rsMetaData.getTableName(i + 1);
						} catch (SQLException sex) {
							// ignore
						}
						if ((tableName != null) && (tableName.length() == 0)) {
							tableName = null;
						}
					}
				}

				columnName = columnName.trim();
				if (columnName.length() == 0) {
					columnName = null;
				}
				if (columnName != null) {
					columnName = columnName.trim().toUpperCase();
				}
				columnNames[i] = columnName;
				if (tableName != null) {
					tableName = tableName.trim().toUpperCase();
				}
				tableNames[i] = tableName;
			}
		} catch (SQLException sex) {
			throw new DbOrmException("Unable to read ResultSet meta-data.", sex);
		}
	}

	// ---------------------------------------------------------------- delegates

	/**
	 * {@inheritDoc}
	 */
	public boolean next() {
		try {
			return rs.next();
		} catch (SQLException sex) {
			throw new DbOrmException("Unable to move ResultSet cursor to next position.", sex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		try {
			rs.close();
		} catch (SQLException sex) {
			// ignore
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ResultSet getResultSet() {
		return rs;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class[] resolveTables() {
		List<Class> classes = new ArrayList<Class>(tableNames.length);
		String lastTableName = null;
		resultColumns.clear();
		for (int i = 0; i < tableNames.length; i++) {
			String tableName = tableNames[i];
			String columnName = columnNames[i];
			if ((tableName.equals(lastTableName) == false) || (resultColumns.contains(columnName) == true)) {
				resultColumns.clear();
				lastTableName = tableName;
				DbEntityDescriptor ded = dbOrmManager.lookupTableName(tableName);
				if (ded == null) {
					throw new DbOrmException("Table name '" + tableName + "' not registered.");
				}
				classes.add(ded.getType());
			}
			resultColumns.add(columnName);
		}
		return classes.toArray(new Class[classes.size()]);
	}

	// ---------------------------------------------------------------- parse objects

	/**
	 * Creates new instances of a types.
	 */
	protected Object newInstance(Class types) {
		try {
			return types.newInstance();
		} catch (Exception ex) {
			throw new DbOrmException("Unable to create new entity instance using default constructor for type '" + types + "'.", ex);
		}
	}

	protected Class[] cachedUsedTypes;
	protected String[] cachedTypesTableNames;

	/**
	 * Creates table names for all specified types.
	 * Since this is usually done once per result set, these names are cached.
	 * Type name will be <code>null</code> for simple names, i.e. for all those
	 * types that returns <code>null</code> when used by {@link jodd.db.orm.DbOrmManager#lookupType(Class)}.
	 */
	protected String[] createTypesTableNames(Class[] types) {
		if (types != cachedUsedTypes) {
			cachedTypesTableNames = new String[types.length];
			for (int i = 0; i < types.length; i++) {
				if (types[i] == null) {
					cachedTypesTableNames[i] = null;
					continue;
				}
				DbEntityDescriptor ded = dbOrmManager.lookupType(types[i]);
				if (ded != null) {
					cachedTypesTableNames[i] = ded.getTableName();
				}
			}
			cachedUsedTypes = types;			
		}
		return cachedTypesTableNames;
	}


	protected int cachedColumnNdx;
	protected Object cachedColumnValue;

	/**
	 * Reads column value from result set. Since this method may be called more then once for
	 * the same column, it caches column values.
	 */
	protected Object readColumnValue(int colNdx, Class destinationType) {
		if (colNdx != cachedColumnNdx) {
			try {
				SqlType sqlType = SqlTypeManager.lookup(destinationType);
				if (sqlType != null) {
					cachedColumnValue = sqlType.get(rs, colNdx + 1);
				} else {
					cachedColumnValue = rs.getObject(colNdx + 1);
					cachedColumnValue = ReflectUtil.castType(cachedColumnValue, destinationType);
				}
			} catch (SQLException sex) {
				throw new DbOrmException("Unable to read value for column #" + (colNdx + 1) + '.');
			}
			cachedColumnNdx = colNdx;
		}
		return cachedColumnValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object[] parseObjects(Class... types) {
		resultColumns.clear();
		int totalTypes = types.length;
		Object[] result = new Object[totalTypes];
		boolean[] resultUsage = new boolean[totalTypes];
		String[] typesTableNames = createTypesTableNames(types);

		int currentResult = 0;
		cachedColumnNdx = -1;
		int colNdx = 0;
		while (colNdx < totalColumns) {

			// no more types for mapping?
			if (currentResult >= totalTypes) {
				break;
			}

			// skip columns that doesn't map
			Class currentType = types[currentResult];
			if (currentType == null) {
				colNdx++;
				currentResult++; resultColumns.clear();
				continue;
			}

			String columnName = columnNames[colNdx];
			String tableName = tableNames[colNdx];
			String resultTableName = typesTableNames[currentResult];			

			if (resultTableName == null) {
				// match: simple type
				result[currentResult] = readColumnValue(colNdx, currentType);
				resultUsage[currentResult] = true;
				colNdx++;
				currentResult++; resultColumns.clear();
				continue;
			}
			if ((tableName == null) || (resultTableName.equals(tableName) == true)) {
				if (resultColumns.contains(columnName) == false) {
					DbEntityDescriptor ded = dbOrmManager.lookupType(currentType);
					String propertyName = ded.getPropertyName(columnName);
					if (propertyName != null) {
						if (result[currentResult] == null) {
							result[currentResult] = newInstance(currentType);
						}
/*
						boolean success = value != null ?
										BeanUtil.setDeclaredPropertySilent(result[currentResult], propertyName, value) :
										BeanUtil.hasDeclaredProperty(result[currentResult], propertyName);
*/
						Class type = BeanUtil.getDeclaredPropertyType(result[currentResult], propertyName);
						if (type != null) {
							// match: entity
							Object value = readColumnValue(colNdx, type);
							if (value != null) {
								BeanUtil.setDeclaredProperty(result[currentResult], propertyName, value);
								resultUsage[currentResult] = true;
							}
							colNdx++;
							resultColumns.add(columnName);
							continue;
						}
					}
				}
			}
			// got to next type, i.e. result
			currentResult++;
			resultColumns.clear();
		}

		resultColumns.clear();
		for (int i = 0; i < resultUsage.length; i++) {
			if (resultUsage[i] == false) {
				result[i] = null;
			}
		}
		return result;
	}


	
	/**
	 * {@inheritDoc}
	 */
	public Object parseOneObject(Class... types) {
		return parseObjects(types)[0];
	}

}
