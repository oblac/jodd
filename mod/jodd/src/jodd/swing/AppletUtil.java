// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.swing;


import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Misc applet utilities.
 */
public class AppletUtil {

	/**
	 * Creates a title string from the class name:
	 */
	private static String title(Object o) {
		return o.getClass().getSimpleName();
	}


	/**
	 * Runs a frame.
	 */
	public static void run(JFrame frame, int width, int height) {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setVisible(true);
	}
	
	
	/**
	 * Runs an applet.
	 */
	public static void run(JApplet applet, int width, int height) {
		JFrame frame = new JFrame(title(applet));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(applet);
		frame.setSize(width, height);
		applet.init();
		applet.start();
		frame.setVisible(true);
	}

	/**
	 * Runs a panel.
	 */
	public static void run(JPanel panel, int width, int height) {
		JFrame frame = new JFrame(title(panel));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel);
		frame.setSize(width, height);
		frame.setVisible(true);
	}

}
