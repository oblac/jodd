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

	private JPanel jpanel = new JPanel();
	private BorderLayout borderLayout = new BorderLayout();
	private boolean modal;
	private WindowAdapter parentWindowListener;
	private Window owner;


	public CaddyDialog() {
		this((Frame) null, "", false);
	}

	public CaddyDialog(Frame owner) {
		this(owner, "", false);
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
		this(owner, "", false);
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
		jpanel.setLayout(borderLayout);
		getContentPane().add(jpanel);
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

		setTitle(title);
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


