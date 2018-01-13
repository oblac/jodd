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

package jodd.madvoc.config;

/**
 * Action definition is represented by action's path, http method and result base path.
 */
public class ActionDefinition {

	protected final String actionPath;
	protected final String actionMethod;
	protected final String resultBasePath;

	public ActionDefinition(final String actionPath, final String actionMethod, final String resultBasePath) {
		this.actionPath = actionPath;
		this.actionMethod = actionMethod;
		this.resultBasePath = resultBasePath == null ? actionPath : resultBasePath;
	}

	public ActionDefinition(final String actionPath, final String actionMethod) {
		this.actionPath = actionPath;
		this.actionMethod = actionMethod;
		this.resultBasePath = actionPath;
	}

	public ActionDefinition(final String actionPath) {
		this(actionPath, null);
	}

	/**
	 * Returns action's path.
	 */
	public String actionPath() {
		return actionPath;
	}

	/**
	 * Returns action's HTTP method.
	 */
	public String actionMethod() {
		return actionMethod;
	}

	/**
	 * Returns result base path.
	 */
	public String resultBasePath() {
		return resultBasePath;
	}
}