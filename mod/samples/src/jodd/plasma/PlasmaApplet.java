// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.plasma;

import jodd.gfx.GfxPanel;
import jodd.gfx.runners.GfxAppletRunner;

public class PlasmaApplet extends GfxAppletRunner {
	@Override
	public GfxPanel createGfxPanel() {
		return new Plasma();
	}
}
