// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ScopeType;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.component.ScopeDataManager;
import jodd.madvoc.result.MoveResult;
import jodd.servlet.upload.MultipartRequestWrapper;
import jodd.servlet.upload.FileUpload;
import jodd.servlet.ServletUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.io.UnsupportedEncodingException;


/**
 * Request scope injector. Perfroms {@link MoveResult moving} as well.
 */
public class RequestScopeInjector extends ScopeInjector {

	private static final String REQ_METHOD_GET = "GET";

	protected final String encoding;

	public RequestScopeInjector(ScopeDataManager scopeDataManager) {
		super(scopeDataManager);
		this.encoding = scopeDataManager.getMadvocConfig().getEncoding();
	}


	// ---------------------------------------------------------------- configuration

	protected boolean ignoreEmptyRequestParams;
	protected boolean treatEmptyParamsAsNull;
	protected boolean injectAttributes = true;
	protected boolean injectParameters = true;
	protected boolean copyParamsToAttributes;
	protected boolean trimParams;
	protected boolean encodeGetParams;

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

	public boolean isCopyParamsToAttributes() {
		return copyParamsToAttributes;
	}
	/**
	 * Specifies if request parameters will to be copied to attributes.
	 * Usually, when this flag is set to <code>true</code>, {@link #setInjectAttributes(boolean) injectOnlyAttributes}
	 * is also set to <code>true</code>. 
	 */
	public void setCopyParamsToAttributes(boolean copyParamsToAttributes) {
		this.copyParamsToAttributes = copyParamsToAttributes;
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
	 * Specifies if GET parameters should be encoded. Alternativly, this can be set in container as well.
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

	// ---------------------------------------------------------------- inject

	/**
	 * Inject request attributes.
	 */
	protected void injectAttributes(Object target, ScopeData.In[] injectData, HttpServletRequest servletRequest) {
		Enumeration attributeNames = servletRequest.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attrName = (String) attributeNames.nextElement();
			for (ScopeData.In ii : injectData) {
				String name = getMatchedPropertyName(ii, attrName);
				if (name != null) {
					Object attrValue = servletRequest.getAttribute(attrName);
					setTargetProperty(target, name, attrValue, ii.create);
					if (ii.remove) {
						servletRequest.removeAttribute(attrName);
					}
				}
			}
		}
	}

	/**
	 * Inject request parameters. Parameters with the same name as one of request attributes
	 * are simply ignored.
	 */
	protected void injectParameters(Object target, ScopeData.In[] injectData, HttpServletRequest servletRequest) {
		boolean encode = encodeGetParams && servletRequest.getMethod().equals(REQ_METHOD_GET);
		Enumeration paramNames = servletRequest.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if (servletRequest.getAttribute(paramName) != null) {
				continue;
			}
			for (ScopeData.In ii : injectData) {
				String name = getMatchedPropertyName(ii, paramName);
				if (name != null) {
					String[] paramValues = servletRequest.getParameterValues(paramName);
					paramValues = ServletUtil.prepareParameters(paramValues, trimParams, treatEmptyParamsAsNull, ignoreEmptyRequestParams);
					if (paramValues == null) {
						continue;
					}
					if (encode) {
						for (int i = 0; i < paramValues.length; i++) {
							String p = paramValues[i];
							if (p != null) {
								try {
									paramValues[i] = StringUtil.convertCharset(p, StringPool.ISO_8859_1, encoding);
								} catch (UnsupportedEncodingException unex) {
									//ignore
								}
							}
						}
					}
					setTargetProperty(target, name, paramValues, ii.create);
				}
			}
		}
	}



	/**
	 * Inject uploaded files from multipart request parameters.
	 */
	protected void injectUploadedFiles(Object target, ScopeData.In[] injectData, HttpServletRequest servletRequest) {
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
			for (ScopeData.In ii : injectData) {
				String name = getMatchedPropertyName(ii, paramName);
				if (name != null) {
					FileUpload[] paramValues = multipartRequest.getFiles(paramName);
					setTargetProperty(target, name, paramValues, ii.create);
				}
			}
		}
	}


	/**
	 * Outjects all request data from move result source, if exist.
	 */
	protected void outjectMoveSource(HttpServletRequest servletRequest) {
		String moveId = servletRequest.getParameter(MoveResult.MOVE_ID);
		if (moveId != null) {
			HttpSession session = servletRequest.getSession();
			ActionRequest sourceRequest = (ActionRequest) session.getAttribute(moveId);
			session.removeAttribute(moveId);
			if (sourceRequest != null) {
				outject(sourceRequest.getAction(), servletRequest);
			}
		}
	}


	public void inject(Object target, HttpServletRequest servletRequest) {
		if (copyParamsToAttributes == true) {
			ServletUtil.copyParamsToAttributes(servletRequest, trimParams, treatEmptyParamsAsNull, ignoreEmptyRequestParams);
		}
		outjectMoveSource(servletRequest);
		ScopeData.In[] injectData = scopeDataManager.lookupInData(target, ScopeType.REQUEST);
		if (injectData == null) {
			return;
		}
		if (injectAttributes == true) {
			injectAttributes(target, injectData, servletRequest);
		}
		if (injectParameters == true) {
			injectParameters(target, injectData, servletRequest);
			injectUploadedFiles(target, injectData, servletRequest);
		}
	}

	// ---------------------------------------------------------------- outject

	public void outject(Object target, HttpServletRequest servletRequest) {
		ScopeData.Out[] outjectData = scopeDataManager.lookupOutData(target, ScopeType.REQUEST);
		if (outjectData == null) {
			return;
		}

		for (ScopeData.Out oi : outjectData) {
			Object value = getTargetProperty(target, oi);
			servletRequest.setAttribute(oi.name, value);
		}
	}
}
