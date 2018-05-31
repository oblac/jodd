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

import jodd.madvoc.MadvocException;
import jodd.util.ArraysUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Collection of root packages.
 */
public class RootPackages {

	protected String[] packages;
	protected String[] mappings;

	protected Map<String, String> packagePaths;     // cache: package -> path

	/**
	 * Adds root package with no additional mapping.
	 */
	public void addRootPackage(final String rootPackage) {
		addRootPackage(rootPackage, StringPool.EMPTY);
	}

	/**
	 * Sets root package to package of given class.
	 */
	public void addRootPackageOf(final Class actionClass) {
		addRootPackageOf(actionClass, StringPool.EMPTY);
	}

	/**
	 * Adds root package and its path mapping. Duplicate root packages
	 * are ignored, if mapping path is equals, otherwise exception is thrown.
	 */
	public void addRootPackage(final String rootPackage, String mapping) {
		if (packages == null) {
			packages = new String[0];
		}
		if (mappings == null) {
			mappings = new String[0];
		}

		// fix mapping
		if (mapping.length() > 0) {
			// mapping must start with the slash
			if (!mapping.startsWith(StringPool.SLASH)) {
				mapping = StringPool.SLASH + mapping;
			}
			// mapping must NOT end with the slash
			if (mapping.endsWith(StringPool.SLASH)) {
				mapping = StringUtil.substring(mapping, 0, -1);
			}
		}

		// detect duplicates
		for (int i = 0; i < packages.length; i++) {
			if (packages[i].equals(rootPackage)) {
				if (mappings[i].equals(mapping)) {
					// both package and the mappings are the same
					return;
				}
				throw new MadvocException("Different mappings for the same root package: " + rootPackage);
			}
		}

		packages = ArraysUtil.append(packages, rootPackage);
		mappings = ArraysUtil.append(mappings, mapping);
	}

	/**
	 * Sets root package to package of given class.
	 */
	public void addRootPackageOf(final Class actionClass, final String mapping) {
		addRootPackage(actionClass.getPackage().getName(), mapping);
	}

	// ---------------------------------------------------------------- find

	/**
	 * Finds closest root package for the given action path.
	 */
	public String findRootPackageForActionPath(final String actionPath) {
		if (mappings == null) {
			return null;
		}

		int ndx = -1;
		int delta = Integer.MAX_VALUE;

		for (int i = 0; i < mappings.length; i++) {
			String mapping = mappings[i];

			boolean found = false;
			if (actionPath.equals(mapping)) {
				found = true;
			} else {
				mapping += StringPool.SLASH;
				if (actionPath.startsWith(mapping)) {
					found = true;
				}
			}

			if (found) {
				int distance = actionPath.length() - mapping.length();
				if (distance < delta) {
					ndx = i;
					delta = distance;
				}
			}
		}

		if (ndx == -1) {
			return null;
		}

		return packages[ndx];
	}

	/**
	 * Finds mapping for given action class. Returns <code>null</code>
	 * if no mapping is found. If there is more then one matching root
	 * package, the closest one will be returned.
	 */
	public String findPackagePathForActionPackage(final String actionPackage) {
		if (packages == null) {
			return null;
		}
		if (packagePaths == null) {
			packagePaths = new HashMap<>();
		}

		String packagePath = packagePaths.get(actionPackage);
		if (packagePath != null) {
			return packagePath;
		}

		int ndx = -1;
		int delta = Integer.MAX_VALUE;

		for (int i = 0; i < packages.length; i++) {
			String rootPackage = packages[i];

			if (rootPackage.equals(actionPackage)) {
				// exact match
				ndx = i;
				delta = 0;
				break;
			}

			rootPackage += '.';

			if (actionPackage.startsWith(rootPackage)) {
				// found, action package contains root package
				int distanceFromTheRoot = actionPackage.length() - rootPackage.length();

				if (distanceFromTheRoot < delta) {
					ndx = i;
					delta = distanceFromTheRoot;
				}
			}
		}

		if (ndx == -1) {
			return null;
		}

		String packageActionPath = delta == 0 ? StringPool.EMPTY : StringUtil.substring(actionPackage, - delta - 1, 0);

		packageActionPath = packageActionPath.replace('.', '/');

		String result = mappings[ndx] + packageActionPath;

		packagePaths.put(actionPackage, result);

		return result;
	}

	// ---------------------------------------------------------------- toString

	@Override
	public String toString() {
		if (packages == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < packages.length; i++) {
			String rootPackage = packages[i];
			String mapping = mappings[i];

			sb.append(rootPackage).append(" --> ").append(mapping).append(StringPool.NEWLINE);
		}

		return sb.toString();
	}

}