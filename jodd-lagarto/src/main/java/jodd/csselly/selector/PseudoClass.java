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

package jodd.csselly.selector;

import jodd.lagarto.dom.Node;

import java.util.List;

/**
 * Pseudo classes.
 */
public abstract class PseudoClass {

	// ---------------------------------------------------------------- STANDARD PSEUDO CLASSES

	/**
	 * Same as <code>:nth-child(1)</code>. Represents an element that is the first child of some other element.
	 */
	public static class FIRST_CHILD extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return node.getSiblingElementIndex() == 0;
		}
	}

	/**
	 * Same as <code>:nth-last-child(1)</code>. Represents an element that is the last child of some other element.
	 */
	public static class LAST_CHILD extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return node.getSiblingElementIndex() == node.getParentNode().getChildElementsCount() - 1;
		}
	}

	/**
	 * Represents an element that has a parent element and whose parent element has no other element children.
	 * Same as <code>:first-child:last-child</code> or <code>:nth-child(1):nth-last-child(1)</code>, but with
	 * a lower specificity.
	 */
	public static class ONLY_CHILD extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return (node.getSiblingElementIndex() == 0) && (node.getParentNode().getChildElementsCount() == 1);
		}
	}

	/**
	 * Same as <code>:nth-of-type(1)</code>. Represents an element that is the first sibling of its
	 * type in the list of children of its parent element.
	 */
	public static class FIRST_OF_TYPE extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return node.getSiblingNameIndex() == 0;
		}
	}

	/**
	 * Same as <code>:nth-last-of-type(1)</code>. Represents an element that is the last sibling of its
	 * type in the list of children of its parent element.
	 */
	public static class LAST_OF_TYPE extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return node.getNextSiblingName() == null;
		}
	}

	/**
	 * Represents an element that is the root of the document.
	 * In HTML 4, this is always the HTML element.
	 */
	public static class ROOT extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return node.getParentNode().getNodeType() == Node.NodeType.DOCUMENT;
		}
	}

	/**
	 * Represents an element that has no children at all.
	 */
	public static class EMPTY extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return node.getChildNodesCount() == 0;
		}
	}

	/**
	 * Represents an element that has a parent element and whose parent
	 * element has no other element children with the same expanded element
	 * name. Same as <code>:first-of-type:last-of-type</code> or
	 * <code>:nth-of-type(1):nth-last-of-type(1)</code>, but with a lower specificity.
	 */
	public static class ONLY_OF_TYPE extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return (node.getSiblingNameIndex() == 0) && (node.getNextSiblingName() == null);
		}
	}

	// ---------------------------------------------------------------- EXTENDED PSEUDO CLASSES

	/**
	 * Selects the first matched element.
	 */
	public static class FIRST extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return true;
		}

		@Override
		public boolean match(List<Node> currentResults, Node node, int index) {
			if (currentResults.isEmpty()) {
				return false;
			}
			Node firstNode = currentResults.get(0);	// getFirst();
			if (firstNode == null) {
				return false;
			}
			return firstNode == node;
		}
	}

	/**
	 * Selects the last matched element. Note that <code>:last</code> selects
	 * a single element by filtering the current collection and matching the
	 * last element within it.
	 */
	public static class LAST extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return true;
		}

		@Override
		public boolean match(List<Node> currentResults, Node node, int index) {
			int size = currentResults.size();
			if (size == 0) {
				return false;
			}
			Node lastNode = currentResults.get(size - 1); // getLast();
			if (lastNode == null) {
				return false;
			}
			return lastNode == node;
		}
	}

	/**
	 * Selects all button elements and elements of type button.
	 */
	public static class BUTTON extends PseudoClass {
		@Override
		public boolean match(Node node) {
			String type = node.getAttribute("type");
			if (type == null) {
				return false;
			}
			return type.equals("button");
		}
	}

	/**
	 * Selects all elements of type checkbox.
	 */
	public static class CHECKBOX extends PseudoClass {
		@Override
		public boolean match(Node node) {
			String type = node.getAttribute("type");
			if (type == null) {
				return false;
			}
			return type.equals("checkbox");
		}
	}

	/**
	 * Selects all elements of type file.
	 */
	public static class FILE extends PseudoClass {
		@Override
		public boolean match(Node node) {
			String type = node.getAttribute("type");
			if (type == null) {
				return false;
			}
			return type.equals("file");
		}
	}

	/**
	 * Selects all elements that are headers, like h1, h2, h3 and so on.
	 */
	public static class HEADER extends PseudoClass {
		@Override
		public boolean match(Node node) {
			String name = node.getNodeName();
			if (name == null) {
				return false;
			}
			if (name.length() != 2) {
				return false;
			}
			char c1 = name.charAt(0);
			if (c1 != 'h' && c1 != 'H') {
				return false;
			}
			int c2 = name.charAt(1) - '0';
			return c2 >= 1 && c2 <= 6;
		}
	}

	/**
	 * Selects all elements of type image.
	 */
	public static class IMAGE extends PseudoClass {
		@Override
		public boolean match(Node node) {
			String type = node.getAttribute("type");
			if (type == null) {
				return false;
			}
			return type.equals("image");
		}
	}

	/**
	 * Selects all input, textarea, select and button elements.
	 */
	public static class INPUT extends PseudoClass {
		@Override
		public boolean match(Node node) {
			String tagName = node.getNodeName();
			if (tagName == null) {
				return false;
			}
			if (tagName.equals("button")) {
				return true;
			}
			if (tagName.equals("input")) {
				return true;
			}
			if (tagName.equals("select")) {
				return true;
			}
			//noinspection RedundantIfStatement
			if (tagName.equals("textarea")) {
				return true;
			}
			return false;
		}
	}

	/**
	 * Select all elements that are the parent of another element, including text nodes.
	 * This is the inverse of <code>:empty</code>.
	 */
	public static class PARENT extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return node.getChildNodesCount() != 0;
		}
	}

	/**
	 * Selects all elements of type password.
	 */
	public static class PASSWORD extends PseudoClass {
		@Override
		public boolean match(Node node) {
			String type = node.getAttribute("type");
			if (type == null) {
				return false;
			}
			return type.equals("password");
		}
	}

	/**
	 * Selects all elements of type radio.
	 */
	public static class RADIO extends PseudoClass {
		@Override
		public boolean match(Node node) {
			String type = node.getAttribute("type");
			if (type == null) {
				return false;
			}
			return type.equals("radio");
		}
	}

	/**
	 * Selects all elements of type reset.
	 */
	public static class RESET extends PseudoClass {
		@Override
		public boolean match(Node node) {
			String type = node.getAttribute("type");
			if (type == null) {
				return false;
			}
			return type.equals("reset");
		}
	}

	/**
	 * Selects all elements that are selected.
	 */
	public static class SELECTED extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return node.hasAttribute("selected");
		}
	}

	/**
	 * Selects all elements that are checked.
	 */
	public static class CHECKED extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return node.hasAttribute("checked");
		}
	}

	/**
	 * Selects all elements of type submit.
	 */
	public static class SUBMIT extends PseudoClass {
		@Override
		public boolean match(Node node) {
			String type = node.getAttribute("type");
			if (type == null) {
				return false;
			}
			return type.equals("submit");
		}
	}

	/**
	 * Selects all elements of type text.
	 */
	public static class TEXT extends PseudoClass {
		@Override
		public boolean match(Node node) {
			String type = node.getAttribute("type");
			if (type == null) {
				return false;
			}
			return type.equals("text");
		}
	}

	/**
	 * Selects even elements, zero-indexed.
	 */
	public static class EVEN extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return true;
		}

		@Override
		public boolean match(List<Node> currentResults, Node node, int index) {
			return index % 2 == 0;
		}
	}

	/**
	 * Selects odd elements, zero-indexed.
	 */
	public static class ODD extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return true;
		}

		@Override
		public boolean match(List<Node> currentResults, Node node, int index) {
			return index % 2 != 0;
		}
	}


	// ---------------------------------------------------------------- interface

	/**
	 * Returns <code>true</code> if node matches the pseudoclass.
	 */
	public abstract boolean match(Node node);

	/**
	 * Returns <code>true</code> if node matches the pseudoclass within current results.
	 */
	public boolean match(List<Node> currentResults, Node node, int index) {
		return true;
	}

	/**
	 * Returns pseudo-class name from simple class name.
	 */
	public String getPseudoClassName() {
		String name = getClass().getSimpleName().toLowerCase();
		name = name.replace('_', '-');
		return name;
	}

}