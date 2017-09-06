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
import jodd.madvoc.ScopeData;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ScopeDataResolver;
import jodd.madvoc.result.MoveResult;
import jodd.servlet.ServletUtil;
import jodd.servlet.upload.MultipartRequestWrapper;
import jodd.upload.FileUpload;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;


/**
 * Request scope injector. Performs {@link MoveResult moving} as well.
 * Request injector should be independent and therefore more then one
 * instance can be used in the Madvoc application. That's why
 * configuration is being cloned on injector creation.
 */
public class RequestScopeInjector extends BaseScopeInjector
		implements Injector, Outjector {

	public RequestScopeInjector(MadvocConfig madvocConfig, ScopeDataResolver scopeDataResolver) {
		super(ScopeType.REQUEST, scopeDataResolver);
		this.encoding = madvocConfig.getEncoding();
		this.attributeMoveId = madvocConfig.getAttributeMoveId();
		silent = true;
	}

	// ---------------------------------------------------------------- configuration

	protected final String encoding;
	protected final String attributeMoveId;

	// flags

	protected boolean ignoreEmptyRequestParams;
	protected boolean treatEmptyParamsAsNull;
	protected boolean injectAttributes = true;
	protected boolean injectParameters = true;
	protected boolean trimParams;
	protected boolean encodeGetParams;
	protected boolean ignoreInvalidUploadFiles = true;

	public boolean isIgnoreEmptyRequestParams() {
		return ignoreEmptyRequestParams;
	}
	/**
	 * Specifies if empty request parameters will be totally ignored as they were not sent at all.
	 */
	public void setIgnoreEmptyRequestParams(boolean ignoreEmptyRequestParams) {
		this.ignoreEmptyRequestParams = ignoreEmptyRequestParams;
	}

	public boolean isTreatEmptyParamsAsNull() {
		return treatEmptyParamsAsNull;
	}
	/**
	 * Specifies if empty parameters will be injected as <code>null</code> value.
	 */
	public void setTreatEmptyParamsAsNull(boolean treatEmptyParamsAsNull) {
		this.treatEmptyParamsAsNull = treatEmptyParamsAsNull;
	}

	public boolean isInjectAttributes() {
		return injectAttributes;
	}
	/**
	 * Specifies if attributes will be injected.
	 */
	public void setInjectAttributes(boolean injectAttributes) {
		this.injectAttributes = injectAttributes;
	}

	public boolean isInjectParameters() {
		return injectParameters;
	}
	/**
	 * Specifies if parameters will be injected.
	 */
	public void setInjectParameters(boolean injectParameters) {
		this.injectParameters = injectParameters;
	}

	public boolean isTrimParams() {
		return trimParams;
	}
	/**
	 * Specifies if parameters will be trimmed before injection.
	 */
	public void setTrimParams(boolean trimParams) {
		this.trimParams = trimParams;
	}

	public boolean isEncodeGetParams() {
		return encodeGetParams;
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
	public void setEncodeGetParams(boolean encodeGetParams) {
		this.encodeGetParams = encodeGetParams;
	}

	/**
	 * Returns <code>true</code> if invalid and non-existing upload files are ignored.
	 */
	public boolean isIgnoreInvalidUploadFiles() {
		return ignoreInvalidUploadFiles;
	}

	/**
	 * Specifies if invalid and non-existing upload files should be <code>null</code>.
	 */
	public void setIgnoreInvalidUploadFiles(boolean ignoreInvalidUploadFiles) {
		this.ignoreInvalidUploadFiles = ignoreInvalidUploadFiles;
	}

	// ---------------------------------------------------------------- inject

	/**
	 * Inject request attributes.
	 */
	protected void injectAttributes(Target[] targets, ScopeData[] injectData, HttpServletRequest servletRequest) {
		Enumeration attributeNames = servletRequest.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attrName = (String) attributeNames.nextElement();

			for (int i = 0; i < targets.length; i++) {
				Target target = targets[i];
				if (injectData[i] == null) {
					continue;
				}
				ScopeData.In[] scopes = injectData[i].in;
				if (scopes == null) {
					continue;
				}

				for (ScopeData.In in : scopes) {
					String name = getMatchedPropertyName(in, attrName);
					if (name != null) {
						Object attrValue = servletRequest.getAttribute(attrName);
						setTargetProperty(target, name, attrValue);
					}
				}
			}
		}
	}

	/**
	 * Inject request parameters.
	 */
	protected void injectParameters(Target[] targets, ScopeData[] injectData, HttpServletRequest servletRequest) {
		boolean encode = encodeGetParams && servletRequest.getMethod().equals("GET");
		Enumeration paramNames = servletRequest.getParameterNames();

		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if (servletRequest.getAttribute(paramName) != null) {
				continue;
			}

			for (int i = 0; i < targets.length; i++) {
				Target target = targets[i];
				if (injectData[i] == null) {
					continue;
				}
				ScopeData.In[] scopes = injectData[i].in;
				if (scopes == null) {
					continue;
				}

				for (ScopeData.In in : scopes) {
					String name = getMatchedPropertyName(in, paramName);
					if (name != null) {
						String[] paramValues = servletRequest.getParameterValues(paramName);
						paramValues = ServletUtil.prepareParameters(
								paramValues, trimParams, treatEmptyParamsAsNull, ignoreEmptyRequestParams);

						if (paramValues == null) {
							continue;
						}
						if (encode) {
							for (int j = 0; j < paramValues.length; j++) {
								String p = paramValues[j];
								if (p != null) {
									paramValues[j] = StringUtil.convertCharset(p, StringPool.ISO_8859_1, encoding);
								}
							}
						}
						Object value = (paramValues.length != 1 ? paramValues : paramValues[0]);
						setTargetProperty(target, name, value);
					}
				}
			}
		}
	}

	/**
	 * Inject uploaded files from multipart request parameters.
	 */
	protected void injectUploadedFiles(Target[] targets, ScopeData[] injectData, HttpServletRequest servletRequest) {
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

			for (int i = 0; i < targets.length; i++) {
				Target target = targets[i];
				if (injectData[i] == null) {
					continue;
				}
				ScopeData.In[] scopes = injectData[i].in;
				if (scopes == null) {
					continue;
				}

				for (ScopeData.In in : scopes) {
					String name = getMatchedPropertyName(in, paramName);
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
						setTargetProperty(target, name, value);
					}
				}
			}
		}
	}


	/**
	 * Outjects all request data from move result source, if exist.
	 */
	protected void outjectMoveSource(ActionRequest actionRequest) {
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();

		String moveId = servletRequest.getParameter(attributeMoveId);
		if (moveId != null) {
			HttpSession session = servletRequest.getSession();
			ActionRequest sourceRequest = (ActionRequest) session.getAttribute(moveId);
			session.removeAttribute(moveId);
			if (sourceRequest != null) {
				outjectAfterMove(actionRequest.getHttpServletRequest(), sourceRequest);
			}
		}
	}

	/**
	 * Prepares stuff before {@link #inject(jodd.madvoc.ActionRequest)} injection}.
	 * Preparation should be invoked only once per request. It includes the following:
	 * <ul>
	 * <li>copying parameters to attributes</li>
	 * <li>handling of move results by outjection the move source.</li>
	 * </ul>
	 */
	public void prepare(ActionRequest actionRequest) {
		outjectMoveSource(actionRequest);
	}

	@Override
	public void inject(ActionRequest actionRequest) {
		Target[] targets = actionRequest.getTargets();

		ScopeData[] injectData = lookupScopeData(actionRequest);
		if (injectData == null) {
			return;
		}
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();

		if (injectAttributes) {
			injectAttributes(targets, injectData, servletRequest);
		}
		if (injectParameters) {
			injectParameters(targets, injectData, servletRequest);
			injectUploadedFiles(targets, injectData, servletRequest);
		}
	}

	// ---------------------------------------------------------------- outject

	@Override
	public void outject(ActionRequest actionRequest) {
		ScopeData[] outjectData = lookupScopeData(actionRequest);
		if (outjectData == null) {
			return;
		}

		Target[] targets = actionRequest.getTargets();
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();

		for (int i = 0; i < targets.length; i++) {
			Target target = targets[i];
			if (outjectData[i] == null) {
				continue;
			}
			ScopeData.Out[] scopes = outjectData[i].out;
			if (scopes == null) {
				continue;
			}

			for (ScopeData.Out out : scopes) {
				Object value = getTargetProperty(target, out);
				servletRequest.setAttribute(out.name, value);
			}
		}
	}

	protected void outjectAfterMove(ServletRequest targetServletRequest, ActionRequest sourceRequest) {
		ScopeData[] outjectData = lookupScopeData(sourceRequest);
		if (outjectData == null) {
			return;
		}

		Target[] targets = sourceRequest.getTargets();

		for (int i = 0; i < targets.length; i++) {
			Target target = targets[i];
			if (outjectData[i] == null) {
				continue;
			}
			ScopeData.Out[] scopes = outjectData[i].out;
			if (scopes == null) {
				continue;
			}

			for (ScopeData.Out out : scopes) {
				Object value = getTargetProperty(target, out);
				targetServletRequest.setAttribute(out.name, value);
			}
		}
	}

}