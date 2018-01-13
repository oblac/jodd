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

package jodd.json.impl;

import jodd.io.FileUtil;
import jodd.json.JsonContext;
import jodd.json.JsonException;
import jodd.json.TypeJsonSerializer;
import jodd.util.Base64;

import java.io.File;
import java.io.IOException;

/**
 * File json serializer offers various ways of file to JSON serializations.
 */
public class FileJsonSerializer implements TypeJsonSerializer<File> {

	public enum Type {
		/**
		 * File will be serialized with the full path.
		 */
		PATH,
		/**
		 * File will be serialized as its name.
		 */
		NAME,
		/**
		 * File will be serialized with content in Base64 form/
		 */
		CONTENT
	}

	public FileJsonSerializer(final Type serializationType) {
		this.serializationType = serializationType;
	}

	private final Type serializationType;


	@Override
	public boolean serialize(final JsonContext jsonContext, final File file) {
		switch (serializationType) {
			case PATH:
				jsonContext.writeString(file.getAbsolutePath());
				break;
			case NAME:
				jsonContext.writeString(file.getName());
				break;
			case CONTENT: {
					byte[] bytes;

					try {
						bytes = FileUtil.readBytes(file);
					}
					catch (IOException e) {
						throw new JsonException("Unable to read files content", e);
					}

					String encoded = Base64.encodeToString(bytes);

					jsonContext.writeString(encoded);
				}
				break;
			default:
				throw new JsonException("Invalid type");
		}
		return true;
	}
}