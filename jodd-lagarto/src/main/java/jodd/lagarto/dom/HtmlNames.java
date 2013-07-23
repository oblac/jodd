//  Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import java.util.Arrays;

/**
 * Utility class for HTML5 tag names.
 */
public class HtmlNames {

	public static final String[] HTML5_TAGS = {
		"a", "abbr", "acronym", "address", "applet", "area", "article", "aside", "audio", "b", "base", "basefont", "bdi", "bdo",
		"big", "blockquote", "body", "br", "button", "canvas", "caption", "center", "cite", "code", "col", "colgroup", "command", "datalist",
		"dd", "del", "details", "dfn", "dialog", "dir", "div", "dl", "dt", "em", "embed", "fieldset", "figcaption", "figure", "font", "footer",
		"form", "frame", "frameset", "h1", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hr", "html", "i", "iframe", "img",
		"input", "ins", "isindex", "kbd", "keygen", "label", "legend", "li", "link", "map", "mark", "menu", "meta", "meter", "nav", "noframes",
		"noscript", "object", "ol", "optgroup", "option", "output", "p", "param", "pre", "progress", "q", "rp", "rt", "ruby", "s", "samp",
		"script", "section", "select", "small", "source", "span", "strike", "strong", "style", "sub", "summary", "sup", "table", "tbody", "td",
		"textarea", "tfoot", "th", "thead", "time", "title", "tr", "track", "tt", "u", "ul", "var", "video", "wbr"};

	public static final String[] HTML5_ATTRIBUTES = {
		"abbr", "accept", "accept-charset", "accesskey", "action", "align", "alink", "alt", "archive", "axis", "background", "bgcolor", "border",
		"cellpadding", "cellspacing", "char", "charoff", "charset", "checked", "cite", "class", "classid", "clear", "code", "codebase", "codetype",
		"color", "cols", "colspan", "compact", "content", "coords", "data", "datetime", "declare", "defer", "dir", "disabled", "enctype", "face",
		"for", "frame", "frameborder", "headers", "height", "href", "hreflang", "hspace", "http-equiv", "id", "ismap", "label", "lang", "language",
		"link", "longdesc", "marginheight", "marginwidth", "maxlength", "media", "method", "multiple", "name", "nohref", "noresize", "noshade",
		"nowrap", "object", "onblur", "onchange", "onclick", "ondblclick", "onfocus", "onkeydown", "onkeypress", "onkeyup", "onload", "onmousedown",
		"onmousemove", "onmouseout", "onmouseover", "onmouseup", "onreset", "onselect", "onsubmit", "onunload", "profile", "prompt", "readonly",
		"rel", "rev", "rows", "rowspan", "rules", "scheme", "scope", "scrolling", "selected", "shape", "size", "span", "src", "standby", "start",
		"style", "summary", "tabindex", "target", "text", "title", "type", "usemap", "valign", "value", "valuetype", "version", "vlink", "vspace",
		"width"};

	static {
		Arrays.sort(HTML5_TAGS);
		Arrays.sort(HTML5_ATTRIBUTES);
	}

	/**
	 * Returns <code>true</code> if given tag name is HTML tag name.
	 */
	public boolean isHtmlTag(String tagName) {
		tagName = tagName.toLowerCase();

		return Arrays.binarySearch(HTML5_TAGS, tagName) >= 0;
	}

	/**
	 * Returns <code>true</code> if attribute name is one of the HTML attributes.
	 */
	public boolean isHtmlAttribute(String attrName) {
		attrName = attrName.toLowerCase();
		return Arrays.binarySearch(HTML5_ATTRIBUTES, attrName) >= 0;
	}

}