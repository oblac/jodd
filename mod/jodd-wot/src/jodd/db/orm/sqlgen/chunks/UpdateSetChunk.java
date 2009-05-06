// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.sqlgen.chunks;

import jodd.db.orm.DbEntityDescriptor;
import jodd.db.orm.DbEntityColumnDescriptor;
import jodd.bean.BeanUtil;
import jodd.util.StringUtil;

/**
 * Generates the SET part of the UPDATE statement.
 * It may contains only non-<code>null</code> values, or all.
 */
public class UpdateSetChunk extends SqlChunk {

	private static final String SET = "set ";
	
	protected final Object data;
	protected final String tableRef;
	protected final boolean includeNulls;

	public UpdateSetChunk(String tableRef, Object data, boolean includeNulls) {
		super(CHUNK_UPDATE);
		this.tableRef = tableRef;
		this.data = data;
		this.includeNulls = includeNulls;
	}

	@Override
	public void process(StringBuilder out) {
		if (isPreviousChunkOfType(CHUNK_TABLE)) {
			appendMissingSpace(out);
		}

		DbEntityDescriptor ded = tableRef != null ?
				lookupTableRef(tableRef) :
				lookupType(resolveClass(data));

		out.append(SET);

		DbEntityColumnDescriptor[] decList = ded.getColumnDescriptors();
		String typeName = StringUtil.uncapitalize(ded.getEntityName());
		String table = resolveTable(tableRef, ded); 

		int size = 0;
		for (DbEntityColumnDescriptor dec : decList) {
			String property = dec.getPropertyName();
			Object value = BeanUtil.getDeclaredProperty(data, property);
			if ((includeNulls == false) && (value == null)) {
				continue;
			}
			if (size > 0) {
				out.append(',').append(' ');
			}
			size++;
			out.append(table).append('.').append(dec.getColumnName()).append('=');
			String propertyName = typeName + '.' + property;
			defineParameter(out, propertyName, value);
		}
		if (size > 0) {
			out.append(' ');
		}
	}

	// ---------------------------------------------------------------- clone

	@Override
	public SqlChunk clone() {
		return new UpdateSetChunk(tableRef, data, includeNulls);
	}
}