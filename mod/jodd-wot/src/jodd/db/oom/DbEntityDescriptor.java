// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.introspector.ClassIntrospector;
import jodd.util.sort.FastMergeSort;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds all information about some entity type, such as table name and {@link DbEntityColumnDescriptor columns data}.
 */
public class DbEntityDescriptor {

	public DbEntityDescriptor(Class type, String schemaName, String tableNamePrefix, String tableNameSuffix, boolean tableNameUppercase, boolean columnNameUppercase) {
		this.type = type;
		this.entityName = type.getSimpleName();
		this.isAnnotated = DbMetaUtil.resolveIsAnnotated(type);
		this.schemaName = DbMetaUtil.resolveSchemaName(type, schemaName);
		this.tableName = DbMetaUtil.resolveTableName(type, tableNamePrefix, tableNameSuffix, tableNameUppercase);
		this.columnNameUppercase = columnNameUppercase;
		this.tableNameUppercase = tableNameUppercase;
	}

	// ---------------------------------------------------------------- type and table

	private final Class type;
	private final String entityName;
	private final boolean isAnnotated;
	private final String tableName;
	private final String schemaName;
	private final boolean columnNameUppercase;
	private final boolean tableNameUppercase;

	/**
	 * Returns entity type.
	 */
	public Class getType() {
		return type;
	}

	/**
	 * Returns <code>true</code> if type is annotated with {@link jodd.db.oom.meta.DbTable}.
	 */
	public boolean isAnnotated() {
		return isAnnotated;
	}

	/**
	 * Returns table name to which the entity is mapped.
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Returns type name.
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * Returns schema name or <code>null</code> if not available.
	 */
	public String getSchemaName() {
		return schemaName;
	}

	public boolean isColumnNameUppercase() {
		return columnNameUppercase;
	}

	public boolean isTableNameUppercase() {
		return tableNameUppercase;
	}

	// ---------------------------------------------------------------- columns and fields

	private DbEntityColumnDescriptor[] columnDescriptors;
	private DbEntityColumnDescriptor[] idColumnDescriptors;

	/**
	 * Returns the array of column descriptors.
	 */
	public DbEntityColumnDescriptor[] getColumnDescriptors() {
		init();
		return columnDescriptors;
	}

	// ---------------------------------------------------------------- initialization

	/**
	 * Lazy initialization of descriptor.
	 */
	protected void init() {
		if (columnDescriptors == null) {
			resolveColumnsAndProperties(type);
		}
	}

	/**
	 * Resolves list of all columns and properties.
	 */
	private void resolveColumnsAndProperties(Class type) {
		Field[] fields = ClassIntrospector.lookup(type).getAllFields(true);
		List<DbEntityColumnDescriptor> decList = new ArrayList<DbEntityColumnDescriptor>(fields.length);
		int idcount = 0;
		for (Field field : fields) {
			DbEntityColumnDescriptor dec = DbMetaUtil.resolveColumnDescriptors(this, field, isAnnotated, columnNameUppercase);
			if (dec != null) {
				decList.add(dec);
				if (dec.isId) {
					idcount++;
				}
			}
		}
		if (decList.isEmpty()) {
			throw new DbOomException("Entity '" + type + "' doesn't have any column mappings.");
		}
		columnDescriptors = decList.toArray(new DbEntityColumnDescriptor[decList.size()]);
		FastMergeSort.doSort(columnDescriptors);

		// extract ids from sorted list
		if (idcount > 0) {
			idColumnDescriptors = new DbEntityColumnDescriptor[idcount];
			idcount = 0;
			for (DbEntityColumnDescriptor dec : columnDescriptors) {
				if (dec.isId) {
					idColumnDescriptors[idcount++] = dec;
				}
			}
		}
	}

	// ---------------------------------------------------------------- finders

	/**
	 * Finds column descriptor by column name.
	 */
	public DbEntityColumnDescriptor findByColumnName(String columnName) {
		if (columnName == null) {
			return null;
		}
		init();
		for (DbEntityColumnDescriptor columnDescriptor : columnDescriptors) {
			if (columnDescriptor.columnName.equals(columnName) == true) {
				return columnDescriptor;
			}
		}
		return null;
	}
	/**
	 * Finds column descriptor by property name.
	 */
	public DbEntityColumnDescriptor findByPropertyName(String propertyName) {
		if (propertyName == null) {
			return null;
		}
		init();
		for (DbEntityColumnDescriptor columnDescriptor : columnDescriptors) {
			if (columnDescriptor.propertyName.equals(propertyName) == true) {
				return columnDescriptor;
			}
		}
		return null;
	}


	/**
	 * Returns property name for specified column name.
	 */
	public String getPropertyName(String columnName) {
		DbEntityColumnDescriptor dec = findByColumnName(columnName);
		return dec == null ? null : dec.propertyName;
	}

	/**
	 * Returns column name for specified property name..
	 */
	public String getColumnName(String propertyName) {
		DbEntityColumnDescriptor dec = findByPropertyName(propertyName);
		return dec == null ? null : dec.columnName;
	}

	// ---------------------------------------------------------------- column work

	/**
	 * Returns total number of columns.
	 */
	public int getColumnsCount() {
		init();
		return columnDescriptors.length;
	}

	/**
	 * Returns total number of identity columns.
	 */
	public int getIdColumnsCount() {
		init();
		return idColumnDescriptors == null ? 0 : idColumnDescriptors.length;
	}

	private void ensureSingleIdColumn() {
		init();
		if (idColumnDescriptors == null) {
			throw new DbOomException("Entity '" + entityName + "' has no identity column.");
		} else if (idColumnDescriptors.length > 1) {
			throw new DbOomException("Entity '" + entityName + "' has more then one (" + idColumnDescriptors.length + ") identity columns.");
		}

	}

	/**
	 * Returns the identity column name of column marked as identity.
	 * Throws an exception if table has composite primary key.
	 */
	public String getIdColumnName() {
		ensureSingleIdColumn();
		return idColumnDescriptors[0].getColumnName();
	}

	/**
	 * Returns the first property name of column marked as identity.
	 * Throws an exception if table has composite primary key.
	 */
	public String getIdPropertyName() {
		ensureSingleIdColumn();
		return idColumnDescriptors[0].getPropertyName();
	}

}
