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

package jodd.decora.parser;

import jodd.decora.DecoraException;
import jodd.lagarto.EmptyTagVisitor;
import jodd.lagarto.Tag;
import jodd.lagarto.TagType;

import java.util.ArrayList;

/**
 * Visitor that detect Decora tags in the decorator.
 * Therefore, it determines Decora tags position inside decorator file.
 * <p>
 * If decorator content is static, array of Decora tags can be cached
 * and {@link jodd.decora.parser.DecoraTag#duplicate() duplicated} to
 * skip parsing decorator again.
 */
public class DecoratorTagVisitor extends EmptyTagVisitor {

	protected ArrayList<DecoraTag> decoraTags = new ArrayList<>();

	/**
	 * Returns an array of founded Decora tags.
	 */
	public DecoraTag[] getDecoraTags() {
		return decoraTags.toArray(new DecoraTag[0]);
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
	public void tag(final Tag tag) {
		String tagName = tag.getName().toString();

		if (tagName.startsWith("decora:") ) {
			onDecoraTag(tag);
			return;
		}
		if (tag.getType().isStartingTag()) {
			CharSequence id = tag.getId();

			if (id != null && id.toString().startsWith("decora-")) {
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
	 * Handle Decora tags.
	 */
	protected void onDecoraTag(final Tag tag) {
		String tagName = tag.getName().toString();

		if (tag.getType() == TagType.SELF_CLOSING) {
			checkNestedDecoraTags();
			decoraTagName = tagName.substring(7);
			decoraTagStart = tag.getTagPosition();
			decoraTagEnd = tag.getTagPosition() + tag.getTagLength();
			defineDecoraTag();
			return;
		}

		if (tag.getType() == TagType.START) {
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
	protected void onIdAttrStart(final Tag tag) {
		String id = tag.getId().toString().substring(7);
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

		if (tag.getType() == TagType.SELF_CLOSING) {
			checkNestedDecoraTags();
			decoraTagName = tagName;
			decoraIdName = idName;
			decoraTagStart = tag.getTagPosition();
			decoraTagEnd = tag.getTagPosition() + tag.getTagLength();
			defineDecoraTag();
			return;
		}

		if (tag.getType() == TagType.START) {
			checkNestedDecoraTags();
			decoraTagName = tagName;
			decoraIdName = idName;
			decoraTagStart = tag.getTagPosition();
			decoraTagDefaultValueStart = tag.getTagPosition() + tag.getTagLength();

			closingTagName = tag.getName().toString();
			closingTagDeepLevel = tag.getDeepLevel();
		}
	}

	protected void onIdAttrEnd(final Tag tag) {
		decoraTagEnd = tag.getTagPosition() + tag.getTagLength();
		decoraTagDefaultValueEnd = tag.getTagPosition();
		defineDecoraTag();
	}

	// ---------------------------------------------------------------- define

	/**
	 * Defines Decora tag position inside decorator content.
	 * Resets current Decora tag tracking.
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
