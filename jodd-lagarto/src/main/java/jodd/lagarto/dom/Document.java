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

package jodd.lagarto.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Document node is always a root node.
 * Holds various DOM-related configuration and information.
 */
public class Document extends Node {

	protected long elapsedTime;
	protected final LagartoDomBuilderConfig config;
	protected List<String> errors;

	public Document() {
		this(new LagartoDomBuilderConfig());
	}

	/**
	 * Document constructor with all relevant flags.
	 */
	public Document(LagartoDomBuilderConfig config) {
		super(null, NodeType.DOCUMENT, null);
		this.config = config;
		this.elapsedTime = System.currentTimeMillis();
	}

	@Override
	public Document clone() {
		Document document = cloneTo(new Document(config));
		document.elapsedTime = this.elapsedTime;
		return document;
	}

	/**
	 * Notifies document that parsing is done.
	 */
	protected void end() {
		elapsedTime = System.currentTimeMillis() - elapsedTime;
	}

	@Override
	protected void visitNode(NodeVisitor nodeVisitor) {
		nodeVisitor.document(this);
	}

	// ---------------------------------------------------------------- errors

	/**
	 * Add new error message to the {@link #getErrors() errors list}.
	 * If errors are not collected error, message is ignored.
	 */
	public void addError(String message) {
		if (config.collectErrors) {
			if (errors == null) {
				errors = new ArrayList<>();
			}
			errors.add(message);
		}
	}

	/**
	 * Returns list of warnings and errors occurred during parsing.
	 * Returns <code>null</code> if parsing was successful; or if
	 * errors are not collected.
	 */
	public List<String> getErrors() {
		return errors;
	}

	// ---------------------------------------------------------------- attr

	/**
	 * Document node does not have attributes.
	 */
	@Override
	public void setAttribute(String name, String value) {
	}

	// ---------------------------------------------------------------- getter

	/**
	 * Returns DOM building elapsed time.
	 */
	public long getElapsedTime() {
		return elapsedTime;
	}

	/**
	 * Returns used {@link jodd.lagarto.dom.LagartoDomBuilderConfig}.
	 */
	public LagartoDomBuilderConfig getConfig() {
		return config;
	}

}