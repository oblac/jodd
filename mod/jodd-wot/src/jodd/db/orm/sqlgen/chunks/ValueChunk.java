// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.sqlgen.chunks;

import java.util.Collection;

/**
 * Defines SQL parameter and its value. Name may be <code>null</code> and will be
 * automatically generated. Collections are recognized and will be rendered into
 * the list of values.
 */
public class ValueChunk extends SqlChunk {

	protected final String name;
	protected Object value;
	protected final String objReference;

	public ValueChunk(String name, Object value) {
		this(name, value, null);
	}

	public ValueChunk(String objReference) {
		this(null, null, objReference);
	}

	protected ValueChunk(String name, Object value, String objReference) {
		super(CHUNK_VALUE);
		this.name = name;
		this.value = value;
		this.objReference = objReference;
	}

	@Override
	public void process(StringBuilder out) {
		if (objReference != null) {
			value = templateData.lookupObject(objReference);
		}
		if (value != null) {
			if (value instanceof Collection) {
				Collection collection = (Collection) value;
				int counter = 0;
				for (Object obj : collection) {
					if (counter > 0) {
						out.append(',').append(' ');
					}
					defineParameter(out, name + counter, obj, null);
					counter++;
				}
				return;
			}
		}
		defineParameter(out, name, value, null);
	}

	// ---------------------------------------------------------------- clone

	@Override
	public SqlChunk clone() {
		return new ValueChunk(name, value, objReference);
	}
}
