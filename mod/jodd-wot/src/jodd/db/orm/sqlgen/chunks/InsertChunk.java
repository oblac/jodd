// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.sqlgen.chunks;

import jodd.db.orm.DbEntityDescriptor;
import jodd.db.orm.DbEntityColumnDescriptor;
import jodd.bean.BeanUtil;
import jodd.util.StringUtil;

/**
 * Renders complete INSERT statement. This chunk is a bit different than others since it renders a complete SQL
 * query for inserting.
 */
public class InsertChunk extends SqlChunk {

	protected final String entityName;
	protected final Class entityType;
	protected final Object data;

	public InsertChunk(String entityName, Object data) {
		this(entityName, null, data);
	}

	public InsertChunk(Class entityType, Object data) {
		this(null, entityType, data);
	}

	protected InsertChunk(String entityName, Class entityType, Object data) {
		super(CHUNK_INSERT);
		this.entityName = entityName;
		this.entityType = entityType;
		this.data = data;
	}

	@Override
	public void process(StringBuilder out) {
		DbEntityDescriptor ded = entityName != null ? lookupName(entityName) : lookupType(entityType);
		StringBuilder col = new StringBuilder();
		StringBuilder val = new StringBuilder();

		DbEntityColumnDescriptor[] decList = ded.getColumnDescriptors();
		String typeName = StringUtil.uncapitalize(ded.getEntityName());

		int size = 0;
		for (DbEntityColumnDescriptor dec : decList) {
			String property = dec.getPropertyName();
			Object value = BeanUtil.getDeclaredProperty(data, property);
			if (value == null) {
				continue;
			}
			
			if (size > 0) {
				col.append(',').append(' ');
				val.append(',').append(' ');
			}
			size++;
			col.append(dec.getColumnName());

			String propertyName = typeName + '.' + property;
			defineParameter(val, propertyName, value, dec);
		}

		out.append("insert into ").append(ded.getTableName()).append(" (")
				.append(col).append(") values (").append(val).append(')');
	}

	// ---------------------------------------------------------------- clone

	@Override
	public SqlChunk clone() {
		return new InsertChunk(entityName, entityType, data);
	}
}
