// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.MadvocException;
import jodd.madvoc.macro.PathMacro;
import jodd.petite.meta.PetiteInject;

/**
 * Create and manage action path macros.
 */
public class ActionPathMacroManager {

	@PetiteInject
	protected MadvocConfig madvocConfig;


	/**
	 * Builds action path macros from given action path chunks.
	 * Resulting array is either <code>null</code>, if no chunk contains
	 * a macro, or array of the same size, with <code>PathMacro</code>
	 * implementations on indexes where path macro chunk is detected.
	 */
	public PathMacro[] buildActionPathMacros(String[] chunks) {
		PathMacro[] pathMacros = new PathMacro[chunks.length];

		int macroCount = 0;

		for (int i = 0; i < chunks.length; i++) {
			String chunk = chunks[i];

			PathMacro pathMacro = createPathMacro();

			if (pathMacro.init(chunk)) {
				macroCount++;

				pathMacros[i] = pathMacro;
			}
		}

		if (macroCount == 0) {
			return null;
		}

		return pathMacros;
	}

	/**
	 * Creates new <code>PathMacro</code> instance.
	 */
	protected PathMacro createPathMacro() {
		try {
			return madvocConfig.getPathMacroClass().newInstance();
		} catch (Exception ex) {
			throw new MadvocException("Unable to create Madvoc path macro class.", ex);
		}
	}

}
