// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.sqlgen.chunks;

import jodd.db.oom.DbEntityDescriptor;
import jodd.db.oom.sqlgen.TemplateData;

/**
 * Table chunk resolves table name, optional table alias and defines table references.
 * <p>
 * If previous chunk is also a table, comma separator will be added between two table definitions, otherwise,
 * regular space will be added if needed to separate from previous chunk.
 * <p>
 * Tables <b>must</b> be processed first, <b>before</b> all other chunks processing, since other chunks depends on it.
 */
public class TableChunk extends SqlChunk {

	protected final Class entity;
	protected final String entityName;
	protected final String tableAlias;

	public TableChunk(Object entity) {
		super(CHUNK_TABLE);
		this.entity = resolveClass(entity);
		this.entityName = null;
		this.tableAlias = this.entity.getSimpleName();
	}

	public TableChunk(Object entity, String alias) {
		super(CHUNK_TABLE);
		this.entity = resolveClass(entity);
		this.entityName = null;
		this.tableAlias = alias;
	}

	public TableChunk(String entityName, String alias) {
		this(null, entityName, alias);
	}

	protected TableChunk(Class entity, String entityName, String tableAlias) {
		super(CHUNK_TABLE);
		this.entity = entity;
		this.entityName = entityName;
		this.tableAlias = tableAlias;
	}

	public TableChunk(String tableRef) {
		super(CHUNK_TABLE);
		tableRef = tableRef.trim();
		int spaceNdx = tableRef.indexOf(' ');
		this.entity = null;
		if (spaceNdx != -1) {
			this.entityName = tableRef.substring(0, spaceNdx);
			String alias = tableRef.substring(spaceNdx + 1).trim();
			tableAlias = alias.length() == 0 ? null : alias;
		} else {
			this.entityName = tableRef;
			this.tableAlias = null;
		}
	}

	// ---------------------------------------------------------------- process

	protected DbEntityDescriptor ded;

	/**
	 * Resolves and registers table references.
	 */
	@Override
	public void init(TemplateData templateData) {
		super.init(templateData);
		if (entity != null) {
			ded = lookupType(entity);
		} else {
			Object object = templateData.getObjectReference(entityName);
			if (object != null) {
				ded = lookupType(resolveClass(object));
			} else {
				ded = lookupName(entityName);
			}
		}
		String tableReference = tableAlias;
		if (tableReference == null) {
			tableReference = entityName;
		}
		if (tableReference == null) {
			tableReference = ded.getEntityName();
		}
		templateData.registerTableReference(tableReference, ded, tableAlias);
	}

	@Override
	public void process(StringBuilder out) {
		separateByCommaOrSpace(out);
		out.append(ded.getTableName());
		if (tableAlias != null) {
			out.append(' ').append(tableAlias);
		}
	}

	// ---------------------------------------------------------------- clone

	@Override
	public SqlChunk clone() {
		return new TableChunk(entity,  entityName, tableAlias);
	}
}
