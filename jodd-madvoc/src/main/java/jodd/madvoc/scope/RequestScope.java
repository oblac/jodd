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

import jodd.io.upload.FileUpload;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocConfig;
import jodd.madvoc.config.Targets;
import jodd.petite.meta.PetiteInject;
import jodd.servlet.ServletUtil;
import jodd.servlet.upload.MultipartRequestWrapper;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * The request scope.
 */
public class RequestScope implements MadvocScope {

	@PetiteInject
	MadvocConfig madvocConfig;

	protected final ActionPathMacroInjector actionPathMacroInjector = new ActionPathMacroInjector(this);
	protected final InstancesInjector instancesInjector = new InstancesInjector(this);

	// ---------------------------------------------------------------- configuration

	// flags

	/**
	 * Specifies if empty request parameters will be totally ignored as they were not sent at all.
	 */
	protected boolean ignoreEmptyRequestParams;
	/**
	 * Specifies if empty parameters will be injected as <code>null</code> value.
	 */
	protected boolean treatEmptyParamsAsNull;
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

	// ---------------------------------------------------------------- inject

	@Override
	public void inject(final ActionRequest actionRequest, final Targets targets) {
		final HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();

		instancesInjector.inject(actionRequest, targets);

		if (injectAttributes) {
			injectAttributes(servletRequest, targets);
		}
		if (injectParameters) {
			injectParameters(servletRequest, targets);
			injectUploadedFiles(servletRequest, targets);
		}

		actionPathMacroInjector.inject(actionRequest, targets);
	}

	/**
	 * Injects request attributes.
	 */
	protected void injectAttributes(final HttpServletRequest servletRequest, final Targets targets) {
		final Enumeration<String> attributeNames = servletRequest.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			final String attrName = attributeNames.nextElement();

			targets.forEachTargetAndIn(this, (target, in) -> {
				final String name = in.matchedName(attrName);
				if (name != null) {
					final Object attrValue = servletRequest.getAttribute(attrName);
					target.writeValue(name, attrValue, true);
				}
			});
		}
	}

	/**
	 * Inject request parameters.
	 */
	protected void injectParameters(final HttpServletRequest servletRequest, final Targets targets) {
		final boolean encode = encodeGetParams && servletRequest.getMethod().equals("GET");
		final Enumeration<String> paramNames = servletRequest.getParameterNames();

		while (paramNames.hasMoreElements()) {
			final String paramName = paramNames.nextElement();
			if (servletRequest.getAttribute(paramName) != null) {
				continue;
			}

			targets.forEachTargetAndIn(this, (target, in) -> {
				final String name = in.matchedName(paramName);
				if (name != null) {
					String[] paramValues = servletRequest.getParameterValues(paramName);

					paramValues = ServletUtil.prepareParameters(
						paramValues, treatEmptyParamsAsNull, ignoreEmptyRequestParams);

					if (paramValues != null) {
						if (encode) {
							for (int j = 0; j < paramValues.length; j++) {
								final String p = paramValues[j];
								if (p != null) {
									final String encoding = madvocConfig.getEncoding();
									paramValues[j] = StringUtil.convertCharset(p, StringPool.ISO_8859_1, encoding);
								}
							}
						}
						final Object value = (paramValues.length != 1 ? paramValues : paramValues[0]);
						target.writeValue(name, value, true);
					}
				}
			});
		}
	}

	/**
	 * Inject uploaded files from multipart request parameters.
	 */
	protected void injectUploadedFiles(final HttpServletRequest servletRequest, final Targets targets) {
		if (!(servletRequest instanceof MultipartRequestWrapper)) {
			return;
		}
		final MultipartRequestWrapper multipartRequest = (MultipartRequestWrapper) servletRequest;
		if (!multipartRequest.isMultipart()) {
			return;
		}
		final Enumeration<String> paramNames = multipartRequest.getFileParameterNames();
		while (paramNames.hasMoreElements()) {
			final String paramName = paramNames.nextElement();
			if (servletRequest.getAttribute(paramName) != null) {
				continue;
			}

			targets.forEachTargetAndIn(this, (target, in) -> {
				final String name = in.matchedName(paramName);
				if (name != null) {
					final FileUpload[] paramValues = multipartRequest.getFiles(paramName);

					if (ignoreInvalidUploadFiles) {
						for (int j = 0; j < paramValues.length; j++) {
							final FileUpload paramValue = paramValues[j];

							if ((!paramValue.isValid()) || (!paramValue.isUploaded())) {
								paramValues[j] = null;
							}
						}
					}

					final Object value = (paramValues.length == 1 ? paramValues[0] : paramValues);
					target.writeValue(name, value, true);
				}
			});
		}
	}

	// ---------------------------------------------------------------- outject

	@Override
	public void outject(final ActionRequest actionRequest, final Targets targets) {
		final HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();

		targets.forEachTargetAndOut(this, (target, out) -> {
			final Object value = target.readValue(out);
			servletRequest.setAttribute(out.name(), value);
		});
	}

}
