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

package jodd.json;

import static jodd.util.StringPool.NULL;

/**
 * {@link JsonSerializer} that prints out formatted JSON.
 * It is slower, but the output is prettier.
 */
public class PrettyJsonSerializer extends JsonSerializer {

	private int deep = 0;
	private boolean breakOnNewObject = true;
	private int identSize = 2;
	private char identChar = ' ';
	private boolean prefixSeparatorBySpace = true;
	private boolean suffixSeparatorBySpace = true;

	/**
	 * Defines ident size in number of {@link #identChar} to be used for single indentation.
	 */
	public void identSize(final int ident) {
		this.identSize = ident;
	}

	/**
	 * Sets ident character. Usually its a space or a tab.
	 */
	public void identChar(final char identChar) {
		this.identChar = identChar;
	}

	private void breakLine(final JsonContext jsonContext) {
		jsonContext.write('\n');
		ident(jsonContext);
	}

	private void ident(final JsonContext jsonContext) {
		for (int i = 0; i < deep; i++) {
			for (int j = 0; j < identSize; j++) {
				jsonContext.write(identChar);
			}
		}
	}

	@Override
	public JsonContext createJsonContext(final Appendable appendable) {
		return new JsonContext(this, appendable) {
			@Override
			public void writeOpenArray() {
				deep++;
				super.writeOpenArray();
				if (breakOnNewObject) {
					breakLine(this);
				}
			}

			@Override
			public void writeCloseArray() {
				deep--;
				breakLine(this);
				super.writeCloseArray();
			}

			@Override
			public void writeOpenObject() {
				popName();
				deep++;
				write('{');
				if (breakOnNewObject) {
					breakLine(this);
				}
			}

			@Override
			public void writeCloseObject() {
				deep--;
				if (breakOnNewObject) {
					breakLine(this);
				}
				super.writeCloseObject();
			}

			@Override
			public void writeComma() {
				super.writeComma();
				breakLine(this);
			}

			@Override
			public void writeName(final String name) {
				if (name != null) {
					writeString(name);
				}
				else {
					write(NULL);
				}

				if (prefixSeparatorBySpace) {
					write(' ');
				}

				write(':');

				if (suffixSeparatorBySpace) {
					write(' ');
				}
			}
		};
	}
}
