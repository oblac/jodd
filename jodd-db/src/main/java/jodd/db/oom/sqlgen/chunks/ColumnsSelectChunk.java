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

import jodd.db.oom.ColumnAliasType;
import jodd.db.oom.DbEntityColumnDescriptor;
import jodd.db.oom.DbEntityDescriptor;
import jodd.db.oom.DbEntityManager;
import jodd.db.oom.sqlgen.DbSqlBuilderException;
import jodd.db.oom.sqlgen.TemplateData;
import jodd.util.ArraysUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

/**
 * Columns select chunk resolves entity column(s) from column references. Should be used for SELECT queries.
 * <p>
 * Column reference is specified as: <code>{@link TableChunk tableReference}.propertyName</code> where property name is
 * a property of the entity references by table reference. Result is rendered as:
 * <code>tableName.column</code> or <code>alias.column</code> (if table has an alias).
 * <p>
 * There are some special values for propertyName
 * <ul>
 * <li>wildcard (*), all table columns will be listed</li>
 * <li>id sign (+), all table id columns will be listed</li>
 * </ul>
 * <p>
 * If previous chunk is also a column chunk, comma separator will be added in between.
 * <p>
 * Note that column alias are appended to the column name (using 'as' construct).
 * <p>
 * Macro rules:
 * <ul>
 * <li><code>$C{tableRef}</code> is rendered as FOO.col1, FOO.col2,...</li>
 * <li><code>$C{tableRef.*}</code> is equal to above, renders all entity columns</li>
 * <li><code>$C{tableRef.+}</code> renders to only identity columns</li>
 * <li><code>$C{tableRef.%}</code> renders all but identity columns</li>
 * <li><code>$C{tableRef.colRef}</code> is rendered as FOO.column</li>
 * <li><code>$C{tableRef.[colRef1,colRef2|...]}</code> is rendered as FOO.column1, FOO.column2,..., support id sign (+)</li>
 * <li><code>$C{entityRef.colRef}</code> renders to FOO$column</li>
 * <li><code>$C{hint.entityRef...}</code> defines a hint</li>
 * <li><code>$C{hint:entityRef...}</code> defines a hint with custom name</li>
 * <li><code>$C{.columName}</code> renders as column name</li>
 * <li><code>$C{hint:.columName}</code> renders as column name and defines its hint</li>
 * </ul>
 */
public class ColumnsSelectChunk extends SqlChunk {

	private static final String AS = " as ";
	private static final char SPLIT = ',';

	protected final String tableRef;
	protected final String columnRef;
	protected final String[] columnRefArr;
	protected final int includeColumns;
	protected final String hint;
	protected final String columnAliasSeparator;

	private ColumnsSelectChunk(
			final DbEntityManager dbEntityManager,
			final String columnAliasSeparator,
			final String tableRef,
			final String columnRef,
			final String[] columnRefArr,
			final int includeColumns,
			final String hint) {
		super(dbEntityManager, CHUNK_SELECT_COLUMNS);
		this.columnAliasSeparator = columnAliasSeparator;
		this.tableRef = tableRef;
		this.columnRef = columnRef;
		this.columnRefArr = columnRefArr;
		this.includeColumns = includeColumns;
		this.hint = hint;
	}

	public ColumnsSelectChunk(final DbEntityManager dbEntityManager, final String columnAliasSeparator, final String tableRef, final String columnRef) {
		this(dbEntityManager, columnAliasSeparator, tableRef, columnRef, null, COLS_NA, null);
	}
	
	public ColumnsSelectChunk(final DbEntityManager dbEntityManager, final String columnAliasSeparator, final String tableRef, final String... columnRefArr) {
		this(dbEntityManager, columnAliasSeparator, tableRef, null, columnRefArr, COLS_NA_MULTI, null);
	}

	public ColumnsSelectChunk(final DbEntityManager dbEntityManager, final String columnAliasSeparator, final String tableRef, final boolean includeAll) {
		this(dbEntityManager, columnAliasSeparator, tableRef, null, null, includeAll ? COLS_ALL : COLS_ONLY_IDS, null);
	}

	public ColumnsSelectChunk(final DbEntityManager dbEntityManager, final String columnAliasSeparator, String reference) {
		super(dbEntityManager, CHUNK_SELECT_COLUMNS);
		reference = reference.trim();
		int dotNdx = reference.lastIndexOf('.');
		if (dotNdx == -1) {
			this.tableRef = reference;
			this.columnRef = null;
			this.columnRefArr = null;
			this.includeColumns = COLS_ALL;
			this.hint = null;
		} else {

			String tref = reference.substring(0, dotNdx);
			reference = reference.substring(dotNdx + 1);

			// table
			dotNdx = tref.lastIndexOf('.');
			if (dotNdx == -1) {
				this.tableRef = tref;
				this.hint = null;
			} else {
				int doubleColumnNdx = tref.indexOf(':');
				if (doubleColumnNdx == -1) {
					// no special hint
					this.tableRef = tref.substring(dotNdx + 1);
					this.hint = tref;
				} else {
					// hint is different
					this.tableRef = tref.substring(doubleColumnNdx + 1);
					this.hint = tref.substring(0, doubleColumnNdx);
				}
			}

			// column
			if (reference.equals(StringPool.STAR)) {
				this.columnRef = null;
				this.columnRefArr = null;
				this.includeColumns = COLS_ALL;
			} else if (reference.equals(StringPool.PLUS)) {
				this.columnRef = null;
				this.columnRefArr = null;
				this.includeColumns = COLS_ONLY_IDS;
			} else if (reference.equals(StringPool.PERCENT)) {
				this.columnRef = null;
				this.columnRefArr = null;
				this.includeColumns = COLS_ALL_BUT_ID;
			} else if (
					reference.length() != 0
					&& reference.charAt(0) == '['
					&& reference.charAt(reference.length() - 1) == ']') {

				this.columnRef = null;
				this.columnRefArr = StringUtil.splitc(reference.substring(1, reference.length() - 1), SPLIT);
				StringUtil.trimAll(this.columnRefArr);
				this.includeColumns = COLS_NA_MULTI;
			} else {
				this.columnRef = reference;
				this.columnRefArr = null;
				this.includeColumns = COLS_NA;
			}
		}
		this.columnAliasSeparator = columnAliasSeparator;
	}

	// ---------------------------------------------------------------- process


	/**
	 * Counts actual real hints.
	 */
	@Override
	public void init(final TemplateData templateData) {
		super.init(templateData);
		if (hint != null) {
			templateData.incrementHintsCount();
		}
	}

	@Override
	public void process(final StringBuilder out) {
		// hints
		if (templateData.hasHints()) {
			templateData.registerHint(hint == null ? tableRef : hint);
		}

		// columns
		separateByCommaOrSpace(out);

		// special case, only column name, no table ref/name
		if (tableRef.length() == 0) {
			out.append(columnRef);
			return;
		}

		boolean useTableReference = true;
		DbEntityDescriptor ded = lookupTableRef(tableRef, false);
		if (ded == null) {
			useTableReference = false;
			ded = lookupName(tableRef);
		}

		if (columnRef == null) {
			DbEntityColumnDescriptor[] decList = ded.getColumnDescriptors();
			int count = 0;
			boolean withIds = (columnRefArr != null) && ArraysUtil.contains(columnRefArr, StringPool.PLUS);
			for (DbEntityColumnDescriptor dec : decList) {
				if ((includeColumns == COLS_ONLY_IDS) && (!dec.isId())) {
					continue;
				}
				if ((includeColumns == COLS_ALL_BUT_ID) && (dec.isId())) {
					continue;
				}
				if ((includeColumns == COLS_NA_MULTI) 
					&& (!withIds || (!dec.isId()))
					&& (!ArraysUtil.contains(columnRefArr, dec.getPropertyName()))) {
					continue;
				}
				if (count > 0) {
					out.append(',').append(' ');
				}
				templateData.lastColumnDec = dec;

				if (useTableReference) {
					appendColumnName(out, ded, dec);
				} else {
					appendAlias(out, ded, dec);
				}
				count++;
			}
		} else {
			final DbEntityColumnDescriptor dec = ded.findByPropertyName(columnRef);
			if (dec == null) {
				throw new DbSqlBuilderException("Invalid column reference: [" + tableRef + '.' + columnRef + "]");
			}

			templateData.lastColumnDec = dec;
			if (useTableReference) {
				appendColumnName(out, ded, dec);
			} else {
				appendAlias(out, ded, dec);
			}
		}
	}

	/**
	 * Appends alias.
	 */
	protected void appendAlias(final StringBuilder query, final DbEntityDescriptor ded, final DbEntityColumnDescriptor dec) {
		final ColumnAliasType columnAliasType = templateData.getColumnAliasType();

		if (columnAliasType == null || columnAliasType == ColumnAliasType.TABLE_REFERENCE) {
			final String tableName = ded.getTableName();
			final String columnName = dec.getColumnNameForQuery();
			templateData.registerColumnDataForTableRef(tableRef, tableName);
			query.append(tableRef).append(columnAliasSeparator).append(columnName);
		} else
		if (columnAliasType == ColumnAliasType.COLUMN_CODE) {
			final String tableName = ded.getTableName();
			final String columnName = dec.getColumnName();
			final String code = templateData.registerColumnDataForColumnCode(tableName, columnName);
			query.append(code);
		} else
		if (columnAliasType == ColumnAliasType.TABLE_NAME) {
			final String tableName = ded.getTableNameForQuery();
			final String columnName = dec.getColumnNameForQuery();
			query.append(tableName).append(columnAliasSeparator).append(columnName);
		}
	}

	/**
	 * Simply appends column name with optional table reference and alias.
	 */
	protected void appendColumnName(final StringBuilder query, final DbEntityDescriptor ded, final DbEntityColumnDescriptor dec) {
		query.append(resolveTable(tableRef, ded)).append('.').append(dec.getColumnName());
		
		if (templateData.getColumnAliasType() != null) {     // create column aliases

			query.append(AS);

			switch (templateData.getColumnAliasType()) {
				case TABLE_NAME: {
					final String tableName = ded.getTableNameForQuery();
					query.append(tableName).append(columnAliasSeparator).append(dec.getColumnNameForQuery());
					break;
				}
				case TABLE_REFERENCE: {
					final String tableName = ded.getTableName();
					templateData.registerColumnDataForTableRef(tableRef, tableName);
					query.append(tableRef).append(columnAliasSeparator).append(dec.getColumnNameForQuery());
					break;
				}
				case COLUMN_CODE: {
					final String tableName = ded.getTableName();
					final String code = templateData.registerColumnDataForColumnCode(tableName, dec.getColumnName());
					query.append(code);
					break;
				}
			}
		}
	}

}