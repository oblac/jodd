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

package jodd.swingspy;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * SwingSpy.
 */
public class SwingSpy {

	private static final int MOUSE_HOTKEY = MouseEvent.SHIFT_DOWN_MASK | MouseEvent.CTRL_DOWN_MASK | MouseEvent.BUTTON1_DOWN_MASK;
	private static SwingSpy spy;
	protected JDialog spyDialog;
	protected SwingSpyPanel spyPanel;
	protected SwingSpyGlassPane spyGlass;

	public SwingSpy() {
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			/**
			 * Mouse hot-key combination clicked.
			 */
			public void eventDispatched(AWTEvent awtEvent) {
				MouseEvent event = (MouseEvent) awtEvent;
				if ((event.getModifiersEx() == MOUSE_HOTKEY) && (event.getClickCount() == 1)) {
					showSpyDialog((Component) event.getSource());
					event.consume();
				}
			}
		}, AWTEvent.MOUSE_EVENT_MASK);
		printUsageMessage();
	}

	/**
	 * Print SwingSpy usage message.
	 */
	protected void printUsageMessage() {
		System.out.println("SHIFT-CTRL-CLICK on any component to activate.");
	}

	/**
	 * Installs the SwingSpy globally if not already installed.
	 */
	public static void install() {
		if (spy == null) {
			System.out.print("SwingSpy has been installed. ");
			spy = new SwingSpy();
		} else {
			System.out.println("SwingSpy already installed.");
		}
	}

	/**
	 * Shows spy dialog on selected component.
	 */
	public void showSpyDialog(final Component component) {
		showSpyDialog(SwingUtilities.getRoot(component), component);
	}


	/**
	 * Shows spy dialog or reload existing one.
	 *
	 * @param rootComponent root component
	 * @param component current component
	 */
	public void showSpyDialog(final Component rootComponent, final Component component) {
		if (spyDialog != null) {
			spyDialog.setVisible(true);
			spyGlass.setVisible(true);
			spyPanel.reload(rootComponent, component);
			return;
		}
		if (rootComponent instanceof RootPaneContainer) {
			RootPaneContainer rootPane = (RootPaneContainer) rootComponent;
			spyGlass = new SwingSpyGlassPane(rootPane);
			rootPane.setGlassPane(spyGlass);
			rootPane.getGlassPane().setVisible(true);
			Toolkit.getDefaultToolkit().addAWTEventListener(spyGlass, AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initSpyDialog(rootComponent, component);
			}
		});
	}

	/**
	 * Initializes Spy dialog.
	 */
	protected void initSpyDialog(Component rootComponent, Component component) {
		if (rootComponent instanceof Dialog) {
			spyDialog = new CaddyDialog((Dialog) rootComponent) {
				@Override
				protected JRootPane createRootPane() {
					return createSpyRootPane();
				}
			};
		} else if (rootComponent instanceof Frame) {
			spyDialog = new CaddyDialog((Frame) rootComponent) {
				@Override
				protected JRootPane createRootPane() {
					return createSpyRootPane();
				}
			};
		} else {
			spyDialog = new JDialog() {
				@Override
				protected JRootPane createRootPane() {
					return createSpyRootPane();
				}
			};
		}
		spyDialog.setName("SwingSpy");
		spyDialog.setTitle("SwingSpy");
		spyDialog.setModal(false);
		spyDialog.setAlwaysOnTop(true);
		Container contentPane = spyDialog.getContentPane();
		contentPane.setLayout(new BorderLayout());
		spyPanel = new SwingSpyPanel();
		spyPanel.reload(rootComponent, component);
		contentPane.add(spyPanel);
		spyDialog.pack();

		spyDialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosed(e);
				spyGlass.setVisible(false);
				spyDialog = null;
			}
		});
		spyDialog.setLocationRelativeTo(null);
		spyDialog.setVisible(true);
	}

	/**
	 * Creates spy root pane for spy dialog.
	 */
	protected JRootPane createSpyRootPane() {
		JRootPane rootPane = new JRootPane();
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		rootPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				spyGlass.setVisible(false);
				spyDialog.setVisible(false);
			}
		}, escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		return rootPane;
	}


}
