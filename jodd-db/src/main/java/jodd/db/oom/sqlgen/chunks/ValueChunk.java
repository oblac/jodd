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

import jodd.db.oom.DbEntityManager;

import java.util.Collection;

/**
 * Defines SQL parameter and its value. Name may be <code>null</code> and will be
 * automatically generated. Collections are recognized and will be rendered into
 * the list of values.
 * <p>
 * For the <b>last</b> value use {@link ColumnValueChunk}!
 */
public class ValueChunk extends SqlChunk {

	protected final String name;
	protected Object value;
	protected final String objReference;

	public ValueChunk(final DbEntityManager dbEntityManager, final String name, final Object value) {
		this(dbEntityManager, name, value, null);
	}

	public ValueChunk(final DbEntityManager dbEntityManager, final String objReference) {
		this(dbEntityManager, null, null, objReference);
	}

	protected ValueChunk(final DbEntityManager dbEntityManager, final String name, final Object value, final String objReference) {
		super(dbEntityManager, CHUNK_VALUE);
		this.name = name;
		this.value = value;
		this.objReference = objReference;
	}

	@Override
	public void process(final StringBuilder out) {
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

}