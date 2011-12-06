// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.Tag;
import jodd.util.StringUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Tag node.
 */
public class Element extends Node {

	protected Set<String> classNames;

	public Element(Tag tag, boolean caseSensitive) {
		super(NodeType.ELEMENT, tag.getName(), caseSensitive);

		int attrCount = tag.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String key = tag.getAttributeName(i);
			String value = tag.getAttributeValue(i);
			setAttribute(key, value);
		}
	}

	public Element(String name) {
		this(name, false);
	}

	public Element(String name, boolean caseSensitive) {
		super(NodeType.ELEMENT, name, caseSensitive);
	}


	/**
	 * Handles special attribute: <code>class</code>.
	 */
	@Override
	public void setAttribute(String name, String value) {
		if (name.equals("class")) {	// todo
			setClassNames(value);
		}
		super.setAttribute(name, value);
	}

	// ---------------------------------------------------------------- class names

	/**
	 * Set class names.
	 */
	public void setClassNames(String classNames) {
		if (classNames == null) {
			this.classNames = null;
			return;
		}

		initClassNames();
		this.classNames.clear();

		String[] classes = StringUtil.splitc(classNames, ' ');
		for (String cn : classes) {
			this.classNames.add(cn);
		}
	}

	/**
	 * Returns <code>true</code> if element contains class name.
	 * // todo REMOVE!!!!
	 */
	public boolean hasClass(String className) {
		if (this.classNames == null) {
			return false;
		}
		return this.classNames.contains(className);
	}

	// ---------------------------------------------------------------- html

	/**
	 * When set to <code>true</code> closed tag will be used instead of
	 * shortcut form (&lt;foo/&gt;) when there are no children nodes. Some
	 * tags requires to have closing tag (e.g. <code>script</code>).
	 */
	protected boolean forceCloseTag;

	@Override
	public void toHtml(Appendable appendable) throws IOException {
		appendable.append('<');
		appendable.append(nodeName);

		int attrCount = getAttributesCount();
		if (attrCount != 0) {
			for (int i = 0; i < attrCount; i++) {
				Attribute attr = getAttribute(i);
				appendable.append(' ');
				attr.toHtml(appendable);
			}
		}

		int childCount = getChildNodesCount();
		if ((childCount == 0) && !forceCloseTag) {
			appendable.append("/>");
		} else {
			appendable.append('>');

			if (childCount != 0) {
				writeChildNodesAsHtml(appendable);
			}

			appendable.append("</");
			appendable.append(nodeName);
			appendable.append('>');
		}
	}


	// ---------------------------------------------------------------- init

	/**
	 * Initializes class names if needed.
	 */
	protected void initClassNames() {
		if (classNames == null) {
			classNames = new HashSet<String>();
		}
	}
}
