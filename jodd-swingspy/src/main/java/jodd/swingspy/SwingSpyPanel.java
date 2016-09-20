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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.TreeSet;

/**
 * SwingSpy GUI.
 */
public class SwingSpyPanel extends JPanel {

	private final JTree componentTree;
	private final JEditorPane componentData;
	private final JEditorPane detailsData;
	private final JScrollPane detailsScrollPane;
	private final DefaultMutableTreeNode root;

	private static final int INITIAL_WIDTH = 600;
	private static final int INITIAL_HEIGHT = 500;

	private final Font font = new Font("Arial", Font.PLAIN,  12);

	/**
	 * Initialization.
	 */
	public SwingSpyPanel() {
		setPreferredSize(new Dimension(INITIAL_WIDTH, INITIAL_HEIGHT));
		setLayout(new BorderLayout());

		root = new DefaultMutableTreeNode();
		componentTree = new JTree(root);
		componentTree.setRootVisible(false);
		componentTree.setCellRenderer(new SwingComponentRenderer());
		componentTree.addTreeSelectionListener(new CustomSelectionListener());
//		add(new JScrollPane(componentTree), BorderLayout.CENTER);

		detailsData = new JEditorPane();
		detailsData.setBackground(new Color(250, 250, 250));
		detailsData.setForeground(new Color(33, 33, 33));
		detailsData.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 244), 1));
		detailsData.setPreferredSize(new Dimension(150, INITIAL_HEIGHT));
		detailsData.setEditable(false);
		detailsData.setContentType("text/html");
		SwingUtil.enforceJEditorPaneFont(detailsData, font);
		detailsScrollPane = new JScrollPane(detailsData);
//		add(detailsScrollPane, BorderLayout.EAST);

		JSplitPane hPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(componentTree), detailsScrollPane);
		hPane.setContinuousLayout(true);
		hPane.setOneTouchExpandable(true);
		hPane.setDividerLocation(INITIAL_WIDTH - 200);
		add(hPane, BorderLayout.CENTER);

		componentData = new JEditorPane();
		componentData.setBackground(new Color(250, 250, 250));
		componentData.setForeground(new Color(33, 33, 33));
		componentData.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 244), 1));
		componentData.setPreferredSize(new Dimension(INITIAL_WIDTH, 36));
		componentData.setEditable(false);
		componentData.setContentType("text/html");
		SwingUtil.enforceJEditorPaneFont(componentData, font);
		add(componentData, BorderLayout.SOUTH);

	}

	/**
	 * Reloads component tree completely.
	 */
	public void reload(Component rootComponent, Component selectedComponent) {
		root.removeAllChildren();
		addNode(root, rootComponent, selectedComponent);
		SwingUtilities.updateComponentTreeUI(componentTree);
	}

	/**
	 * Recursively adds new nodes to the tree.
	 */
	protected void addNode(DefaultMutableTreeNode parent, Component component, Component selectedComponent) {
		DefaultMutableTreeNode componentNode = new DefaultMutableTreeNode(new ComponentWrapper(component));
		parent.add(componentNode);
		if (component == selectedComponent) {
			TreePath selectedPath = new TreePath(componentNode.getPath());
			componentTree.setSelectionPath(selectedPath);
			componentTree.scrollPathToVisible(selectedPath);
		}

		if (component instanceof Container) {
			Container container = (Container) component;
			Component[] childComponents = container.getComponents();
			for (Component child : childComponents) {
				addNode(componentNode, child, selectedComponent);
			}
		}
	}

	// ---------------------------------------------------------------- component wrapper

	/**
	 * Simple component wrapper that has nice <code>toString</code> representation,
	 * suitable for the tree view.
	 */
	static class ComponentWrapper {
		Component component;

		ComponentWrapper(Component component) {
			this.component = component;
		}

		@Override
		public String toString() {
			String name = component.getName();
			return (name != null ? name + "  " : "") + '(' + component.getClass().getSimpleName() + ')';
		}

		public String toHtmlString() {
			return new StringBuilder("<html>").append("&nbsp;name: ")
					.append("<b>").append(component.getName())
					.append("</b><br>").append("&nbsp;class: ").append("<b>")
					.append(component.getClass().getName()).append("</b><br>")
					.toString();
		}

		public String toDetailedString() {
			Object bean = component;
			if (bean == null) {
				return "<null>";
			}

			TreeSet<String> treeSet = new TreeSet<>();

			Class clazz = bean.getClass();
			Field[] fields = clazz.getDeclaredFields();

			for (Field field : fields) {
				StringBuilder str = new StringBuilder();

				int modifiers = field.getModifiers();
				if (Modifier.isPublic(modifiers)) {
					str.append('+');
				} else if (Modifier.isProtected(modifiers)) {
					str.append('#');
				} else if (Modifier.isPrivate(modifiers)) {
					str.append('-');
				} else {
					str.append(' ');
				}
				field.setAccessible(true);

				str.append(field.getName()).append(':');

				try {
					Object value = field.get(bean);

					if (value == null) {
						str.append("<null>");
					} else {
						String valueString = value.toString();
						if (valueString.length() > 250) {
							valueString = valueString.substring(0, 250) + "...";
						}
						str.append(valueString);
					}
				} catch (IllegalAccessException ignore) {
					str.append("N/A");
				}

				str.append('\n');

				treeSet.add(htmlSafe(str.toString()));
			}

			StringBuilder resolve = new StringBuilder("<html>");
			for (String s1 : treeSet) {
				resolve.append(s1);
			}
			return resolve.toString();
		}
	}

	private static String htmlSafe(String str) {
		str = str.replace(">", "&gt;");
		str = str.replace("<", "&lt;");
		str = str.replace(" ", "&nbsp;");
		str = str.replace("\n", "<br>");
		return str;
	}

	// ---------------------------------------------------------------- selection listener

	/**
	 * Selection listener.
	 */
	class CustomSelectionListener implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent tse) {
			DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();
			ComponentWrapper cmp = (ComponentWrapper) lastNode.getUserObject();
			componentData.setText(cmp.toHtmlString());
			detailsData.setText(cmp.toDetailedString());
			SwingUtil.scrollToTopLater(detailsScrollPane);
		}
	}

	// ---------------------------------------------------------------- custom renderer

	/**
	 * Displays node icons based on component type.
	 */
	static class SwingComponentRenderer extends DefaultTreeCellRenderer {

		private static ImageIcon noneIcon;
		private static Class[] cmpClasses;
		private static ImageIcon[] cmpIcons;

		static {
			String packageName = SwingComponentRenderer.class.getPackage().getName();
			packageName = '/' + packageName.replace('.', '/') + "/icons/";

			noneIcon = createImageIcon(packageName + "none.png");

			cmpClasses = new Class[]{
					JButton.class,
					JCheckBox.class,
					JComboBox.class,
					JLabel.class,
					JList.class,
					JPanel.class,
					JProgressBar.class,
					JRadioButton.class,
					JScrollBar.class,
					JScrollPane.class,
					JSlider.class,
					JTabbedPane.class,
					JTextArea.class,
					JTextField.class,
					JTree.class,
					JEditorPane.class,
					JFormattedTextField.class,
					JPasswordField.class,
					JSpinner.class,
					JTable.class,
					JTextPane.class,
					JToolBar.class,
					JLayeredPane.class,
					JSplitPane.class,
					JSeparator.class,
			};
			cmpIcons = new ImageIcon[]{
					createImageIcon(packageName + "button.png"),
					createImageIcon(packageName + "checkBox.png"),
					createImageIcon(packageName + "comboBox.png"),
					createImageIcon(packageName + "label.png"),
					createImageIcon(packageName + "list.png"),
					createImageIcon(packageName + "panel.png"),
					createImageIcon(packageName + "progressbar.png"),
					createImageIcon(packageName + "radioButton.png"),
					createImageIcon(packageName + "scrollbar.png"),
					createImageIcon(packageName + "scrollPane.png"),
					createImageIcon(packageName + "slider.png"),
					createImageIcon(packageName + "tabbedPane.png"),
					createImageIcon(packageName + "textArea.png"),
					createImageIcon(packageName + "textField.png"),
					createImageIcon(packageName + "tree.png"),
					createImageIcon(packageName + "editorPane.png"),
					createImageIcon(packageName + "formattedTextField.png"),
					createImageIcon(packageName + "passwordField.png"),
					createImageIcon(packageName + "spinner.png"),
					createImageIcon(packageName + "table.png"),
					createImageIcon(packageName + "textPane.png"),
					createImageIcon(packageName + "toolbar.png"),
					createImageIcon(packageName + "panel.png"),
					createImageIcon(packageName + "splitPane.png"),
					createImageIcon(packageName + "separator.png"),
			};
		}


		protected static ImageIcon createImageIcon(String path) {
			URL imgURL = SwingComponentRenderer.class.getResource(path);
			if (imgURL != null) {
				return new ImageIcon(imgURL);
			} else {
				System.err.println("Couldn't find icon file: " + path);
				return null;
			}
		}


		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			if (node.getUserObject() != null) {
				ComponentWrapper userObject = (ComponentWrapper) node.getUserObject();
				if (userObject != null) {
					Component c = userObject.component;
					for (int i = 0; i < cmpClasses.length; i++) {
						Class clazz = cmpClasses[i];
						if (clazz.isAssignableFrom(c.getClass())) {
							setIcon(cmpIcons[i]);
							return this;
						}
					}
				}
				setIcon(noneIcon);
			}
			return this;
		}

	}

}