// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.ActionRequest;

/**
 * Simple interceptor that measures time and prints out information about invoked actions.
 * User may inherit it and change the way message is printed.
 */
public class EchoInterceptor extends ActionInterceptor {

	/**
	 * Measure action invocation time.
	 */
	@Override
	public Object intercept(ActionRequest actionRequest) throws Exception {
		printBefore(actionRequest);
		long startTime = System.currentTimeMillis();
		Object result = null;
		try {
			result = actionRequest.invoke();
		} catch (Exception ex) {
			result = "<exception>";
			throw ex;
		} catch (Throwable th) {
			result = "<throwable>";
			throw new Exception(th);
		} finally {
			long executionTime = System.currentTimeMillis() - startTime;
			printAfter(actionRequest, executionTime, result);
		}
		return result;
	}

	/**
	 * Prints out the message. User can override this method and modify the way
	 * the message is printed.
	 */
	protected void printBefore(ActionRequest request) {
		StringBuilder message = new StringBuilder("----->");
		message.append(request.getActionPath()).append("   [").append(request.getActionConfig().getActionString()).append(']');
		out(message.toString());
	}

	/**
	 * Prints out the message. User can override this method and modify the way
	 * the message is printed.
	 */
	protected void printAfter(ActionRequest request, long executionTime, Object result) {
		StringBuilder message = new StringBuilder("<----- ");
		message.append(request.getActionPath()).append("  (").append(result);
		message.append(") in ").append(executionTime).append("ms.");
		out(message.toString());
	}

	/**
	 * Outputs info message. By default, it outputs it to console.
	 */
	protected void out(String message) {
		System.out.println(message);
	}

}
