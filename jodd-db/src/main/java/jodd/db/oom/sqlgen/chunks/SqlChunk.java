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

package jodd.db.oom.sqlgen.chunks;

import jodd.db.oom.DbEntityColumnDescriptor;
import jodd.db.oom.DbEntityDescriptor;
import jodd.db.oom.sqlgen.DbSqlBuilderException;
import jodd.db.oom.sqlgen.TemplateData;
import jodd.util.CharUtil;

/**
 * SQL chunk defines part of the SQL query that can be processed.
 */
public abstract class SqlChunk {

	public static final int COLS_NA = 0;                // using explicit reference.
	public static final int COLS_ONLY_EXISTING = 1;     // using only existing columns i.e. that are not-null
	public static final int COLS_ONLY_IDS = 2;          // using only identity columns
	public static final int COLS_ALL = 3;               // using all available columns
	public static final int COLS_ALL_BUT_ID = 4;        // using all available columns except the identity column
	public static final int COLS_NA_MULTI = 0;          // using explicit reference.

	protected final int chunkType;                      // chunk type
	public static final int CHUNK_RAW = -1;
	public static final int CHUNK_SELECT_COLUMNS = 1;
	public static final int CHUNK_TABLE = 2;
	public static final int CHUNK_REFERENCE = 3;
	public static final int CHUNK_MATCH = 4;
	public static final int CHUNK_VALUE = 5;
	public static final int CHUNK_INSERT = 5;
	public static final int CHUNK_UPDATE = 6;


	protected SqlChunk(int chunkType) {
		this.chunkType = chunkType;
	}

	// ---------------------------------------------------------------- linked list

	protected SqlChunk previousChunk;

	/**
	 * Returns previous chunk.
	 */
	public SqlChunk getPreviousChunk() {
		return previousChunk;
	}

	protected SqlChunk nextChunk;

	/**
	 * Returns next chunk.
	 */
	public SqlChunk getNextChunk() {
		return nextChunk;
	}

	/**
	 * Appends chunk to previous one and maintains the double-linked list of the previous chunk.
	 * Current surrounding connections of this chunk will be cut-off.
	 */
	public void insertChunkAfter(SqlChunk previous) {
		SqlChunk next = previous.nextChunk;
		previous.nextChunk = this;
		this.previousChunk = previous;
		if (next != null) {
			next.previousChunk = this;
			this.nextChunk = next;
		}
	}

	/**
	 * Returns <code>true</code> if previous chunk is of provided type.
	 */
	public boolean isPreviousChunkOfType(int type) {
		if (previousChunk == null) {
			return false;
		}
		return previousChunk.chunkType == type;
	}

	/**
	 * Returns <code>true</code> if previous chunk is of the same type.
	 */
	public boolean isPreviousChunkOfSameType() {
		if (previousChunk == null) {
			return false;
		}
		return previousChunk.chunkType == chunkType;
	}

	/**
	 * Returns <code>true</code> if previous chunk is not raw.
	 */
	public boolean isPreviousMacroChunk() {
		if (previousChunk == null) {
			return false;
		}
		return previousChunk.chunkType != CHUNK_RAW;
	}

	public boolean isPreviousRawChunk() {
		if (previousChunk == null) {
			return false;
		}
		return previousChunk.chunkType == CHUNK_RAW;
	}


	// ---------------------------------------------------------------- process

	protected TemplateData templateData;      // working template context

	/**
	 * Initializes chunk. Assigns {@link jodd.db.oom.sqlgen.TemplateData} to chunk.
	 * If chunk needs some pre-processing, they should be done here.
	 */
	public void init(TemplateData templateData) {
		this.templateData = templateData;
	}

	/**
	 * Process the chunk and appends data to the output.
	 */
	public abstract void process(StringBuilder out);


	// ---------------------------------------------------------------- lookup

	/**
	 * Lookups for entity name and throws exception if entity name not found.
	 */
	protected DbEntityDescriptor lookupName(String entityName) {
		DbEntityDescriptor ded = templateData.getDbOomManager().lookupName(entityName);
		if (ded == null) {
			throw new DbSqlBuilderException("Entity name not registered: " + entityName);
		}
		return ded;
	}

	/**
	 * Lookups for entity name and throws an exception if entity type is invalid.
	 */
	protected DbEntityDescriptor lookupType(Class entity) {
		DbEntityDescriptor ded = templateData.getDbOomManager().lookupType(entity);
		if (ded == null) {
			throw new DbSqlBuilderException("Invalid or not-persistent entity: " + entity.getName());
		}
		return ded;
	}

	/**
	 * Lookups for table reference and throws an exception if table reference not found.
	 */
	protected DbEntityDescriptor lookupTableRef(String tableRef) {
		return lookupTableRef(tableRef, true);
	}

	/**
	 * Lookups for table reference and optionally throws an exception if table reference not found.
	 */
	protected DbEntityDescriptor lookupTableRef(String tableRef, boolean throwExceptionIfNotFound) {
		DbEntityDescriptor ded = templateData.getTableDescriptor(tableRef);
		if (ded == null) {
			if (throwExceptionIfNotFound) {
				throw new DbSqlBuilderException("Invalid table reference: " + tableRef);
			}
		}
		return ded;
	}

	/**
	 * Finds a table that contains given column.
	 */
	protected DbEntityDescriptor findColumnRef(String columnRef) {
		DbEntityDescriptor ded = templateData.findTableDescriptorByColumnRef(columnRef);
		if (ded == null) {
			throw new DbSqlBuilderException("Invalid column reference: " + columnRef);
		}
		return ded;
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Resolves table name or alias that will be used in the query.
	 */
	protected String resolveTable(String tableRef, DbEntityDescriptor ded) {
		String tableAlias = templateData.getTableAlias(tableRef);
		if (tableAlias != null) {
			return tableAlias;
		}
		return ded.getTableName();
	}

	/**
	 * Defines parameter with name and its value.
	 */
	protected void defineParameter(StringBuilder query, String name, Object value, DbEntityColumnDescriptor dec) {
		if (name == null) {
			name = templateData.getNextParameterName();
		}
		query.append(':').append(name);
		templateData.addParameter(name, value, dec);
	}

	/**
	 * Resolves object to a class.
	 */
	protected static Class resolveClass(Object object) {
		Class type = object.getClass();
		return type == Class.class ? (Class) object : type;
	}

	// ---------------------------------------------------------------- separation

	/**
	 * Appends missing space if the output doesn't end with whitespace.
	 */
	protected void appendMissingSpace(StringBuilder out) {
		int len = out.length();
		if (len == 0) {
			return;
		}
		len--;
		if (!CharUtil.isWhitespace(out.charAt(len))) {
			out.append(' ');
		}
	}

	/**
	 * Separates from previous chunk by comma if is of the same type.
	 */
	protected void separateByCommaOrSpace(StringBuilder out) {
		if (isPreviousChunkOfSameType()) {
			out.append(',').append(' ');
		} else {
			appendMissingSpace(out);
		}
	}

}