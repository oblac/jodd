// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.MadvocException;
import jodd.madvoc.macro.PathMacros;
import jodd.petite.meta.PetiteInject;

/**
 * Create and manage action path macros.
 */
public class ActionPathMacroManager {

	@PetiteInject
	protected MadvocConfig madvocConfig;

	/**
	 * Builds {@link PathMacros action path macros} from given action
	 * path chunks. Returns either <code>null</code>, if
	 * no action path contains no macros, or instance of the <code>PathMacro</code>
	 * implementations.
	 */
	public PathMacros buildActionPathMacros(String actionPath) {
		PathMacros pathMacros = createPathMacro();

		if (pathMacros.init(actionPath) == false) {
			return null;
		}

		return pathMacros;
	}

	/**
	 * Creates new <code>PathMacro</code> instance.
	 */
	protected PathMacros createPathMacro() {
		try {
			return madvocConfig.getPathMacroClass().newInstance();
		} catch (Exception ex) {
			throw new MadvocException("PathMacro class error", ex);
		}
	}

}