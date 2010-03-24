// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.plasma;

import jodd.gfx.runners.GfxFrameRunner;

/**
 * Plasma applet.
 */
public class PlasmaFrame extends GfxFrameRunner {

	public static void main(String args[]) {
		new PlasmaFrame().run(new Plasma());
	}

}
