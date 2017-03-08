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

import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Font;

/**
 * Some common swing utilities.
 */
public class SwingUtil {

	/**
	 * Turns on anti-aliased text. Call it before any other Swing usage.
	 * It works for JDK5.
	 */
	public static void enableJDK5AntiAliasedText() {
		System.setProperty("swing.aatext", "true");
	}

	/**
	 * Scrolls scroll pane to the top left corner.
	 */
	public static void scrollToTop(final JScrollPane scrollPane) {
		JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
		JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
		verticalScrollBar.setValue(verticalScrollBar.getMinimum());
		horizontalScrollBar.setValue(horizontalScrollBar.getMinimum());
	}

	/**
	 * Scrolls scroll pane to the top left corner a bit later.
	 * @see #scrollToTop(JScrollPane)
	 */
	public static void scrollToTopLater(final JScrollPane scrollPane) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				scrollToTop(scrollPane);
			}
		});
	}

	/**
	 * Center JFrame.
	 */
	public static void center(JFrame frame) {
		Dimension frameSize = frame.getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - frameSize.width) >> 1, (screenSize.height - frameSize.height) >> 1);
	}

	/**
	 * Center JDialog.
	 */
	public static void center(JDialog dialog) {
		Dimension dialogSize = dialog.getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation((screenSize.width - dialogSize.width) >> 1, (screenSize.height - dialogSize.height) >> 1);
	}

	/**
	 * Enforces JEditorPane font.
	 * Once the content type of a JEditorPane is set to text/html the font on the Pane starts to be managed by Swing.
	 * This method forces using provided font.
	 */
	public static void enforceJEditorPaneFont(JEditorPane jEditorPane, Font font) {
		jEditorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		jEditorPane.setFont(font);
	}


}
