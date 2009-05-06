package jodd.gfx.runners;

import jodd.gfx.GfxPanel;
import jodd.gfx.StringConvert;

import javax.swing.JApplet;
import java.awt.Container;

/**
 * Applet runner for GfxPanel.
 */
public abstract class AppletRunner extends JApplet {

	public GfxPanel gfxPanel;

	public abstract GfxPanel createGfxPanel();

	@Override
	public void init() {
		gfxPanel = createGfxPanel();
		gfxPanel.doInit();
		initGUI();
		gfxPanel.start();
	}

	/**
	 * Defines default GUI: just GfxPanel in the ceter.
	 */
	public void initGUI() {
		Container c = getContentPane();    // default BorderLayout used
		c.add(gfxPanel, "Center");
	}

	// ---------------------------------------------------------------- applet life cycle methods

	@Override
	public void start() {
		gfxPanel.resume();
	}

	@Override
	public void stop() {
		gfxPanel.pause();
	}

	@Override
	public void destroy() {
		gfxPanel.stop();
	}

	// ---------------------------------------------------------------- parameters
	
	/**
	 * Returns float value of some HTML parameter
	 *
	 * @param name				parameter name
	 * @param default_value		default value in case of error
	 * @return					float parameter value
	 */
	public float getParameterFloat(String name, float default_value) {
		float f = default_value;
		try {
			f = StringConvert.toFloat(getParameter(name), f);
		} catch (Exception ex) {
			// ignore
		}
		return f;
	}

	/**
	 * Returns int value of some HTML parameter
	 *
	 * @param name				parameter name
	 * @param default_value		default value in case of error
	 * @return					int parameter value
	 */
	public int getParameterInt(String name, int default_value) {
		int i = default_value;
		try {
			i = StringConvert.toInt(getParameter(name), i);
		} catch (Exception ex) {
			// ignore
		}
		return i;
	}
}
