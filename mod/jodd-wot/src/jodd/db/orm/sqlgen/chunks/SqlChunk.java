// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.sqlgen.chunks;

import jodd.db.orm.DbEntityDescriptor;
import jodd.db.orm.DbEntityColumnDescriptor;
import jodd.db.orm.sqlgen.DbSqlBuilderException;
import jodd.db.orm.sqlgen.TemplateData;
import jodd.util.CharUtil;

/**
 * SQL chunk defines part of the SQL query that can be processed.
 */
public abstract class SqlChunk implements Cloneable {

	public static final int COLS_NA = 0;                // using explicit reference.
	public static final int COLS_ONLY_EXISTING = 1;     // using only existing columns i.e. that are not-null
	public static final int COLS_ONLY_IDS = 2;          // using only identity columns
	public static final int COLS_ALL = 3;               // using all available columns

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
	 * Appends chunk to previous one and maintaince the double-linked list of the previous chunk.
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
	 * Initializes chunk. Assigns {@link jodd.db.orm.sqlgen.TemplateData} to chunk.
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
		DbEntityDescriptor ded = templateData.getDbOrmManager().lookupName(entityName);
		if (ded == null) {
			throw new DbSqlBuilderException("Entity name '" + entityName + "' is not registered with DbOrmManager.");
		}
		return ded;
	}

	/**
	 * Lookups for entity name and throws an exception if entity type is invalid.
	 */
	protected DbEntityDescriptor lookupType(Class entity) {
		DbEntityDescriptor ded = templateData.getDbOrmManager().lookupType(entity);
		if (ded == null) {
			throw new DbSqlBuilderException("Invalid or not-persistent entity type: '" + entity.getName() + "'.");
		}
		return ded;
	}

	/**
	 * Lookups for table reference and throws an exception if table reference not found.
	 */
	protected DbEntityDescriptor lookupTableRef(String tableRef) {
		DbEntityDescriptor ded = templateData.getTableDescriptor(tableRef);
		if (ded == null) {
			throw new DbSqlBuilderException("Invalid table reference: '" + tableRef + "', not defined in the query.");
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
		if (CharUtil.isWhitespace(out.charAt(len)) == false) {
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


	// ---------------------------------------------------------------- clone

	/**
	 * Clones all parsed chunk data to an instance that is ready for processing.
	 */
	@Override
	public abstract SqlChunk clone();

}
