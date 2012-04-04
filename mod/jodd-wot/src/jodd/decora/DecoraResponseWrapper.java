// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.decora;

import jodd.servlet.wrapper.BufferResponseWrapper;
import jodd.servlet.wrapper.LastModifiedData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Decora response wrapper uses {@link DecoraManager decora manager} to determine
 * if buffering should be enabled or disabled.
 */
public class DecoraResponseWrapper extends BufferResponseWrapper {

	protected final DecoraManager decoraManager;
	protected final HttpServletRequest request;
	protected final HttpServletResponse response;

	public DecoraResponseWrapper(HttpServletRequest originalRequest, HttpServletResponse originalResponse, LastModifiedData lastModifiedData, DecoraManager decoraManager) {
		super(originalResponse, lastModifiedData);
		this.request = originalRequest;
		this.response = originalResponse;
		this.decoraManager = decoraManager;
	}

	@Override
	protected boolean bufferContentType(String contentType, String mimeType, String encoding) {
		return decoraManager.decorateContentType(contentType, mimeType, encoding);
	}

	@Override
	protected boolean bufferStatusCode(int statusCode) {
		return decoraManager.decorateStatusCode(statusCode);
	}

	// todo move to BufferResponseWrapper ?
	@Override
	protected void preResponseCommit() {
		long lastModified = lastModifiedData.getLastModified();
		long ifModifiedSince = request.getDateHeader("If-Modified-Since");

		if (lastModified > -1 && !response.containsHeader("Last-Modified")) {
			if (ifModifiedSince < (lastModified / 1000 * 1000)) {
				response.setDateHeader("Last-Modified", lastModified);
			} else {
				response.reset();
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			}
		}
	}
}