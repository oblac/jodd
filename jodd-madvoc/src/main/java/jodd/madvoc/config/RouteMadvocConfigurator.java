// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.config;

import jodd.io.StreamUtil;
import jodd.madvoc.ActionWrapper;
import jodd.madvoc.MadvocException;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.meta.Action;
import jodd.madvoc.result.ActionResult;
import jodd.petite.meta.PetiteInject;
import jodd.util.ClassLoaderUtil;
import jodd.util.ReflectUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * {@link jodd.madvoc.config.MadvocConfigurator} that reads
 * routes defined in external file <code>madvoc-routes.txt</code> (name can be
 * changed).
 */
public class RouteMadvocConfigurator extends ManualMadvocConfigurator {

	@PetiteInject
	protected MadvocConfig madvocConfig;

	public void configure() {
		String fileName = madvocConfig.getRoutesFileName();

		URL url = ClassLoaderUtil.getResourceUrl("/" + fileName);

		if (url == null) {
			throw new MadvocException("Routes file not found: " + fileName);
		}

		InputStream in = null;
		String routes;
		try {
			in = url.openStream();

			char[] chars = StreamUtil.readChars(in, StringPool.UTF_8);
			routes = new String(chars);
		}
		catch (IOException ioex) {
			throw new MadvocException(ioex);
		}
		finally {
			StreamUtil.close(in);
		}

		try {
			parse(routes);
		} catch (Exception ex) {
			throw new MadvocException(ex);
		}
	}

	protected HashMap<String, Class<? extends ActionWrapper>> wrappers;
	protected List<String> currentWrappers;

	/**
	 * Parses routes file.
	 */
	protected void parse(String routes) throws Exception {
		initDefaultWrappers();

		String[] lines = StringUtil.split(routes, StringPool.NEWLINE);

		for (String line : lines) {
			line = line.trim();

			if (StringUtil.isEmpty(line)) {
				continue;
			}

			parseLine(line);
		}
	}

	/**
	 * Initializes default wrappers by storing theirs classes names.
	 */
	protected void initDefaultWrappers() throws Exception {
		wrappers = new HashMap<String, Class<? extends ActionWrapper>>();
		currentWrappers = new ArrayList<String>();

		Class<? extends ActionInterceptor>[] defaultWebAppInterceptorClasses
				= madvocConfig.getDefaultInterceptors();

		if (defaultWebAppInterceptorClasses != null) {
			for (Class<? extends ActionWrapper> interceptorClass : defaultWebAppInterceptorClasses) {
				currentWrappers.add(interceptorClass.getName());
				wrappers.put(interceptorClass.getName(), interceptorClass);
			}
		}

		Class<? extends ActionFilter>[] defaultWebAppFilterClasses
				= madvocConfig.getDefaultFilters();

		if (defaultWebAppFilterClasses != null) {
			for (Class<? extends ActionFilter> filterClass : defaultWebAppFilterClasses) {
				currentWrappers.add(filterClass.getName());
				wrappers.put(filterClass.getName(), filterClass);
			}
		}
	}

	protected String[] IGNORED_FIXES = new String[] {
			StringPool.QUOTE, StringPool.QUOTE,
			StringPool.SINGLE_QUOTE, StringPool.SINGLE_QUOTE,
			StringPool.BACKTICK, StringPool.BACKTICK,
			StringPool.LEFT_BRACE, StringPool.RIGHT_BRACE,
			StringPool.LEFT_BRACKET, StringPool.RIGHT_BRACKET,
			StringPool.LEFT_SQ_BRACKET, StringPool.RIGHT_SQ_BRACKET,
			StringPool.EMPTY, StringPool.COMMA,
			StringPool.EMPTY, StringPool.SEMICOLON,
			StringPool.EMPTY, StringPool.COLON,
	};

	/**
	 * Parses single route line.
	 */
	protected void parseLine(String line) throws Exception {
		if (line.startsWith(StringPool.LEFT_SQ_BRACKET) && line.endsWith(StringPool.RIGHT_SQ_BRACKET)) {
			// interceptors and filters
			line = StringUtil.substring(line, 1, -1);

			currentWrappers.clear();

			String[] wrapperNames = StringUtil.splitc(line, ',');

			for (String wrapperName : wrapperNames) {
				wrapperName = wrapperName.trim();

				currentWrappers.add(wrapperName);
			}

			return;
		}

		// action mapping

		String[] chunks = StringUtil.splitc(line, " \t");

		ActionBuilder action = action();

		for (String chunk : chunks) {
			// alias (between "<" and ">")
			if (chunk.startsWith(StringPool.LEFT_CHEV) && chunk.endsWith(StringPool.RIGHT_CHEV)) {
				String alias = StringUtil.substring(chunk, 1, -1);
				action.alias(alias);
				continue;
			}

			// detect pre/suf-fixes, remove them
			for (int i = 0; i < IGNORED_FIXES.length; i += 2) {
				String left = IGNORED_FIXES[i];
				String right = IGNORED_FIXES[i + 1];
				if (chunk.startsWith(left) && chunk.endsWith(right)) {
					chunk = StringUtil.substring(chunk, left.length(), -right.length());
					break;
				}
			}

			// action path (starts with '/')
			if (chunk.startsWith(StringPool.SLASH)) {
				action.path(chunk);
				continue;
			}

			// flag (starts with '#')
			if (chunk.startsWith(StringPool.HASH)) {
				String flag = chunk.substring(1);

				// async flag
				if (flag.equals("async")) {
					action.async(true);
					continue;
				}
			}

			// class#method (has '#')
			if (chunk.contains(StringPool.HASH)) {
				String[] names = StringUtil.splitc(chunk, '#');
				if (names.length != 2) {
					continue;
				}

				String className = names[0];

				Class actionClass = ClassLoaderUtil.loadClass(className);

				action.mapTo(actionClass, names[1]);
				continue;
			}

			// class name (ends with ".class")
			if (chunk.endsWith(".class")) {
				String className = StringUtil.substring(chunk, 0, -6);

				Class chunkClass = ClassLoaderUtil.loadClass(className);

				// detect result class
				if (ReflectUtil.isInterfaceImpl(chunkClass, ActionResult.class)) {
					result(chunkClass);
				}
			}

			// http method
			if (	chunk.equals(Action.GET) ||
					chunk.equals(Action.HEAD) ||
					chunk.equals(Action.POST) ||
					chunk.equals(Action.PUT) ||
					chunk.equals(Action.DELETE) ||
					chunk.equals(Action.TRACE) ||
					chunk.equals(Action.OPTIONS) ||
					chunk.equals(Action.CONNECT) ||
					chunk.equals(Action.PATCH)
				) {

				action.httpMethod(chunk);
			}

			// continue on anything else, we are not strict
		}

		for (String currentWrapper : currentWrappers) {
			Class<? extends ActionWrapper> wrapper = getWrapperClass(currentWrapper);

			if (ReflectUtil.isInterfaceImpl(wrapper, ActionInterceptor.class)) {
				action.interceptBy((Class<? extends ActionInterceptor>) wrapper);
			}
			else if (ReflectUtil.isInterfaceImpl(wrapper, ActionFilter.class)) {
				action.filterBy((Class<? extends ActionFilter>) wrapper);
			}
			else {
				throw new MadvocException("Invalid wrapper: " + currentWrapper);
			}
		}

		if (action.isSet()) {
			action.bind();
		}
	}

	/**
	 * Returns wrapper class for given name.
	 */
	@SuppressWarnings("unchecked")
	protected Class<? extends ActionWrapper> getWrapperClass(String className) throws Exception {
		Class<? extends ActionWrapper> actionWrapperClass = wrappers.get(className);

		if (actionWrapperClass == null) {
			actionWrapperClass = ClassLoaderUtil.loadClass(className);

			wrappers.put(className, actionWrapperClass);
		}

		return actionWrapperClass;
	}
}