// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocException;
import jodd.madvoc.ScopeData;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ScopeDataResolver;
import jodd.madvoc.result.MoveResult;
import jodd.servlet.upload.MultipartRequestWrapper;
import jodd.upload.FileUpload;
import jodd.servlet.ServletUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

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
		this.config = madvocConfig.getRequestScopeInjectorConfig().clone();
		this.attributeMoveId = madvocConfig.getAttributeMoveId();
	}

	// ---------------------------------------------------------------- configuration

	protected final String encoding;
	protected final Config config;
	protected final String attributeMoveId;

	/**
	 * Returns encoding used inside. The same as Madvoc encoding.
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Returns request scope configuration.
	 */
	public Config getConfig() {
		return config;
	}

	/**
	 * Request scope configuration.
	 */
	public static class Config implements Cloneable {
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

		@Override
		public Config clone() {
			try {
				return (Config) super.clone();
			} catch (CloneNotSupportedException cnsex) {
				throw new MadvocException(cnsex);
			}
		}
	}

	// ---------------------------------------------------------------- inject

	/**
	 * Inject request attributes.
	 */
	protected void injectAttributes(Object[] targets, ScopeData.In[][] injectData, HttpServletRequest servletRequest) {
		Enumeration attributeNames = servletRequest.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attrName = (String) attributeNames.nextElement();

			for (int i = 0; i < targets.length; i++) {
				Object target = targets[i];
				ScopeData.In[] scopes = injectData[i];
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
	 * Inject request parameters. Parameters with the same name as one of request attributes
	 * are simply ignored.
	 */
	protected void injectParameters(Object[] targets, ScopeData.In[][] injectData, HttpServletRequest servletRequest) {
		boolean encode = config.encodeGetParams && servletRequest.getMethod().equals("GET");
		Enumeration paramNames = servletRequest.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if (servletRequest.getAttribute(paramName) != null) {
				continue;
			}

			for (int i = 0; i < targets.length; i++) {
				Object target = targets[i];
				ScopeData.In[] scopes = injectData[i];
				if (scopes == null) {
					continue;
				}

				for (ScopeData.In in : scopes) {
					String name = getMatchedPropertyName(in, paramName);
					if (name != null) {
						String[] paramValues = servletRequest.getParameterValues(paramName);
						paramValues = ServletUtil.prepareParameters(
								paramValues,
								config.trimParams,
								config.treatEmptyParamsAsNull,
								config.ignoreEmptyRequestParams);

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
	protected void injectUploadedFiles(Object[] targets, ScopeData.In[][] injectData, HttpServletRequest servletRequest) {
		if ((servletRequest instanceof MultipartRequestWrapper) == false) {
			return;
		}
		MultipartRequestWrapper multipartRequest = (MultipartRequestWrapper) servletRequest;
		if (multipartRequest.isMultipart() == false) {
			return;
		}
		Enumeration paramNames = multipartRequest.getFileParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if (servletRequest.getAttribute(paramName) != null) {
				continue;
			}

			for (int i = 0; i < targets.length; i++) {
				Object target = targets[i];
				ScopeData.In[] scopes = injectData[i];
				if (scopes == null) {
					continue;
				}

				for (ScopeData.In in : scopes) {
					String name = getMatchedPropertyName(in, paramName);
					if (name != null) {
						FileUpload[] paramValues = multipartRequest.getFiles(paramName);

						if (config.ignoreInvalidUploadFiles) {
							for (int j = 0; j < paramValues.length; j++) {
								FileUpload paramValue = paramValues[j];

								if ((paramValue.isValid() == false) || (paramValue.isUploaded() == false)) {
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
				outjectAfterMove(sourceRequest);
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

	public void inject(ActionRequest actionRequest) {
		Object[] targets = actionRequest.getTargets();

		ScopeData.In[][] injectData = lookupInData(actionRequest);
		if (injectData == null) {
			return;
		}
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();

		if (config.injectAttributes == true) {
			injectAttributes(targets, injectData, servletRequest);
		}
		if (config.injectParameters == true) {
			injectParameters(targets, injectData, servletRequest);
			injectUploadedFiles(targets, injectData, servletRequest);
		}
	}

	// ---------------------------------------------------------------- outject

	public void outject(ActionRequest actionRequest) {
		ScopeData.Out[][] outjectData = lookupOutData(actionRequest);
		if (outjectData == null) {
			return;
		}

		Object[] targets = actionRequest.getTargets();
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();

		for (int i = 0; i < targets.length; i++) {
			Object target = targets[i];
			ScopeData.Out[] scopes = outjectData[i];
			if (scopes == null) {
				continue;
			}

			for (ScopeData.Out out : scopes) {
				Object value = getTargetProperty(target, out);
				servletRequest.setAttribute(out.name, value);
			}
		}
	}

	protected void outjectAfterMove(ActionRequest sourceRequest) {
		ScopeData.Out[][] outjectData = lookupOutData(sourceRequest);
		if (outjectData == null) {
			return;
		}

		Object[] targets = sourceRequest.getTargets();
		HttpServletRequest servletRequest = sourceRequest.getHttpServletRequest();

		for (int i = 0; i < targets.length; i++) {
			Object target = targets[i];
			ScopeData.Out[] scopes = outjectData[i];
			if (scopes == null) {
				continue;
			}

			for (ScopeData.Out out : scopes) {
				Object value = getTargetProperty(target, out);
				servletRequest.setAttribute(out.name, value);
			}
		}
	}

}