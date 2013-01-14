// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.sqlgen.chunks;

import jodd.db.oom.ColumnAliasType;
import jodd.db.oom.DbEntityDescriptor;
import jodd.db.oom.DbEntityColumnDescriptor;
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
 * <li>id sign (+), all table id columns will be listed</li?
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
 * <li><code>$C{tableRef.colRef}</code> is rendered as FOO.column</li>
 * <li><code>$C{entityRef.colRef}</code> renders to FOO$column</li>
 * <li><code>$C{hint.entityRef...}</code> defines a hint</li>
 * <li><code>$C{hint:entityRef...}</code> defines a hint with custom name</li>
 * <li><code>$C{.columName}</code> renders as column name</li>
 * <li><code>$C{hint:.columName}</code> renders as column name and defines its hint</li>
 * </ul>
 */
public class ColumnsSelectChunk extends SqlChunk {

	private static final String AS = " as ";
	private static final char SPLIT = '|';
	private static final char  LEFT_SQ_BRACKET  = '[';
	private static final char  RIGHT_SQ_BRACKET = ']';

	protected final String tableRef;
	protected final String columnRef;
	protected String[] columnRefArr;
	protected final int includeColumns;
	protected final String hint;

	protected ColumnsSelectChunk(String tableRef, String columnRef, int includeColumns, String hint) {
		super(CHUNK_SELECT_COLUMNS);
		this.tableRef = tableRef;
		this.columnRef = columnRef;
		this.includeColumns = includeColumns;
		this.hint = hint;
	}

	public ColumnsSelectChunk(String tableRef, String columnRef) {
		this(tableRef, columnRef, COLS_NA, null);
	}

	public ColumnsSelectChunk(String tableRef, boolean includeAll) {
		this(tableRef, null, includeAll == true ? COLS_ALL : COLS_ONLY_IDS, null);
	}

	public ColumnsSelectChunk(String reference) {
		super(CHUNK_SELECT_COLUMNS);
		reference = reference.trim();
		int dotNdx = reference.lastIndexOf('.');
		if (dotNdx == -1) {
			this.tableRef = reference;
			this.columnRef = null;
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
				this.includeColumns = COLS_ALL;
			} else if (reference.equals(StringPool.PLUS)) {
				this.columnRef = null;
				this.includeColumns = COLS_ONLY_IDS;
			} else if(!reference.isEmpty() 
				&& reference.charAt(0) == LEFT_SQ_BRACKET
				&&reference.charAt(reference.length()-1) == RIGHT_SQ_BRACKET){
			    	this.columnRef = null;
				this.columnRefArr = StringUtil.splitc(reference.substring(1, reference.length()-1), SPLIT);
				StringUtil.trimAll(this.columnRefArr);
				this.includeColumns = COLS_NA_MULTI;
			} else {
				this.columnRef = reference;
				this.includeColumns = COLS_NA;
			}
		}
	}

	// ---------------------------------------------------------------- process


	/**
	 * Counts actual real hints.
	 */
	@Override
	public void init(TemplateData templateData) {
		super.init(templateData);
		if (hint != null) {
			templateData.incrementHintsCount();
		}
	}

	@Override
	public void process(StringBuilder out) {
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
				if ((includeColumns == COLS_ONLY_IDS) && (dec.isId() == false)) {
					continue;
				}
				if ((includeColumns == COLS_NA_MULTI) 
					&& (!withIds || (dec.isId() == false))
					&& (!ArraysUtil.contains(columnRefArr, dec.getPropertyName()))) {
					continue;
				}
				if (count > 0) {
					out.append(',').append(' ');
				}
				templateData.lastColumnDec = dec;

				if (useTableReference) {
					appendColumnName(out, ded, dec.getColumnName());
				} else {
					appendAlias(out, ded, dec.getColumnName());
				}
				count++;
			}
		} else {
			DbEntityColumnDescriptor dec = ded.findByPropertyName(columnRef);
			templateData.lastColumnDec = dec;
			String columnName = dec == null ? null : dec.getColumnName();
			//String columnName = ded.getColumnName(columnRef);
			if (columnName == null) {
				throw new DbSqlBuilderException("Unable to resolve column reference: " + tableRef + '.' + columnRef);
			}
			if (useTableReference) {
				appendColumnName(out, ded, columnName);
			} else {
				appendAlias(out, ded, columnName);
			}
		}
	}

	/**
	 * Appends alias.
	 */
	protected void appendAlias(StringBuilder query, DbEntityDescriptor ded, String column) {
		String tableName = ded.getTableName();

		ColumnAliasType columnAliasType = templateData.getColumnAliasType();
		String columnAliasSeparator = templateData.getDbOomManager().getColumnAliasSeparator();

		if (columnAliasType == null || columnAliasType == ColumnAliasType.TABLE_REFERENCE) {
			templateData.registerColumnDataForTableRef(tableRef, tableName);
			query.append(tableRef).append(columnAliasSeparator).append(column);
		} else
		if (columnAliasType == ColumnAliasType.COLUMN_CODE) {
			String code = templateData.registerColumnDataForColumnCode(tableName, column);
			query.append(code);
		} else
		if (columnAliasType == ColumnAliasType.TABLE_NAME) {
			query.append(tableName).append(columnAliasSeparator).append(column);
		}
	}

	/**
	 * Simply appends column name with optional table reference and alias.
	 */
	protected void appendColumnName(StringBuilder query, DbEntityDescriptor ded, String column) {
		query.append(resolveTable(tableRef, ded)).append('.').append(column);
		
		if (templateData.getColumnAliasType() != null) {     // create column aliases
			String tableName = ded.getTableName();
			query.append(AS);
			switch (templateData.getColumnAliasType()) {
				case TABLE_NAME:
					query.append(tableName).append(templateData.getDbOomManager().getColumnAliasSeparator()).append(column);
					break;
				case TABLE_REFERENCE:
					templateData.registerColumnDataForTableRef(tableRef, tableName);
					query.append(tableRef).append(templateData.getDbOomManager().getColumnAliasSeparator()).append(column);
					break;
				case COLUMN_CODE:
					String code = templateData.registerColumnDataForColumnCode(tableName, column);
					query.append(code);
					break;
			}
		}
	}

	// ---------------------------------------------------------------- clone

	@Override
	public SqlChunk clone() {
		return new ColumnsSelectChunk(tableRef, columnRef, includeColumns, hint);
	}
}
