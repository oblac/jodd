// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.swingspy;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.FlowLayout;

public class HelloSwing extends JFrame {

	public HelloSwing() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JPanel p = new ContentPanel();
		this.add(p);
	}

	public static void main(String[] args) {
		try {
			Class.forName("jodd.swingspy.SwingSpy").getMethod("install").invoke(null);
		} catch (Exception e) {
			System.err.println("SwingSpy is not installed... ");
		}
		HelloSwing w = new HelloSwing();
		w.setBounds(200, 100, 250, 150);
		w.setTitle("First Window");
		w.setVisible(true);
	}

	public static class ContentPanel extends JPanel {

		JPanel panel;
		JLabel label;
		JButton button;

		public ContentPanel() {
			panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			AddControls();
		}

		private void AddControls() {
			label = new JLabel("Click the Button");
			button = new JButton("Click Me");

			panel.add(label);
			panel.add(button);

			this.add(panel);
		}
	}
}
