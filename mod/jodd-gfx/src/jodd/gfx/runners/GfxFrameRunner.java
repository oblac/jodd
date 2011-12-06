// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.gfx.runners;

import jodd.gfx.GfxPanel;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 * Runs GfxPanel as stand-alone application.
 * Also, communication to other controls should goes through this class.
 */
public abstract class GfxFrameRunner extends JFrame implements WindowListener {

	// ---------------------------------------------------------------- WindowsListener

	public void windowActivated(WindowEvent e) {
		gfxPanel.resume();
	}

	public void windowDeactivated(WindowEvent e) {
		gfxPanel.pause();
	}


	public void windowDeiconified(WindowEvent e) {
		gfxPanel.resume();
	}

	public void windowIconified(WindowEvent e) {
		gfxPanel.pause();
	}


	public void windowClosing(WindowEvent e) {
		gfxPanel.stop();
	}

	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}

	// ---------------------------------------------------------------- run

	public GfxPanel gfxPanel;

	/**
	 * Runs GfxPanel. It calls <code>initGui()</code> for defining main frame GUI.
	 */
	public final void run(GfxPanel gfxPanel) {
		this.gfxPanel = gfxPanel;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(this);
		initGUI();
		gfxPanel.initialize();
		setTitle(gfxPanel.name);
		setSize(gfxPanel.width, gfxPanel.height);
		pack();
		setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - this.getWidth()) / 2, (screenSize.height - this.getHeight()) / 2);
		gfxPanel.start();
		setVisible(true);
	}

	/**
	 * Defines default GUI: just GfxPanel in the center.
	 */
	public void initGUI() {
		Container c = getContentPane();    // default BorderLayout used
		c.add(gfxPanel, "Center");
	}
}
