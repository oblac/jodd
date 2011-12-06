// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.gfx;

import javax.swing.JComponent;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 * Some utilities.
 */
public class GfxUtil {

	public static Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	public static BufferedImage captureScreen() {
		try {
			Robot robot = new Robot();
			return robot.createScreenCapture(new Rectangle(getScreenSize()));
		} catch (AWTException awex) {
			return null;
		}
	}

	public static BufferedImage captureBounds(Rectangle bounds) {
		try {
			Robot robot = new Robot();
			return robot.createScreenCapture(bounds);
		} catch (AWTException awex) {
			return null;
		}
	}

	/**
	 * Captures a specific component only, eg. a JTabbedPane or JPanel.
	 */
	public static BufferedImage captureComponent(JComponent component) {
		Dimension size = component.getSize();
		BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
		component.paint(image.getGraphics());
		return image;
	}

	
}
