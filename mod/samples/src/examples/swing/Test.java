// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.swing;

import jodd.swing.spy.SwingSpy;
import jodd.swing.SwingUtil;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JDialog;
import java.awt.BorderLayout;


public class Test extends JDialog {

	private JPanel contentPane;

	public Test() {
		super();
		contentPane = new JPanel();
		setContentPane(contentPane);
		BorderLayout b = new BorderLayout();
		contentPane.setLayout(b);

		contentPane.add(new JLabel("test"), BorderLayout.NORTH);
		contentPane.add(new JTextField("test"), BorderLayout.CENTER);

		setModal(true);
	}

	public static void main(String[] args) {
		SwingUtil.enableJDK5AntiAliasedText();
//		try {
//			Class.forName("jodd.swing.spy.SwingSpy").getMethod("install").invoke(null);
//		} catch (Exception e) {
//			System.err.println("SwingSpy is not installed... " + e.toString());
//		}
		Test dialog = new Test();
		dialog.setModal(true);
		new SwingSpy().showSpyDialog(dialog);
		dialog.pack();
		dialog.setVisible(true);
	}
}
