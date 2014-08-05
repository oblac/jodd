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
import java.util.Map;

/**
 * {@link jodd.madvoc.config.MadvocConfigurator} that reads
 * routes defined in external file <code>madvoc-routes.txt</code>
 * (name can be changed in @link MadvocConfig).
 * <p>
 * These are the routes rules:
 * <ul>
 *     <li>if line ends with <code>\</code>, it continues to the next line.</li>
 *     <li>if line starts with <code>#</code>, it's a comment and it is ignored.</li>
 *     <li>if line starts with <code>@</code> and it contains a <code>=</code> sign,
 *     		it's a variable definition.
 *     		Variables are processed as they appear in the file.</li>
 *     <li>default area is defined by <code>[]</code></li>. Default area defines section
 *     		with the same wrappers.
 *     <li>paths start with <code>/</code>. Action path is first such path
 *     		and result path is last such path.
 *     </li>
 *     <li>flags, like <code>async</code> are defined in words that starts with a <code>#</code>.</li>
 *     <li>target class and method is given in this form: <code>className#methodName</code>.</li>
 *     <li>classes are defined by single word that ends with <code>.class</code>. If class is
 *     		a result class, everything else will be ignored. If class is a wrapper, it overrides
 *     		default wrappers.</li>
 *     <li>alias is defined by last unprocessed word.</li>
 * </ul>
 */
public class RouteMadvocConfigurator extends ManualMadvocConfigurator {

	@PetiteInject
	protected MadvocConfig madvocConfig;

	protected HashMap<String, String> variables;
	protected Class<? extends ActionWrapper>[] currentWrappers;

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
		String defaultWrappers = "";

		variables = new HashMap<String, String>();

		Class<? extends ActionInterceptor>[] defaultWebAppInterceptorClasses
				= madvocConfig.getDefaultInterceptors();

		if (defaultWebAppInterceptorClasses != null) {
			for (Class<? extends ActionWrapper> interceptorClass : defaultWebAppInterceptorClasses) {
				defaultWrappers += interceptorClass.getName();
				defaultWrappers += ".class ";
			}
		}

		Class<? extends ActionFilter>[] defaultWebAppFilterClasses
				= madvocConfig.getDefaultFilters();

		if (defaultWebAppFilterClasses != null) {
			for (Class<? extends ActionFilter> filterClass : defaultWebAppFilterClasses) {
				defaultWrappers += filterClass.getName();
				defaultWrappers += ".class ";
			}
		}

		defaultWrappers = defaultWrappers.trim();

		variables.put("default.wrappers", defaultWrappers);

		processDefaults(defaultWrappers);
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

		// variable definition
		if (line.startsWith(StringPool.AT)) {
			int ndx = line.indexOf('=');
			if (ndx != -1) {
				String groupName = line.substring(1, ndx).trim();
				line = line.substring(ndx + 1).trim();

				variables.put(groupName, line);
				return;
			}
		}

		// comments
		if (line.startsWith(StringPool.HASH)) {
			return;
		}

		// variable replacer
		line = replaceVariables(line);

		// defaults
		if (line.startsWith(StringPool.LEFT_SQ_BRACKET) && line.endsWith(StringPool.RIGHT_SQ_BRACKET)) {
			line = StringUtil.substring(line, 1, -1).trim();

			processDefaults(line);

			return;
		}

		// action mapping

		String[] chunks = StringUtil.splitc(line, " \t");

		ActionBuilder action = action();

		Class<? extends ActionWrapper>[] wrappers = new Class[] {};

		for (String chunk : chunks) {
			chunk = trimPrefixAndSuffix(chunk);

			if (StringUtil.isBlank(chunk)) {
				continue;
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
				Class chunkClass = Convert.toClass(chunk);

				// detect result class
				if (ReflectUtil.isInterfaceImpl(chunkClass, ActionResult.class)) {
					result(chunkClass);
					continue;
				}

				// detect wrapper
				if (ReflectUtil.isInterfaceImpl(chunkClass, ActionWrapper.class)) {
					wrappers = ArraysUtil.append(wrappers, chunkClass);
					continue;
				}

				throw new MadvocException("Unsupported type: " + chunk);
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

			// last remaining unprocessed chunk is an alias
			action.alias(chunk);
		}

		// process wrappers

		if (wrappers.length == 0) {
			wrappers = currentWrappers;
		}


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

	/**
	 * Removes common prefixes and suffixes.
	 */
	protected String trimPrefixAndSuffix(String chunk) {
		for (int i = 0; i < IGNORED_FIXES.length; i += 2) {
			String left = IGNORED_FIXES[i];
			String right = IGNORED_FIXES[i + 1];
			if (chunk.startsWith(left) && chunk.endsWith(right)) {
				chunk = StringUtil.substring(chunk, left.length(), -right.length());
				break;
			}
		}
		return chunk;
	}

	/**
	 * Parses the line and sets the current defaults.
	 */
	protected void processDefaults(String line) {
		String[] chunks = StringUtil.splitc(line, " \t");

		Class<? extends ActionWrapper>[] wrappers = new Class[]{};

		for (String chunk : chunks) {
			chunk = trimPrefixAndSuffix(chunk);

			if (StringUtil.isBlank(chunk)) {
				continue;
			}

			Class type = Convert.toClass(chunk);

			// check wrappers
			if (ReflectUtil.isInterfaceImpl(type, ActionWrapper.class)) {
				wrappers = ArraysUtil.append(wrappers, type);
			}
		}

		if (wrappers.length != 0) {
			currentWrappers = wrappers;
		}
	}

	/**
	 * Replaces variables in the line.
	 */
	protected String replaceVariables(String line) {
		for (Map.Entry<String, String> entry : variables.entrySet()) {
			String name = "@" + entry.getKey();
			String value = entry.getValue();

			line = StringUtil.replace(line, name, value);
		}

		return line;
	}

}