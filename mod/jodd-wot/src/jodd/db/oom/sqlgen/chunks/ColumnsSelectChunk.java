// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.sqlgen.chunks;

import jodd.db.oom.DbEntityDescriptor;
import jodd.db.oom.DbEntityColumnDescriptor;
import jodd.db.oom.sqlgen.DbSqlBuilderException;
import jodd.db.oom.sqlgen.TemplateData;
import jodd.util.StringPool;

/**
 * Columns select chunk resolves entity column(s) from column references. Should be used for SELECT queries.
 * <p>
 * Column reference is specified as: <code>{@link TableChunk tableReference}.propertyName</code> where property name is
 * a property of the entity references by table reference. Result is rendered as:
 * <code>tableName.column</code> or <code>alias.column</code> (if table has an alias).
 * <p>
 * There are some special values for propertyName
 * <li>wildcard (*), all table columns will be listed
 * <li>id sign (+), all table id columns will be listed
 * <p>
 * If previous chunk is also a column chunk, comma separator will be added in between.
 * <p>
 * Note that column alias are appended to the column name ('as' construct).
 * <p>
 * Macro rules:
 * <li><code>$C{tableRef}</code> is rendered as FOO.col1, FOO.col2,...
 * <li><code>$C{tableRef.*}</code> is equal to above, renders all entity columns
 * <li><code>$C{tableRef.+}</code> renders to only identity columns
 * <li><code>$C{tableRef.colRef}</code> is rendered as FOO.column
 */
public class ColumnsSelectChunk extends SqlChunk {

	private static final String AS = " as ";

	protected final String tableRef;
	protected final String columnRef;
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
				this.tableRef = tref.substring(dotNdx + 1);
				this.hint = tref;
			}

			// column
			if (reference.equals(StringPool.STAR)) {
				this.columnRef = null;
				this.includeColumns = COLS_ALL;
			} else if (reference.equals(StringPool.PLUS)) {
				this.columnRef = null;
				this.includeColumns = COLS_ONLY_IDS;
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
			templateData.hintCount++;
		}
	}

	@Override
	public void process(StringBuilder out) {
		// hints
		if (templateData.hintCount > 0) {
			templateData.registerHint(hint == null ? tableRef : hint);
		}

		// columns
		separateByCommaOrSpace(out);
		DbEntityDescriptor ded = lookupTableRef(tableRef);
		if (columnRef == null) {
			DbEntityColumnDescriptor[] decList = ded.getColumnDescriptors();
			int count = 0;
			for (DbEntityColumnDescriptor dec : decList) {
				if ((includeColumns == COLS_ONLY_IDS) && (dec.isId() == false)) {
					continue;
				}
				if (count > 0) {
					out.append(',').append(' ');
				}
				templateData.lastColumnDec = dec;
				appendColumnName(out, ded, dec.getColumnName());
				count++;
			}
		} else {
			DbEntityColumnDescriptor dec = ded.findByPropertyName(columnRef);
			templateData.lastColumnDec = dec;
			String columnName = dec == null ? null : dec.getColumnName();
			//String columnName = ded.getColumnName(columnRef);
			if (columnName == null) {
				throw new DbSqlBuilderException("Unable to resolve column reference: '" + tableRef + '.' + columnRef + "'.");
			}
			appendColumnName(out, ded, columnName);
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
					query.append(tableName).append(templateData.getDbOrmManager().getColumnAliasSeparator()).append(column);
					break;
				case TABLE_REFERENCE:
					templateData.registerColumnDataForTableRef(tableRef, tableName);
					query.append(tableRef).append(templateData.getDbOrmManager().getColumnAliasSeparator()).append(column);
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
