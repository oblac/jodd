// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.decora.parser;

import jodd.decora.DecoraException;
import jodd.lagarto.EmptyTagVisitor;
import jodd.lagarto.Tag;
import jodd.lagarto.TagType;

import java.util.LinkedList;

/**
 * Visitor that detect Decora tags in the decorator.
 * Therefore, it determines decora tags position inside decorator file.
 * <p>
 * If decorator content is static, array of decora tags can be cached
 * adn {@link jodd.decora.parser.DecoraTag#duplicate() duplicated} to
 * skip parsing decorator again.
 */
public class DecoratorTagVisitor extends EmptyTagVisitor {

	protected LinkedList<DecoraTag> decoraTags = new LinkedList<DecoraTag>();

	/**
	 * Returns an array of founded Decora tags.
	 */
	public DecoraTag[] getDecoraTags() {
		return decoraTags.toArray(new DecoraTag[decoraTags.size()]);
	}

	protected String decoraTagName;
	protected String decoraIdName;
	protected int decoraTagStart;
	protected int decoraTagEnd;
	protected int decoraTagDefaultValueStart;
	protected int decoraTagDefaultValueEnd;

	protected String closingTagName;
	protected int closingTagDeepLevel;

	@Override
	public void tag(Tag tag) {
		String tagName = tag.getName();

		if (tagName.startsWith("decora:") ) {
			onDecoraTag(tag);
			return;
		}
		if (tag.getType().isOpeningTag()) {
			String id = tag.getId();

			if (id != null && id.startsWith("decora-")) {
				onIdAttrStart(tag);
			}
		} else {
			// close tag
			if (tagName.equals(closingTagName) && closingTagDeepLevel == tag.getDeepLevel()) {
				onIdAttrEnd(tag);
			}
		}
	}

	// ---------------------------------------------------------------- handlers

	/**
	 * Handle decora tags.
	 */
	protected void onDecoraTag(Tag tag) {
		String tagName = tag.getName();

		if (tag.getType() == TagType.EMPTY) {
			checkNestedDecoraTags();
			decoraTagName = tagName.substring(7);
			decoraTagStart = tag.getTagPosition();
			decoraTagEnd = tag.getTagPosition() + tag.getTagLength();
			defineDecoraTag();
			return;
		}

		if (tag.getType() == TagType.OPEN) {
			checkNestedDecoraTags();
			decoraTagName = tagName.substring(7);
			decoraTagStart = tag.getTagPosition();
			decoraTagDefaultValueStart = tag.getTagPosition() + tag.getTagLength();
			return;
		}

		// closed tag type
		decoraTagEnd = tag.getTagPosition() + tag.getTagLength();
		decoraTagDefaultValueEnd = tag.getTagPosition();
		defineDecoraTag();
	}

	/**
	 * Handle open and empty ID attribute tags.
	 */
	protected void onIdAttrStart(Tag tag) {
		String id = tag.getId().substring(7);
		String tagName;
		String idName;

		int dashIndex = id.indexOf('-');
		if (dashIndex == -1) {
			tagName = id;
			idName = null;
		} else {
			tagName = id.substring(0, dashIndex);
			idName = id.substring(dashIndex + 1);
		}

		if (tag.getType() == TagType.EMPTY) {
			checkNestedDecoraTags();
			decoraTagName = tagName;
			decoraIdName = idName;
			decoraTagStart = tag.getTagPosition();
			decoraTagEnd = tag.getTagPosition() + tag.getTagLength();
			defineDecoraTag();
			return;
		}

		if (tag.getType() == TagType.OPEN) {
			checkNestedDecoraTags();
			decoraTagName = tagName;
			decoraIdName = idName;
			decoraTagStart = tag.getTagPosition();
			decoraTagDefaultValueStart = tag.getTagPosition() + tag.getTagLength();

			closingTagName = tag.getName();
			closingTagDeepLevel = tag.getDeepLevel();
		}
	}

	protected void onIdAttrEnd(Tag tag) {
		decoraTagEnd = tag.getTagPosition() + tag.getTagLength();
		decoraTagDefaultValueEnd = tag.getTagPosition();
		defineDecoraTag();
	}

	// ---------------------------------------------------------------- define

	/**
	 * Defines decora tag position inside decorator content.
	 * Resets current decora tag tracking.
	 */
	protected void defineDecoraTag() {
		DecoraTag decoraTag =
				decoraTagDefaultValueStart == 0 ?
					new DecoraTag(decoraTagName, decoraIdName, decoraTagStart, decoraTagEnd) :
					new DecoraTag(
							decoraTagName, decoraIdName,
							decoraTagStart, decoraTagEnd,
							decoraTagDefaultValueStart, decoraTagDefaultValueEnd - decoraTagDefaultValueStart);

		decoraTags.add(decoraTag);
		decoraTagName = null;
		decoraIdName = null;
		closingTagName = null;
		decoraTagDefaultValueStart = 0;
	}

	// ---------------------------------------------------------------- tools

	/**
	 * Check if decora tag is currently defined and throws an exception
	 * on nested tags.
	 */
	protected void checkNestedDecoraTags() {
		if (decoraTagName != null) {
			throw new DecoraException("Nested Decora tags not allowed");
		}
	}
}
