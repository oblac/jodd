// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.mapper;

import java.sql.ResultSet;

/**
 * ResultSet mapper which implementations parse objects from one result set row.
 * There are two ways of mapping. The basic way is mapping against provided
 * entity types. The second, extended, way is auto-mapping, where no types
 * are provided. Instead, they are mapped by {@link jodd.db.orm.DbOrmManager} or
 * similar external class.
 * <p>
 * This interface also specifies some simple and most used ResultSet wrapper methods.
 */
public interface ResultSetMapper {

	// ---------------------------------------------------------------- moving

	/**
	 * Moves the cursor down one row from its current position.
	 */
	boolean next();

	/**
	 * Releases this ResultSet object's database and JDBC resources immediately instead of
	 * waiting for this to happen when it is automatically closed.
	 */
	void close();

	/**
	 * Return JDBC result set.
	 */
	ResultSet getResultSet();


	// ---------------------------------------------------------------- parse types

	/**
	 * Resolves table names into the list of entity types.
	 * Resolving is used when query is executed without specified types.
	 */
	Class[] resolveTables();

	/**
	 * Parse objects from one result set row to specified types.
	 */
	Object[] parseObjects(Class... types);

	/**
	 * Parse single object from result set row to specified type.
	 * @see #parseObjects(Class[]) 
	 */
	Object parseOneObject(Class... types);

}
