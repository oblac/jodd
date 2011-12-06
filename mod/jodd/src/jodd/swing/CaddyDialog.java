// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.swing;

import jodd.util.StringPool;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Frame;
import java.awt.Window;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * JDialog that doesn't block.
 * A work-around for bug: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4080029
 */
public class CaddyDialog extends JDialog {

	private JPanel panel1 = new JPanel();
	private BorderLayout borderLayout1 = new BorderLayout();
	protected boolean modal;
	private WindowAdapter parentWindowListener;
	private Window owner;


	public CaddyDialog() {
		this((Frame) null, StringPool.EMPTY, false);
	}

	public CaddyDialog(Frame owner) {
		this(owner, StringPool.EMPTY, false);
	}

	public CaddyDialog(Frame owner, String title) {
		this(owner, title, false);
	}

	public CaddyDialog(Frame owner, String title, boolean modal) {
		super(owner, title, false);
		initDialog(owner, title, modal);
		try {
			jbInit();
			pack();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public CaddyDialog(Dialog owner) {
		this(owner, StringPool.EMPTY, false);
	}

	public CaddyDialog(Dialog owner, String title) {
		this(owner, title, false);
	}

	public CaddyDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, false);
		initDialog(owner, title, modal);
		try {
			jbInit();
			pack();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// ---------------------------------------------------------------- funcs

	private void jbInit() {
		panel1.setLayout(borderLayout1);
		getContentPane().add(panel1);
	}

	private void initDialog(Window parent, String title, boolean isModal) {
		this.owner = parent;
		this.modal = isModal;

		parentWindowListener = new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				if (isVisible()) {
					getFocusBack();
				}
			}
		};
	}

	protected void getFocusBack() {
		Toolkit.getDefaultToolkit().beep();
		super.setVisible(false);
		super.pack();
		super.setLocationRelativeTo(owner);
		super.setVisible(true);
	}


	@Override
	public void dispose() {
		owner.setEnabled(true);
		owner.setFocusableWindowState(true);
		super.dispose();
	}

	@Override
	public void hide() {
		owner.setEnabled(true);
		owner.setFocusableWindowState(true);
		super.hide();
	}

	@Override
	public void setVisible(boolean visible) {
		boolean blockParent = (visible && modal);
		owner.setEnabled(!blockParent);
		owner.setFocusableWindowState(!blockParent);
		super.setVisible(visible);

		if (blockParent) {
			owner.addWindowListener(parentWindowListener);
			try {
				if (SwingUtilities.isEventDispatchThread()) {
					EventQueue theQueue = getToolkit().getSystemEventQueue();
					while (isVisible()) {
						AWTEvent event = theQueue.getNextEvent();
						Object src = event.getSource();
						if (event instanceof ActiveEvent) {
							((ActiveEvent) event).dispatch();
						} else if (src instanceof Component) {
							((Component) src).dispatchEvent(event);
						}
					}
				} else {
					synchronized (getTreeLock()) {
						while (isVisible()) {
							try {
								getTreeLock().wait();
							} catch (InterruptedException e) {
								break;
							}
						}
					}
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			owner.removeWindowListener(parentWindowListener);
			// added 2 lines
			owner.setEnabled(true);
			owner.setFocusableWindowState(true);
		}
	}

	@Override
	public void setModal(boolean modal) {
		this.modal = modal;
	}
}


