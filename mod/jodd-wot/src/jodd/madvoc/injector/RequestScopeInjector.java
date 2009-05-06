// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ScopeType;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.component.ScopeDataManager;
import jodd.madvoc.result.MoveResult;
import jodd.servlet.upload.MultipartRequestWrapper;
import jodd.servlet.upload.FileUpload;
import jodd.servlet.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;


/**
 * Request scope injector. Perfroms {@link MoveResult moving} as well.
 */
public class RequestScopeInjector extends ScopeInjector {

	public RequestScopeInjector(ScopeDataManager scopeDataManager) {
		super(scopeDataManager);
	}


	// ---------------------------------------------------------------- configuration

	protected boolean ignoreEmptyRequestParams = true;

	public boolean isIgnoreEmptyRequestParams() {
		return ignoreEmptyRequestParams;
	}

	/**
	 * Specifies if empty request parameters should be ignored.
	 */
	public void setIgnoreEmptyRequestParams(boolean ignoreEmptyRequestParams) {
		this.ignoreEmptyRequestParams = ignoreEmptyRequestParams;
	}

	public RequestScopeInjector ignoreEmptyRequestParams(boolean ignoreEmptyRequestParams) {
		this.ignoreEmptyRequestParams = ignoreEmptyRequestParams;
		return this;
	}



	protected boolean injectAttributes = true;

	public boolean isInjectAttributes() {
		return injectAttributes;
	}

	/**
	 * Specifies if attributes should be set.
	 */
	public void setInjectAttributes(boolean injectAttributes) {
		this.injectAttributes = injectAttributes;
	}

	public RequestScopeInjector injectAttributes(boolean injectAttributes) {
		this.injectAttributes = injectAttributes;
		return this;
	}



	protected boolean injectParameters = true;

	public boolean isInjectParameters() {
		return injectParameters;
	}

	public void setInjectParameters(boolean injectParameters) {
		this.injectParameters = injectParameters;
	}

	public RequestScopeInjector injectParameters(boolean injectParameters) {
		this.injectParameters = injectParameters;
		return this;
	}

	protected boolean copyParamsToAttributes;

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

	public RequestScopeInjector copyParamsToAttributes(boolean copyParamsToAttributes) {
		this.copyParamsToAttributes = copyParamsToAttributes;
		return this;
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

					// ignore empty parameters
					if (ignoreEmptyRequestParams == true) {
						int ignoreCount = 0;
						for (int i = 0; i < paramValues.length; i++) {
							if (paramValues[i].length() == 0) {
								paramValues[i] = null;
								ignoreCount++;
							}
						}
						if (ignoreCount == paramValues.length) {
							continue;	// ignore null parameters
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
			ServletUtil.copyParamsToAttributes(servletRequest, ignoreEmptyRequestParams);
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
