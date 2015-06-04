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

import jodd.db.oom.ColumnData;
import jodd.db.oom.ColumnAliasType;
import jodd.db.oom.DbSqlGenerator;
import jodd.db.oom.DbOomQuery;
import jodd.db.oom.sqlgen.chunks.ColumnValueChunk;
import jodd.db.oom.sqlgen.chunks.SqlChunk;
import jodd.db.oom.sqlgen.chunks.RawSqlChunk;
import jodd.db.oom.sqlgen.chunks.TableChunk;
import jodd.db.oom.sqlgen.chunks.ColumnsSelectChunk;
import jodd.db.oom.sqlgen.chunks.InsertChunk;
import jodd.db.oom.sqlgen.chunks.ReferenceChunk;
import jodd.db.oom.sqlgen.chunks.ValueChunk;
import jodd.db.oom.sqlgen.chunks.UpdateSetChunk;
import jodd.db.oom.sqlgen.chunks.MatchChunk;
import jodd.db.DbSession;
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
	 * Template constructor.
	 */
	public static DbSqlBuilder sql(String template) {
		return new DbSqlBuilder().append(template);
	}

	/**
	 * Resets the builder so it can be used again.
	 * Object references are not cleared!
	 */
	public DbSqlBuilder reset() {
		resetAll();
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
	public DbSqlBuilder addChunk(SqlChunk chunk) {
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

	public DbSqlBuilder _(SqlChunk chunk) {
		return addChunk(chunk);
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

	public DbSqlBuilder table(Object entity, String alias, String tableReference) {
		return addChunk(new TableChunk(entity, alias, tableReference));
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

	public DbSqlBuilder columnValue(String name, Object value) {
		return addChunk(new ColumnValueChunk(name, value));
	}

	public DbSqlBuilder columnValue(Object value) {
		return addChunk(new ColumnValueChunk(null, value));
	}

	public DbSqlBuilder columnValueRef(String objectReference) {
		return addChunk(new ColumnValueChunk(objectReference));
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
		return addChunk(new UpdateSetChunk(tableRef, values, SqlChunk.COLS_ONLY_EXISTING));
	}

	public DbSqlBuilder setAll(String tableRef, Object values) {
		return addChunk(new UpdateSetChunk(tableRef, values, SqlChunk.COLS_ALL));
	}

	// ---------------------------------------------------------------- query factories

	/**
	 * Returns {@link jodd.db.oom.DbOomQuery} instance for more fluent interface.
	 *
	 */
	public DbOomQuery query() {
		return new DbOomQuery(this);
	}

	public DbOomQuery query(DbSession session) {
		return new DbOomQuery(session, this);
	}

}
