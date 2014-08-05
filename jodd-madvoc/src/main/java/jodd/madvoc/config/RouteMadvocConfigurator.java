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
import jodd.typeconverter.Convert;
import jodd.util.ArraysUtil;
import jodd.util.ClassLoaderUtil;
import jodd.util.ReflectUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * {@link jodd.madvoc.config.MadvocConfigurator} that reads
 * routes defined in external file <code>madvoc-routes.txt</code>
 * (name can be changed in @link MadvocConfig).
 */
public class RouteMadvocConfigurator extends ManualMadvocConfigurator {

	@PetiteInject
	protected MadvocConfig madvocConfig;

	/**
	 * Reads Madvoc route configuration file and process it.
	 */
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
	protected Class<? extends ActionWrapper>[] currentWrappers;

	/**
	 * Parses routes file. Splits content into lines.
	 * If line ends with <code>\</code>, it will be joined to the previous line.
	 */
	protected void parse(String routes) throws Exception {
		initDefaultWrappers();

		ArrayList<String> lines = new ArrayList<String>();

		String line = null;

		int start = 0;

		while (start < routes.length()) {
			int ndx = routes.indexOf('\n', start);

			if (ndx == -1) {
				ndx = routes.length();
			}

			String newLine = routes.substring(start, ndx).trim();

			start = ndx + 1;

			boolean join = false;

			if (newLine.endsWith(StringPool.BACK_SLASH)) {
				newLine = StringUtil.substring(newLine, 0, -1);
				join = true;
			}

			if (line == null) {
				line = newLine;
			} else {
				line += newLine;
			}

			if (join) {
				continue;
			}

			if (!StringUtil.isEmpty(line)) {
				lines.add(line);
			}

			line = null;
		}

		for (String aline : lines) {
			parseLine(aline);
		}
	}

	/**
	 * Initializes default wrappers by storing theirs classes.
	 */
	@SuppressWarnings("unchecked")
	protected void initDefaultWrappers() throws Exception {
		Class<? extends ActionWrapper>[] defaultWrappers = new Class[0];

		wrappers = new HashMap<String, Class<? extends ActionWrapper>>();

		Class<? extends ActionInterceptor>[] defaultWebAppInterceptorClasses
				= madvocConfig.getDefaultInterceptors();

		if (defaultWebAppInterceptorClasses != null) {
			for (Class<? extends ActionWrapper> interceptorClass : defaultWebAppInterceptorClasses) {
				wrappers.put(interceptorClass.getName(), interceptorClass);
			}

			defaultWrappers = ArraysUtil.join(defaultWrappers, defaultWebAppInterceptorClasses);
		}

		Class<? extends ActionFilter>[] defaultWebAppFilterClasses
				= madvocConfig.getDefaultFilters();

		if (defaultWebAppFilterClasses != null) {
			for (Class<? extends ActionFilter> filterClass : defaultWebAppFilterClasses) {
				wrappers.put(filterClass.getName(), filterClass);
			}

			defaultWrappers = ArraysUtil.join(defaultWrappers, defaultWebAppFilterClasses);
		}

		wrapperGroups = new HashMap<String, Class<? extends ActionWrapper>[]>();
		wrapperGroups.put("default", defaultWrappers);
		currentWrappers = defaultWrappers;
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

	protected HashMap<String, Class<? extends ActionWrapper>[]> wrapperGroups;

	/**
	 * Return wrapper classes for given wrapper group. Throws an exception
	 * if group name is unknown.
	 */
	protected Class<? extends ActionWrapper>[] getWrappers(String name) {
		Class<? extends ActionWrapper>[] wrappers = wrapperGroups.get(name);

		if (wrappers == null) {
			throw new MadvocException("Invalid wrappers name: " + name);
		}

		return wrappers;
	}

	/**
	 * Parses single route line. The following rules are applied:
	 * <ul>
	 *     <li>if line starts with <code>#</code>, it's a comment</li>
	 *     <li>if line starts with <code>@</code> it's a group definition (for wrappers)</li>
	 *     <li>group area is defined by <code>[]</code></li>
	 *     <li>paths start with <code>/</code></li>
	 *     <li>flags, like <code>async</code> are defined in words that starts with a <code>#</code></li>
	 *     <li>target class and method is given in this form: <code>className#methodName</code></li>
	 *     <li>classes are defined by single word that ends with <code>.class</code>.
	 *     		This applies only for results, for now.</li>
	 *     <li>alias is defined by any other word.</li>
	 * </ul>
	 */
	protected void parseLine(String line) throws Exception {

		if (line.startsWith(StringPool.AT)) {
			int ndx = line.indexOf('=');
			if (ndx != -1) {
				String groupName = line.substring(1, ndx).trim();
				line = line.substring(ndx + 1).trim();
				Class[] classes = Convert.toClassArray(line);

				wrapperGroups.put(groupName, classes);
				return;
			}
		}

		if (line.startsWith(StringPool.HASH)) {
			// comments
			return;
		}

		if (line.startsWith(StringPool.LEFT_SQ_BRACKET) && line.endsWith(StringPool.RIGHT_SQ_BRACKET)) {
			// wrappers groups
			line = StringUtil.substring(line, 1, -1);

			currentWrappers = getWrappers(line);
			return;
		}

		// action mapping

		String[] chunks = StringUtil.splitc(line, " \t");

		ActionBuilder action = action();
		Class<? extends ActionWrapper>[] wrappers = currentWrappers;

		for (String chunk : chunks) {
			// detect pre/suf-fixes, remove them
			for (int i = 0; i < IGNORED_FIXES.length; i += 2) {
				String left = IGNORED_FIXES[i];
				String right = IGNORED_FIXES[i + 1];
				if (chunk.startsWith(left) && chunk.endsWith(right)) {
					chunk = StringUtil.substring(chunk, left.length(), -right.length());
					break;
				}
			}

			// paths (starts with '/')
			if (chunk.startsWith(StringPool.SLASH)) {
				if (action.isSet()) {
					// result base path is not the last path
					action.resultBase(chunk);
					continue;
				}

				// action path is first path
				action.path(chunk);
				continue;
			}

			// wrappers group (starts with '@')
			if (chunk.startsWith(StringPool.AT)) {
				String name = chunk.substring(1);
				wrappers = getWrappers(name);
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
					continue;
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
				continue;
			}

			// last remaining chunk is an alias
			action.alias(chunk);
		}

		// process wrappers

		for (Class<? extends ActionWrapper> wrapper : wrappers) {
			if (ReflectUtil.isInterfaceImpl(wrapper, ActionInterceptor.class)) {
				action.interceptBy((Class<? extends ActionInterceptor>) wrapper);
			}
			else if (ReflectUtil.isInterfaceImpl(wrapper, ActionFilter.class)) {
				action.filterBy((Class<? extends ActionFilter>) wrapper);
			}
			else {
				throw new MadvocException("Invalid wrapper: " + wrapper.getName());
			}
		}

		if (action.isSet()) {
			action.bind();
		}
	}

}