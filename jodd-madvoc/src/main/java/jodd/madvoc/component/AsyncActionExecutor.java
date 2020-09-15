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

package jodd.madvoc.component;

import jodd.exception.ExceptionUtil;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Thread pool and executor for Async actions.
 */
public class AsyncActionExecutor extends AsyncActionExecutorCfg {

	private static final Logger log = LoggerFactory.getLogger(AsyncActionExecutor.class);

	protected ExecutorService executorService;

	public void start() {
		executorService = new ThreadPoolExecutor(
			corePoolSize,
			maximumPoolSize,
			keepAliveTimeMillis,
			TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<>(queueCapacity));
	}

	/**
	 * Invokes an action asynchronously by submitting it to the thread pool.
	 */
	public void invoke(final ActionRequest actionRequest) {
		if (executorService == null) {
			throw new MadvocException("No action is marked as async!");
		}

		final HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();

		log.debug("Async call to: " + actionRequest);

		final AsyncContext asyncContext = servletRequest.startAsync();

		executorService.submit(() -> {
			try {
				actionRequest.invoke();
			} catch (final Exception ex) {
				log.error("Invoking async action path failed: " , ExceptionUtil.unwrapThrowable(ex));
			} finally {
				asyncContext.complete();
			}
		});
	}
}
