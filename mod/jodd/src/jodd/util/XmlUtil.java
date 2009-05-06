// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Some XML and XPath utilities.
 */
public class XmlUtil {


	// ---------------------------------------------------------------- attributes

	/**
	 * Returns a map of all node's attributes. All non-attribute nodes are ignored.
	 */
	public static Map<String, String> getAllAttributes(Node node) {
		HashMap<String, String> attrs = new HashMap<String, String>();
		NamedNodeMap nmm = node.getAttributes();
		for (int j = 0; j < nmm.getLength(); j++) {
			Node attribute = nmm.item(j);
			if (attribute.getNodeType() != Node.ATTRIBUTE_NODE) {
				continue;
			}
			attrs.put(attribute.getNodeName(), attribute.getNodeValue());
		}
		return attrs;
	}

    /**
	 * Returns attribute value of a node or <code>null</code> if attribute name not found.
	 * Specified attribute is searched on every call.
	 * Consider {@link #getAllAttributes(org.w3c.dom.Node)} for better performances.
	 */
	public static String getAttributeValue(Node node, String attrName) {
		NamedNodeMap nmm = node.getAttributes();
		for (int j = 0; j < nmm.getLength(); j++) {
			Node attribute = nmm.item(j);
			if (attribute.getNodeType() != Node.ATTRIBUTE_NODE) {
				continue;
			}
			String nodeName = attribute.getNodeName();
			if (nodeName.equals(attrName)) {
				return attribute.getNodeValue();
			}
		}
		return null;
	}

	/**
	 * Get element's attribute value or <code>null</code> if attribute not found or empty.
	 */
	public static String getAttributeValue(Element element, String name) {
		String value = element.getAttribute(name);
		if (value.length() == 0) {
			value = null;
		}
		return value;
	}


	// ---------------------------------------------------------------- nodelist

	/**
	 * Filters node list by keeping nodes of specified type.
	 */
	public static List filterNodeList(NodeList nodeList, short keepNodeType) {
		return filterNodeList(nodeList, keepNodeType, null);
	}

	/**
	 * Filters node list by keeping nodes of specified type and node name.
	 */
	public static List<Node> filterNodeList(NodeList nodeList, short keepNodeType, String nodeName) {
		List<Node> nodes = new ArrayList<Node>();
		for (int k = 0; k < nodeList.getLength(); k++) {
			Node node = nodeList.item(k);
			if (node.getNodeType() != keepNodeType) {
				continue;
			}
			if (nodeName != null && (node.getNodeName().equals(nodeName) == false)) {
				continue;
			}
			nodes.add(node);
		}
		return nodes;
	}

	/**
	 * Filter node list for all Element nodes.
	 */
	public static List filterNodeListElements(NodeList nodeList) {
		return filterNodeListElements(nodeList, null);
	}

	/**
	 * Filter node list for Element nodes of specified name.
	 */
	public static List<Node> filterNodeListElements(NodeList nodeList, String nodeName) {
		List<Node> nodes = new ArrayList<Node>();
		for (int k = 0; k < nodeList.getLength(); k++) {
			Node node = nodeList.item(k);
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (nodeName != null && (node.getNodeName().equals(nodeName) == false)) {
				continue;
			}
			nodes.add(node);
		}
		return nodes;
	}


	/**
	 * Returns a list of all child Elements,
	 */
	public static List getChildElements(Node node) {
		return getChildElements(node, null);
	}


	/**
	 * Returns a list of child Elements of specified name.
	 */
	public static List getChildElements(Node node, String nodeName) {
		NodeList childs = node.getChildNodes();
		return filterNodeListElements(childs, nodeName);
	}

	// ---------------------------------------------------------------- node


	/**
	 * Returns value of first available child text node or <code>null</code> if not found.
	 */
	public static String getFirstChildTextNodeValue(Node node) {
		NodeList children = node.getChildNodes();
		int len = children.getLength();
		for (int i = 0; i < len; i++) {
			Node n = children.item(i);
			if (n.getNodeType() == Node.TEXT_NODE) {
				return n.getNodeValue();
			}
		}
		return null;
	}

	/**
	 * Returns value of single child text node or <code>null</code>.
	 */
	public static String getChildTextNodeValue(Node node) {
		if (node.getChildNodes().getLength() != 1) {
			return null;
		}
		Node item0 = node.getChildNodes().item(0);
		if (item0.getNodeType() != Node.TEXT_NODE) {
			return null;
		}
		return item0.getNodeValue();
	}



}
