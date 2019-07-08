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

import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.madvoc.MadvocUtil;
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.config.ResultPath;
import jodd.petite.meta.PetiteInject;
import jodd.util.StringUtil;

/**
 * Mapper from action results paths to result path. Certain set of results
 * defines path where to forward/redirect etc. This mapper converts
 * result path to real path. Result path may contain some macros that
 * will be resolved. Here are the macros that can be used:
 * <ul>
 *     <li>&lt;alias&gt; - replaced with alias value</li>
 *     <li># - strips words from path (goes 'back')</li>
 * </ul>
 */
public class ResultMapper extends ResultMapperCfg {

	private static final Logger log = LoggerFactory.getLogger(ResultMapper.class);

	@PetiteInject
	protected ActionsManager actionsManager;

	/**
	 * Lookups value as an alias and, if not found, as a default alias.
	 */
	protected String lookupAlias(final String alias) {
		String value = actionsManager.lookupPathAlias(alias);
		if (value == null) {
			ActionRuntime cfg = actionsManager.lookup(alias);
			if (cfg != null) {
				value = cfg.getActionPath();
			}
		}
		return value;
	}

	/**
	 * Returns resolved alias result value or passed on, if alias doesn't exist.
	 */
	protected String resolveAlias(final String value) {
		final StringBuilder result = new StringBuilder(value.length());
		int i = 0;
		int len = value.length();
		while (i < len) {
			int ndx = value.indexOf('<', i);
			if (ndx == -1) {
				// alias markers not found
				if (i == 0) {
					// try whole string as an alias
					String alias = lookupAlias(value);
					return (alias != null ? alias : value);
				} else {
					result.append(value.substring(i));
				}
				break;
			}

			// alias marked found
			result.append(value.substring(i, ndx));
			ndx++;
			int ndx2 = value.indexOf('>', ndx);
			String aliasName = (ndx2 == -1 ? value.substring(ndx) : value.substring(ndx, ndx2));

			// process alias
			String alias = lookupAlias(aliasName);
			if (alias != null) {
				result.append(alias);
			}
			else {
				// alias not found
				if (log.isWarnEnabled()) {
					log.warn("Alias not found: " + aliasName);
				}
			}
			i = ndx2 + 1;
		}

		// fix prefix '//' - may happened when aliases are used
		i = 0; len = result.length();
		while (i < len) {
			if (result.charAt(i) != '/') {
				break;
			}
			i++;
		}
		if (i > 1) {
			return result.substring(i - 1, len);
		}
		return result.toString();
	}

	/**
	 * Resolves result path.
	 */
	public ResultPath resolveResultPath(String path, String value) {

		boolean absolutePath = false;

		if (value != null) {
			// [*] resolve alias in value
			value = resolveAlias(value);

			// [*] absolute paths
			if (StringUtil.startsWithChar(value, '/')) {
				absolutePath = true;
				int dotNdx = value.indexOf("..");
				if (dotNdx != -1) {
					path = value.substring(0, dotNdx);
					value = value.substring(dotNdx + 2);
				} else {
					path = value;
					value = null;
				}
			} else {
				// [*] resolve # in value and path
				int i = 0;
				while (i < value.length()) {
					if (value.charAt(i) != '#') {
						break;
					}
					int dotNdx = MadvocUtil.lastIndexOfSlashDot(path);
					if (dotNdx != -1) {
						// dot found
						path = path.substring(0, dotNdx);
					}
					i++;
				}
				if (i > 0) {
					// remove # from value
					value = value.substring(i);

					// [*] update path and value

					if (StringUtil.startsWithChar(value, '.')) {
						value = value.substring(1);
					} else {
						int dotNdx = value.indexOf("..");
						if (dotNdx != -1) {
							path += '.' + value.substring(0, dotNdx);
							value = value.substring(dotNdx + 2);
						} else {
							if (value.length() > 0) {
								if (StringUtil.endsWithChar(path, '/')) {
									path += value;
								} else {
									path += '.' + value;
								}
							}
							value = null;
						}
					}
				}
			}
		}

		if (!absolutePath) {
			if (resultPathPrefix != null) {
				path = resultPathPrefix + path;
			}
		}

		return new ResultPath(path, value);
	}

	/**
	 * Resolves result path as a string, when parts are not important
	 * and when only full string matters. Additional alias resolving
	 * on full path is done.
	 */
	public String resolveResultPathString(final String path, final String value) {
		final ResultPath resultPath = resolveResultPath(path, value);
		final String result = resultPath.pathValue();

		return resolveAlias(result);
	}

}