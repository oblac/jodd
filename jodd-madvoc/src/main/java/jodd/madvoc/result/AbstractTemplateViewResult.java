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

package jodd.madvoc.result;

import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocUtil;
import jodd.madvoc.ResultPath;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.meta.In;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * Base class for dispatching results. May be used as base class for any template-based views.
 */
public abstract class AbstractTemplateViewResult extends BaseActionResult<String> {

	private static final Logger log = LoggerFactory.getLogger(AbstractTemplateViewResult.class);

	protected HashMap<String, String> targetCache;

	public AbstractTemplateViewResult(String name) {
		super(name);
		targetCache = new HashMap<>(256);
	}

	@In(scope = ScopeType.CONTEXT)
	protected ResultMapper resultMapper;

	/**
	 * Dispatches to the JSP location created from result value and JSP extension.
	 * Does its forward via a <code>RequestDispatcher</code>. If the dispatch fails, a 404 error
	 * will be sent back in the http response.
	 */
	public void render(ActionRequest actionRequest, String resultValue) throws Exception {
		String resultBasePath = actionRequest.getActionConfig().getResultBasePath();

		String actionAndResultPath = resultBasePath + (resultValue != null ? ':' + resultValue : StringPool.EMPTY);

		String target = targetCache.get(actionAndResultPath);

		if (target == null) {
			if (log.isDebugEnabled()) {
				log.debug("new target: " + actionAndResultPath);
			}

			target = resolveTarget(actionRequest, resultValue);

			if (target == null) {
				targetNotFound(actionRequest, actionAndResultPath);
				return;
			}

			if (log.isDebugEnabled()) {
				log.debug("target found: " + target);
			}

			// store target in cache
			targetCache.put(actionAndResultPath, target);
		}

		// the target exists, continue
		renderView(actionRequest, target);
	}

	/**
	 * Locates the target file from action path and the result value.
	 */
	protected String resolveTarget(ActionRequest actionRequest, String resultValue) {
		String resultBasePath = actionRequest.getActionConfig().getResultBasePath();

		ResultPath resultPath = resultMapper.resolveResultPath(resultBasePath, resultValue);

		String actionPath = resultPath.getPath();
		String path = actionPath;
		String value = resultPath.getValue();

		if (StringUtil.isEmpty(value)) {
			value = null;
		}

		String target;

		while (true) {

			// variant #1: with value
			if (value != null) {
				if (path == null) {
					// only value remains
					int lastSlashNdx = actionPath.lastIndexOf('/');
					if (lastSlashNdx != -1) {
						target = actionPath.substring(0, lastSlashNdx + 1) + value;
					} else {
						target = '/' + value;
					}
				} else {
					target = path + '.' + value;
				}

				target = locateTarget(actionRequest, target);

				if (target != null) {
						break;
				}
			}

			if (path != null) {
				// variant #2: without value

				target = locateTarget(actionRequest, path);

				if (target != null) {
					break;
				}
			}

			// continue

			if (path == null) {
				// path not found
				return null;
			}

			int dotNdx = MadvocUtil.lastIndexOfDotAfterSlash(path);
			if (dotNdx == -1) {
				path = null;
			} else {
				path = path.substring(0, dotNdx);
			}
		}
		return target;
	}

	/**
	 * Locates target from given path. Returns founded target or <code>null</code> if target is not found.
	 * Path may be modified in different ways to form the target. For example, various extensions
	 * may be appended to the path and so on.
	 */
	protected abstract String locateTarget(ActionRequest actionRequest, String path);

	/**
	 * Renders the view by processing founded target.
	 */
	protected abstract void renderView(ActionRequest actionRequest, String target) throws Exception;

	/**
	 * Called when target not found. By default sends 404 to the response.
	 */
	protected void targetNotFound(ActionRequest actionRequest, String actionAndResultPath) throws IOException {
		HttpServletResponse response = actionRequest.getHttpServletResponse();
		response.sendError(SC_NOT_FOUND, "Result not found: " + actionAndResultPath);
	}

}