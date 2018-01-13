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

package jodd.madvoc.injector;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocConfig;
import jodd.madvoc.ScopeType;
import jodd.servlet.ServletUtil;
import jodd.servlet.upload.MultipartRequestWrapper;
import jodd.upload.FileUpload;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;


/**
 * Request scope injector.
 * Request injector should be independent and therefore more then one
 * instance can be used in the Madvoc application. That's why
 * configuration is being cloned on injector creation.
 */
public class RequestScopeInjector implements Injector, Outjector {
	private final static ScopeType SCOPE_TYPE = ScopeType.REQUEST;

	public RequestScopeInjector(final MadvocConfig madvocConfig) {
		this.encoding = madvocConfig.getEncoding();
	}

	// ---------------------------------------------------------------- configuration

	protected final String encoding;

	// flags

	protected boolean ignoreEmptyRequestParams;
	protected boolean treatEmptyParamsAsNull;
	protected boolean injectAttributes = true;
	protected boolean injectParameters = true;
	protected boolean trimParams;
	protected boolean encodeGetParams;
	protected boolean ignoreInvalidUploadFiles = true;

	/**
	 * Specifies if empty request parameters will be totally ignored as they were not sent at all.
	 */
	public void setIgnoreEmptyRequestParams(final boolean ignoreEmptyRequestParams) {
		this.ignoreEmptyRequestParams = ignoreEmptyRequestParams;
	}

	/**
	 * Specifies if empty parameters will be injected as <code>null</code> value.
	 */
	public void setTreatEmptyParamsAsNull(final boolean treatEmptyParamsAsNull) {
		this.treatEmptyParamsAsNull = treatEmptyParamsAsNull;
	}

	/**
	 * Specifies if attributes will be injected.
	 */
	public void setInjectAttributes(final boolean injectAttributes) {
		this.injectAttributes = injectAttributes;
	}

	/**
	 * Specifies if parameters will be injected.
	 */
	public void setInjectParameters(final boolean injectParameters) {
		this.injectParameters = injectParameters;
	}

	/**
	 * Specifies if parameters will be trimmed before injection.
	 */
	public void setTrimParams(final boolean trimParams) {
		this.trimParams = trimParams;
	}

	/**
	 * Specifies if GET parameters should be encoded. Alternatively, this can be set in container as well.
	 * Setting URIEncoding="UTF-8" in Tomcat's connector settings within the server.xml
	 * file communicates the character-encoding choice to the web server,
	 * and the Tomcat server correctly reads the URL GET parameters correctly.
	 * On Sun Java System Application Server 8.1, "&lt;parameter-encoding default-charset="UTF-8"/&gt;"
	 * can be included in the sun-web.xml file.
	 * See more: http://java.sun.com/developer/technicalArticles/Intl/HTTPCharset/
	 */
	public void setEncodeGetParams(final boolean encodeGetParams) {
		this.encodeGetParams = encodeGetParams;
	}

	/**
	 * Specifies if invalid and non-existing upload files should be <code>null</code>.
	 */
	public void setIgnoreInvalidUploadFiles(final boolean ignoreInvalidUploadFiles) {
		this.ignoreInvalidUploadFiles = ignoreInvalidUploadFiles;
	}

	// ---------------------------------------------------------------- inject

	/**
	 * Injects request attributes.
	 */
	protected void injectAttributes(final Targets targets, final HttpServletRequest servletRequest) {
		Enumeration attributeNames = servletRequest.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attrName = (String) attributeNames.nextElement();

			targets.forEachTargetAndInScopes(SCOPE_TYPE, (target, in) -> {
				String name = in.matchedPropertyName(attrName);
				if (name != null) {
					Object attrValue = servletRequest.getAttribute(attrName);
					target.writeValue(name, attrValue, true);
				}
			});
		}
	}

	/**
	 * Inject request parameters.
	 */
	protected void injectParameters(final Targets targets, final HttpServletRequest servletRequest) {
		boolean encode = encodeGetParams && servletRequest.getMethod().equals("GET");
		Enumeration paramNames = servletRequest.getParameterNames();

		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if (servletRequest.getAttribute(paramName) != null) {
				continue;
			}

			targets.forEachTargetAndInScopes(SCOPE_TYPE, (target, in) -> {
				String name = in.matchedPropertyName(paramName);
				if (name != null) {
					String[] paramValues = servletRequest.getParameterValues(paramName);

					paramValues = ServletUtil.prepareParameters(
							paramValues, trimParams, treatEmptyParamsAsNull, ignoreEmptyRequestParams);

					if (paramValues != null) {
						if (encode) {
							for (int j = 0; j < paramValues.length; j++) {
								String p = paramValues[j];
								if (p != null) {
									paramValues[j] = StringUtil.convertCharset(p, StringPool.ISO_8859_1, encoding);
								}
							}
						}
						Object value = (paramValues.length != 1 ? paramValues : paramValues[0]);
						target.writeValue(name, value, true);
					}
				}
			});
		}
	}

	/**
	 * Inject uploaded files from multipart request parameters.
	 */
	protected void injectUploadedFiles(final Targets targets, final HttpServletRequest servletRequest) {
		if (!(servletRequest instanceof MultipartRequestWrapper)) {
			return;
		}
		MultipartRequestWrapper multipartRequest = (MultipartRequestWrapper) servletRequest;
		if (!multipartRequest.isMultipart()) {
			return;
		}
		Enumeration paramNames = multipartRequest.getFileParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if (servletRequest.getAttribute(paramName) != null) {
				continue;
			}

			targets.forEachTargetAndInScopes(SCOPE_TYPE, (target, in) -> {
				String name = in.matchedPropertyName(paramName);
				if (name != null) {
					FileUpload[] paramValues = multipartRequest.getFiles(paramName);

					if (ignoreInvalidUploadFiles) {
						for (int j = 0; j < paramValues.length; j++) {
							FileUpload paramValue = paramValues[j];

							if ((!paramValue.isValid()) || (!paramValue.isUploaded())) {
								paramValues[j] = null;
							}
						}
					}

					Object value = (paramValues.length == 1 ? paramValues[0] : paramValues);
					target.writeValue(name, value, true);
				}
			});
		}
	}

	@Override
	public void inject(final ActionRequest actionRequest) {
		Targets targets = actionRequest.targets();
		if (!targets.usesScope(SCOPE_TYPE)) {
			return;
		}

		HttpServletRequest servletRequest = actionRequest.httpServletRequest();

		if (injectAttributes) {
			injectAttributes(targets, servletRequest);
		}
		if (injectParameters) {
			injectParameters(targets, servletRequest);
			injectUploadedFiles(targets, servletRequest);
		}
	}

	// ---------------------------------------------------------------- outject

	@Override
	public void outject(final ActionRequest actionRequest) {
		Targets targets = actionRequest.targets();
		if (!targets.usesScope(SCOPE_TYPE)) {
			return;
		}

		HttpServletRequest servletRequest = actionRequest.httpServletRequest();

		targets.forEachTargetAndOutScopes(SCOPE_TYPE, (target, out) -> {
			Object value = target.readTargetProperty(out);
			servletRequest.setAttribute(out.name, value);
		});
	}

}