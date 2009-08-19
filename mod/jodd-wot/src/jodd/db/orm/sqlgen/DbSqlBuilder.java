// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.sqlgen;

import jodd.db.orm.ColumnData;
import jodd.db.orm.ColumnAliasType;
import jodd.db.orm.DbSqlGenerator;
import jodd.db.orm.DbOrmQuery;
import jodd.db.orm.sqlgen.chunks.SqlChunk;
import jodd.db.orm.sqlgen.chunks.RawSqlChunk;
import jodd.db.orm.sqlgen.chunks.TableChunk;
import jodd.db.orm.sqlgen.chunks.ColumnsSelectChunk;
import jodd.db.orm.sqlgen.chunks.InsertChunk;
import jodd.db.orm.sqlgen.chunks.ReferenceChunk;
import jodd.db.orm.sqlgen.chunks.ValueChunk;
import jodd.db.orm.sqlgen.chunks.UpdateSetChunk;
import jodd.db.orm.sqlgen.chunks.MatchChunk;
import jodd.db.DbSession;
import jodd.util.StringPool;
import jodd.cache.Cache;
import jodd.cache.LRUCache;

import java.util.Map;

/**
 * Nice SQL query generator that provides some automatic query generation.
 * <p>
 * Query is built by appending 'chunks' - parts of the query. These chunks may be
 * very simple, as a simple string. However, the main reason of existence of this class are
 * chunks that auto-generate part of the query based on provided domain object. They can be
 * used to easily create some most common queries in an efficient way.
 * <p>
 * Although it is not necessary, use of {@link jodd.db.orm.meta.DbId} annotation boost the functionality
 * of this query builder.
 * <p>
 * Some chunks deals with relations between tables, so they must be aware of foreign key names. Here the
 * naming convention is used, and even users might have their own foreign key naming convention. 
 * <p>
 * Furthermore, if all queries are generated using just sql builder, it is possible to use dialects for various
 * database types.

 */
public class DbSqlBuilder extends TemplateData implements DbSqlGenerator {

	/**
	 * Creates new SQL builder.
	 */
	public DbSqlBuilder() {
		super();
	}

	public DbSqlBuilder(String template) {
		super();
		append(template);
	}

	public static DbSqlBuilder sql() {
		return new DbSqlBuilder();
	}

	/**
	 * Template constructor that uses cache.
	 */
	public static DbSqlBuilder sql(String template) {
		if (cache == null) {
			return new DbSqlBuilder().append(template);
		}
		SqlChunk cachedChunk = cache.get(template);
		if (cachedChunk == null) {
			DbSqlBuilder dbsql = new DbSqlBuilder().append(template);
			if (dbsql.totalChunks >= cacheThreshold) {
				cachedChunk = cloneAllChunks(dbsql.firstChunk);
				cache.put(template, cachedChunk);
			}
			return dbsql;
		}
		DbSqlBuilder dbsql = new DbSqlBuilder();
		SqlChunk cloned = cloneAllChunks(cachedChunk);
		dbsql.firstChunk = cloned;
		while (cloned != null) {
			dbsql.lastChunk = cloned;
			cloned = cloned.getNextChunk();
		}
		return dbsql;
	}

	/**
	 * Resets the builder so it can be used again.
	 * Object references are not cleared!
	 */
	public DbSqlBuilder reset() {
		resetAll();
		return this;
	}


	// ---------------------------------------------------------------- cache & clone

	protected static Cache<String, SqlChunk> cache = new LRUCache<String, SqlChunk>(100);

	protected static int cacheThreshold = 3;

	/**
	 * Sets the minimal number of sql chunks that query must contains so to be cached. 
	 */
	public static void setCacheThreshold(int ct) {
		if (ct < 1) {
			throw new DbSqlBuilderException("Cache threshold can't be less then 1 (" + ct + ").");
		}
		cacheThreshold = ct;
	}

	/**
	 * Sets new cache size. Zero or negative value turns the cache off. 
	 */
	public static void setCacheSize(int size) {
		if (size <= 0) {
			cache = null;
		} else {
			cache = new LRUCache<String, SqlChunk>(size);
		}
	}


	/**
	 * Clones all chunks.
	 */
	protected static SqlChunk cloneAllChunks(SqlChunk chunk) {
		if (chunk == null) {
			return null;
		}
		SqlChunk first = chunk.clone();
		SqlChunk previous = first;
		chunk = chunk.getNextChunk();
		while (chunk != null) {
			SqlChunk cloned = chunk.clone();
			cloned.insertChunkAfter(previous);
			previous = cloned;
			chunk = chunk.getNextChunk();
		}
		return first;
	}

	// ---------------------------------------------------------------- settings

	/**
	 * Specifies column alias type. May be <code>null</code> when column aliases are not used.
	 */
	public DbSqlBuilder aliasColumnsAs(ColumnAliasType aliasesType) {
		this.columnAliasType = aliasesType;
		return this;
	}

	/**
	 * Defines object reference and an object.
	 */
	public DbSqlBuilder use(String name, Object value) {
		setObjectReference(name, value);
		return this;
	}


	// ---------------------------------------------------------------- chunks

	protected SqlChunk firstChunk;
	protected SqlChunk lastChunk;
	protected int totalChunks;
	protected static final TemplateParser templateParser = new TemplateParser();

	/**
	 * Appends chunk to the list. Chunks <b>must</b> be added using this method.
	 */
	protected DbSqlBuilder addChunk(SqlChunk chunk) {
		if (lastChunk == null) {
			lastChunk = firstChunk = chunk;
		} else {
			chunk.insertChunkAfter(lastChunk);
			lastChunk = chunk;
		}
		totalChunks++;
		return this;
	}

	// ---------------------------------------------------------------- template

	/**
	 * Parses provided text into the list of chunks and appends them to the list.
	 */
	public DbSqlBuilder append(String text) {
		templateParser.parse(this, text);
		return this;
	}

	/**
	 * Simply adds text without parsing to the query.
	 */
	public DbSqlBuilder appendRaw(String text) {
		addChunk(new RawSqlChunk(text));
		return this;
	}

	/**
	 * User-friendly append(String).
	 */
	public DbSqlBuilder _(String text) {
		return append(text);
	}

	/**
	 * Single space shortcut.
	 */
	public DbSqlBuilder _() {
		return appendRaw(StringPool.SPACE);
	}

	// ---------------------------------------------------------------- interface

	/**
	 * {@inheritDoc}
	 */
	public String generateQuery() {

		resetOnPreInit();

		// initialization
		SqlChunk chunk = firstChunk;
		while (chunk != null) {
			chunk.init(this);
			chunk = chunk.getNextChunk();
		} 

		// process
		StringBuilder query = new StringBuilder();
		chunk = firstChunk;
		try {
			while (chunk != null) {
				chunk.process(query);
				chunk = chunk.getNextChunk();
			}
		} catch (DbSqlBuilderException dsbex) {
			dsbex.setQueryString(query.toString());
			throw dsbex;
		}

		return query.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, ColumnData> getColumnData() {
		return columnData;
	}

	public Map<String, ParameterValue> getQueryParameters() {
		return parameters;
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getJoinHints() {
		if (hints == null) {
			return null;
		}
		return hints.toArray(new String[hints.size()]);
	}

	// ---------------------------------------------------------------- table

	public DbSqlBuilder table(String entityName) {
		return addChunk(new TableChunk(entityName));
	}

	public DbSqlBuilder table(String entityName, String alias) {
		return addChunk(new TableChunk(entityName, alias));
	}

	public DbSqlBuilder table(Object entity, String alias) {
		return addChunk(new TableChunk(entity, alias));
	}

	public DbSqlBuilder table(Object entity) {
		return addChunk(new TableChunk(entity));
	}

	// ---------------------------------------------------------------- columns

	public DbSqlBuilder column(String reference) {
		return addChunk(new ColumnsSelectChunk(reference));
	}

	public DbSqlBuilder column(String tableRef, String columnRef) {
		return addChunk(new ColumnsSelectChunk(tableRef, columnRef));
	}

	public DbSqlBuilder columnsAll(String tableRef) {
		return addChunk(new ColumnsSelectChunk(tableRef, true));
	}

	public DbSqlBuilder columnsIds(String tableRef) {
		return addChunk(new ColumnsSelectChunk(tableRef, false));
	}


	// ---------------------------------------------------------------- reference

	public DbSqlBuilder ref(String columnRef) {
		return addChunk(new ReferenceChunk(columnRef));
	}

	public DbSqlBuilder ref(String tableRef, String columnRef) {
		return addChunk(new ReferenceChunk(tableRef, columnRef, false));
	}

	public DbSqlBuilder refId(String tableRef) {
		return addChunk(new ReferenceChunk(tableRef, null, true));
	}


	// ---------------------------------------------------------------- match

	/**
	 * Creates condition part of the query only for existing columns.
	 */
	public DbSqlBuilder match(String tableRef, Object value) {
		return addChunk(new MatchChunk(tableRef, value, SqlChunk.COLS_ONLY_EXISTING));
	}

	public DbSqlBuilder match(String tableRef, String objectRef) {
		return addChunk(new MatchChunk(tableRef, objectRef, SqlChunk.COLS_ONLY_EXISTING));
	}

	/**
	 * Creates condition part of the query for id columns
	 */
	public DbSqlBuilder matchIds(String tableRef, Object value) {
		return addChunk(new MatchChunk(tableRef, value, SqlChunk.COLS_ONLY_IDS));
	}

	public DbSqlBuilder matchIds(String tableRef, String objectRef) {
		return addChunk(new MatchChunk(tableRef, objectRef, SqlChunk.COLS_ONLY_IDS));
	}

	/**
	 * Creates condition part of the query for all columns, including the null values.
	 */
	public DbSqlBuilder matchAll(String tableRef, Object value) {
		return addChunk(new MatchChunk(tableRef, value, SqlChunk.COLS_ALL));
	}

	public DbSqlBuilder matchAll(String tableRef, String objectRef) {
		return addChunk(new MatchChunk(tableRef, objectRef, SqlChunk.COLS_ALL));
	}

	public DbSqlBuilder match(String expression) {
		return addChunk(new MatchChunk(expression));
	}


	// ---------------------------------------------------------------- values

	public DbSqlBuilder value(String name, Object value) {
		return addChunk(new ValueChunk(name, value));
	}

	public DbSqlBuilder value(Object value) {
		return addChunk(new ValueChunk(null, value));
	}

	public DbSqlBuilder valueRef(String objectReference) {
		return addChunk(new ValueChunk(objectReference));
	}

	// ---------------------------------------------------------------- insert

	public DbSqlBuilder insert(String entityName, Object values) {
		return addChunk(new InsertChunk(entityName, values));
	}

	public DbSqlBuilder insert(Class entity, Object values) {
		return addChunk(new InsertChunk(entity, values));
	}

	public DbSqlBuilder insert(Object values) {
		return addChunk(new InsertChunk(values.getClass(), values));
	}

	// ---------------------------------------------------------------- update set

	public DbSqlBuilder set(String tableRef, Object values) {
		return addChunk(new UpdateSetChunk(tableRef, values, false));
	}

	public DbSqlBuilder setAll(String tableRef, Object values) {
		return addChunk(new UpdateSetChunk(tableRef, values, true));
	}

	// ---------------------------------------------------------------- query factories

	/**
	 * Returns {@link jodd.db.orm.DbOrmQuery} instance for more fluent interface.
	 *
	 */
	public DbOrmQuery query() {
		return new DbOrmQuery(this);
	}

	public DbOrmQuery query(DbSession session) {
		return new DbOrmQuery(session, this);
	}

}
