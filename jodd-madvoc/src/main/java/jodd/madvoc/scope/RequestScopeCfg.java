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

package jodd.madvoc.scope;

abstract class RequestScopeCfg {

	// flags

	/**
	 * Specifies if empty request parameters will be totally ignored as they were not sent at all.
	 */
	protected boolean ignoreEmptyRequestParams;
	/**
	 * Specifies if empty parameters will be injected as <code>null</code> value.
	 */
	protected boolean treatEmptyParamsAsNull = true;
	/**
	 * Specifies if attributes will be injected.
	 */
	protected boolean injectAttributes = true;
	/**
	 * Specifies if parameters will be injected.
	 */
	protected boolean injectParameters = true;
	/**
	 * Specifies if GET parameters should be encoded. Alternatively, this can be set in container as well.
	 * Setting URIEncoding="UTF-8" in Tomcat's connector settings within the server.xml
	 * file communicates the character-encoding choice to the web server,
	 * and the Tomcat server correctly reads the URL GET parameters correctly.
	 * On Sun Java System Application Server 8.1, "&lt;parameter-encoding default-charset="UTF-8"/&gt;"
	 * can be included in the sun-web.xml file.
	 * See more: http://java.sun.com/developer/technicalArticles/Intl/HTTPCharset/
	 */
	protected boolean encodeGetParams;
	/**
	 * Specifies if invalid and non-existing upload files should be <code>null</code>.
	 */
	protected boolean ignoreInvalidUploadFiles = true;

	public void setIgnoreEmptyRequestParams(final boolean ignoreEmptyRequestParams) {
		this.ignoreEmptyRequestParams = ignoreEmptyRequestParams;
	}

	public void setTreatEmptyParamsAsNull(final boolean treatEmptyParamsAsNull) {
		this.treatEmptyParamsAsNull = treatEmptyParamsAsNull;
	}

	public void setInjectAttributes(final boolean injectAttributes) {
		this.injectAttributes = injectAttributes;
	}

	public void setInjectParameters(final boolean injectParameters) {
		this.injectParameters = injectParameters;
	}

	public void setEncodeGetParams(final boolean encodeGetParams) {
		this.encodeGetParams = encodeGetParams;
	}

	public void setIgnoreInvalidUploadFiles(final boolean ignoreInvalidUploadFiles) {
		this.ignoreInvalidUploadFiles = ignoreInvalidUploadFiles;
	}


}
