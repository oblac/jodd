// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.db.oom.sqlgen;

import jodd.db.DbOom;
import jodd.db.DbSession;
import jodd.db.oom.ColumnAliasType;
import jodd.db.oom.ColumnData;
import jodd.db.oom.DbOomQuery;
import jodd.db.oom.DbSqlGenerator;
import jodd.db.oom.sqlgen.chunks.ColumnValueChunk;
import jodd.db.oom.sqlgen.chunks.ColumnsSelectChunk;
import jodd.db.oom.sqlgen.chunks.InsertChunk;
import jodd.db.oom.sqlgen.chunks.MatchChunk;
import jodd.db.oom.sqlgen.chunks.RawSqlChunk;
import jodd.db.oom.sqlgen.chunks.ReferenceChunk;
import jodd.db.oom.sqlgen.chunks.SqlChunk;
import jodd.db.oom.sqlgen.chunks.TableChunk;
import jodd.db.oom.sqlgen.chunks.UpdateSetChunk;
import jodd.db.oom.sqlgen.chunks.ValueChunk;
import jodd.util.StringPool;

import java.util.Map;

/**
 * Nice SQL query generator that provides some automatic query generation.
 * <p>
 * Query is built by appending 'chunks' - parts of the query. These chunks may be
 * very simple, as a simple string. However, the main reason of existence of this class are
 * chunks that auto-generate part of the query based on provided domain object. They can be
 * used to easily create some most common queries in an efficient way.
 * <p>
 * Although it is not necessary, use of {@link jodd.db.oom.meta.DbId} annotation boost the functionality
 * of this query builder.
 * <p>
 * Some chunks deals with relations between tables, so they must be aware of foreign key names. Here the
 * naming convention is used, and even users might have their own foreign key naming convention. 
 * <p>
 * Furthermore, if all queries are generated using just sql builder, it is possible to use dialects for various
 * database types.
 */
public class DbSqlBuilder extends TemplateData implements DbSqlGenerator {

	private final DbOom dbOom;

	/**
	 * Creates new SQL builder.
	 */
	public DbSqlBuilder() {
		this(DbOom.get());
	}

	public DbSqlBuilder(final String template) {
		this(DbOom.get(), template);
	}

	public DbSqlBuilder(final DbOom dbOom) {
		super(dbOom);
		this.dbOom = dbOom;
	}

	public DbSqlBuilder(final DbOom dbOom, final String template) {
		super(dbOom);
		this.dbOom = dbOom;
		append(template);
	}

	/**
	 * Template static constructor.
	 */
	public static DbSqlBuilder sql() {
		return new DbSqlBuilder();
	}

	/**
	 * Template static constructor.
	 */
	public static DbSqlBuilder sql(final String template) {
		return new DbSqlBuilder().append(template);
	}

	/**
	 * Resets the builder (soft reset), so it can be used again.
	 * Configuration is preserved.
	 * @see TemplateData#resetSoft()
	 */
	public DbSqlBuilder reset() {
		resetSoft();
		return this;
	}

	/**
	 * Hard reset of the builder, all configuration is reset.
	 * @see TemplateData#resetHard()
	 */
	public DbSqlBuilder resetAll() {
		resetHard();
		return this;
	}

	/**
	 * Builds the query and returns parsed data.
	 * Returned value can be cached or stored as a constant value
	 * to prevent further parsing of the same code.
	 */
	public ParsedSql parse() {
		return new ParsedSql(this);
	}


	// ---------------------------------------------------------------- settings

	/**
	 * Specifies column alias type. May be <code>null</code> when column aliases are not used.
	 */
	public DbSqlBuilder aliasColumnsAs(final ColumnAliasType aliasesType) {
		this.columnAliasType = aliasesType;
		return this;
	}

	/**
	 * Defines object reference and an object.
	 */
	public DbSqlBuilder use(final String name, final Object value) {
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
	public DbSqlBuilder addChunk(final SqlChunk chunk) {
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
	public DbSqlBuilder append(final String text) {
		templateParser.parse(this, text);
		return this;
	}

	/**
	 * Simply adds text without parsing to the query.
	 */
	public DbSqlBuilder appendRaw(final String text) {
		addChunk(new RawSqlChunk(entityManager, text));
		return this;
	}

	/**
	 * User-friendly {@link #append(String)}.
	 */
	public DbSqlBuilder $(final String text) {
		return append(text);
	}

	/**
	 * Single space shortcut.
	 */
	public DbSqlBuilder $() {
		return appendRaw(StringPool.SPACE);
	}

	public DbSqlBuilder $(final SqlChunk chunk) {
		return addChunk(chunk);
	}

	// ---------------------------------------------------------------- interface

	protected String generatedQuery;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateQuery() {
		reset();

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

		generatedQuery = query.toString();

		return generatedQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, ColumnData> getColumnData() {
		return columnData;
	}

	@Override
	public Map<String, ParameterValue> getQueryParameters() {
		return parameters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getJoinHints() {
		if (hints == null) {
			return null;
		}
		return hints.toArray(new String[0]);
	}

	// ---------------------------------------------------------------- table

	public DbSqlBuilder table(final String entityName) {
		return addChunk(new TableChunk(entityManager, entityName));
	}

	public DbSqlBuilder table(final String entityName, final String alias) {
		return addChunk(new TableChunk(entityManager, entityName, alias));
	}

	public DbSqlBuilder table(final Object entity, final String alias) {
		return addChunk(new TableChunk(entityManager, entity, alias));
	}

	public DbSqlBuilder table(final Object entity, final String alias, final String tableReference) {
		return addChunk(new TableChunk(entityManager, entity, alias, tableReference));
	}

	public DbSqlBuilder table(final Object entity) {
		return addChunk(new TableChunk(entityManager, entity));
	}

	// ---------------------------------------------------------------- columns

	public DbSqlBuilder column(final String reference) {
		return addChunk(new ColumnsSelectChunk(entityManager, dbOom.config().getColumnAliasSeparator(), reference));
	}

	public DbSqlBuilder column(final String tableRef, final String columnRef) {
		return addChunk(new ColumnsSelectChunk(entityManager, dbOom.config().getColumnAliasSeparator(), tableRef, columnRef));
	}

	public DbSqlBuilder columnsAll(final String tableRef) {
		return addChunk(new ColumnsSelectChunk(entityManager, dbOom.config().getColumnAliasSeparator(), tableRef, true));
	}

	public DbSqlBuilder columnsIds(final String tableRef) {
		return addChunk(new ColumnsSelectChunk(entityManager, dbOom.config().getColumnAliasSeparator(), tableRef, false));
	}


	// ---------------------------------------------------------------- reference

	public DbSqlBuilder ref(final String columnRef) {
		return addChunk(new ReferenceChunk(entityManager, columnRef));
	}

	public DbSqlBuilder ref(final String tableRef, final String columnRef) {
		return addChunk(new ReferenceChunk(entityManager, tableRef, columnRef, false));
	}

	public DbSqlBuilder refId(final String tableRef) {
		return addChunk(new ReferenceChunk(entityManager, tableRef, null, true));
	}


	// ---------------------------------------------------------------- match

	/**
	 * Creates condition part of the query only for existing columns.
	 */
	public DbSqlBuilder match(final String tableRef, final Object value) {
		return addChunk(new MatchChunk(entityManager, tableRef, value, SqlChunk.COLS_ONLY_EXISTING));
	}

	public DbSqlBuilder match(final String tableRef, final String objectRef) {
		return addChunk(new MatchChunk(entityManager, tableRef, objectRef, SqlChunk.COLS_ONLY_EXISTING));
	}

	/**
	 * Creates condition part of the query for id columns
	 */
	public DbSqlBuilder matchIds(final String tableRef, final Object value) {
		return addChunk(new MatchChunk(entityManager, tableRef, value, SqlChunk.COLS_ONLY_IDS));
	}

	public DbSqlBuilder matchIds(final String tableRef, final String objectRef) {
		return addChunk(new MatchChunk(entityManager, tableRef, objectRef, SqlChunk.COLS_ONLY_IDS));
	}

	/**
	 * Creates condition part of the query for all columns, including the null values.
	 */
	public DbSqlBuilder matchAll(final String tableRef, final Object value) {
		return addChunk(new MatchChunk(entityManager, tableRef, value, SqlChunk.COLS_ALL));
	}

	public DbSqlBuilder matchAll(final String tableRef, final String objectRef) {
		return addChunk(new MatchChunk(entityManager, tableRef, objectRef, SqlChunk.COLS_ALL));
	}

	public DbSqlBuilder match(final String expression) {
		return addChunk(new MatchChunk(entityManager, expression));
	}


	// ---------------------------------------------------------------- values

	public DbSqlBuilder value(final String name, final Object value) {
		return addChunk(new ValueChunk(entityManager, name, value));
	}

	public DbSqlBuilder value(final Object value) {
		return addChunk(new ValueChunk(entityManager, null, value));
	}

	public DbSqlBuilder valueRef(final String objectReference) {
		return addChunk(new ValueChunk(entityManager, objectReference));
	}

	public DbSqlBuilder columnValue(final String name, final Object value) {
		return addChunk(new ColumnValueChunk(entityManager, name, value));
	}

	public DbSqlBuilder columnValue(final Object value) {
		return addChunk(new ColumnValueChunk(entityManager, null, value));
	}

	public DbSqlBuilder columnValueRef(final String objectReference) {
		return addChunk(new ColumnValueChunk(entityManager, objectReference));
	}

	// ---------------------------------------------------------------- insert

	public DbSqlBuilder insert(final String entityName, final Object values) {
		return addChunk(new InsertChunk(entityManager, dbOom.config().isUpdateablePrimaryKey(), entityName, values));
	}

	public DbSqlBuilder insert(final Class entity, final Object values) {
		return addChunk(new InsertChunk(entityManager, dbOom.config().isUpdateablePrimaryKey(), entity, values));
	}

	public DbSqlBuilder insert(final Object values) {
		return addChunk(new InsertChunk(entityManager, dbOom.config().isUpdateablePrimaryKey(), values.getClass(), values));
	}

	// ---------------------------------------------------------------- update set

	public DbSqlBuilder set(final String tableRef, final Object values) {
		return addChunk(new UpdateSetChunk(dbOom, tableRef, values, SqlChunk.COLS_ONLY_EXISTING));
	}

	public DbSqlBuilder setAll(final String tableRef, final Object values) {
		return addChunk(new UpdateSetChunk(dbOom, tableRef, values, SqlChunk.COLS_ALL));
	}

	// ---------------------------------------------------------------- query factories

	/**
	 * Returns {@link jodd.db.oom.DbOomQuery} instance for more fluent interface.
	 */
	public DbOomQuery query() {
		return new DbOomQuery(dbOom, this);
	}

	public DbOomQuery query(final DbSession session) {
		return new DbOomQuery(dbOom, session, this);
	}

}
