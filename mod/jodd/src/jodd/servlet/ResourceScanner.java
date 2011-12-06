// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.util.StringUtil;
import jodd.util.StringPool;

import javax.servlet.ServletContext;
import java.util.Set;
import java.io.InputStream;

/**
 * Web resources scanner.
 */
public abstract class ResourceScanner {

	protected static final String WEB_INF_PATH = "/WEB-INF/";

	protected final ServletContext servletContext;

	protected ResourceScanner(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	// ---------------------------------------------------------------- config

	protected boolean ignoreWebInf;

	public boolean isIgnoreWebInf() {
		return ignoreWebInf;
	}

	public void setIgnoreWebInf(boolean ignoreWebInf) {
		this.ignoreWebInf = ignoreWebInf;
	}

	public ResourceScanner ignoreWebInf(boolean ignoreWebibnf) {
		this.ignoreWebInf = ignoreWebibnf;
		return this;
	}


	protected boolean recursive;

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	/**
	 * Activates recursive search.
	 */
	public ResourceScanner recursive(boolean recursive) {
		this.recursive = recursive;
		return this;
	}


	protected boolean includeDirs = true;

	public boolean isIncludeDirs() {
		return includeDirs;
	}

	public void setIncludeDirs(boolean includeDirs) {
		this.includeDirs = includeDirs;
	}

	/**
	 * Include directories in search.
	 */
	public ResourceScanner includeDirs(boolean includeDirs) {
		this.includeDirs = includeDirs;
		return this;
	}



	protected boolean includeFiles = true;

	public boolean isIncludeFiles() {
		return includeFiles;
	}

	public void setIncludeFiles(boolean includeFiles) {
		this.includeFiles = includeFiles;
	}

	/**
	 * Include files in search.
	 */
	public ResourceScanner includeFiles(boolean includeFiles) {
		this.includeFiles = includeFiles;
		return this;
	}

	/**
	 * Include files and folders.
	 */
	public ResourceScanner includeFilesAndFolders() {
		includeDirs = true;
		includeFiles = true;
		return this;
	}


	// ---------------------------------------------------------------- scan

	/**
	 * Starts resource scanning from the root.
	 */
	public void scan() {
		scan(StringPool.SLASH);
	}

	/**
	 * Starts resource scanning.
	 */
	@SuppressWarnings({"unchecked"})
	public void scan(String startPath) {
		Set<String> allpaths = servletContext.getResourcePaths(startPath);
		if (allpaths == null) {
			return;
		}
		for(String path : allpaths) {
			process(path);
		}
	}

	// ---------------------------------------------------------------- process

	/**
	 * Processes single path.
	 */
	protected void process(String path) {
		boolean isFolder = StringUtil.endsWithChar(path, '/');
		if (isFolder) {
			if (ignoreWebInf && StringUtil.startsWithIgnoreCase(path, WEB_INF_PATH)) {
				return;
			}
			if (includeDirs == true) {
				onResource(path, isFolder);
			}
			if (recursive && acceptFolder(path)) {
				scan(path);
			}
			return;
		}
		if (includeFiles == true) {
			onResource(path, isFolder);
		}
	}

	/**
	 * Returns <code>true</code> if some path has to be scanned.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected boolean acceptFolder(String path) {
		return true;
	}

	/**
	 * Invoked on some resource.
	 */
	protected abstract void onResource(String path, boolean isFolder);

	/**
	 * Returns input stream of a resource or <code>null</code> if resource is not valid for reading.
	 */
	protected InputStream openResource(String path) {
		return servletContext.getResourceAsStream(path);
	}

	/**
	 * Returns just resource name.
	 */
	protected String getResourceName(String path) {
		int ndx = path.lastIndexOf('/');
		if (ndx == -1) {
			return path;
		}
		return path.substring(ndx);
	}

}