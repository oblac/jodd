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

package jodd.lagarto.form;

import jodd.lagarto.Tag;
import jodd.lagarto.TagType;
import jodd.lagarto.TagWriter;
import jodd.mutable.MutableInteger;
import jodd.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Form processor. Invokes {@link jodd.lagarto.form.FormFieldResolver} on form fields.
 */
public class FormProcessorVisitor extends TagWriter {

	private static final char[] INPUT = new char[] {'i', 'n', 'p', 'u', 't'};
	private static final char[] TYPE = new char[] {'t', 'y', 'p', 'e'};
	private static final char[] SELECT = new char[] {'s', 'e', 'l', 'e', 'c', 't'};
	private static final char[] OPTION = new char[] {'o', 'p', 't', 'i', 'o', 'n'};
	private static final char[] TEXTAREA = new char[] {'t', 'e', 'x', 't', 'a', 'r', 'e', 'a'};

	private static final String VALUE = "value";
	private static final String NAME = "name";
	private static final String TEXT = "text";
	private static final String HIDDEN = "hidden";
	private static final String IMAGE = "image";
	private static final String PASSWORD = "password";
	private static final String CHECKBOX = "checkbox";
	private static final String TRUE = "true";
	private static final String CHECKED = "checked";
	private static final String RADIO = "radio";
	private static final String SELECTED = "selected";

	private final FormFieldResolver resolver;

	public FormProcessorVisitor(Appendable appendable, FormFieldResolver resolver) {
		super(appendable);
		this.resolver = resolver;
	}

	@Override
	public void tag(Tag tag) {
		if (tag.getType().isStartingTag()) {
			if (tag.matchTagName(INPUT)) {
				processInputStartTag(tag);
				super.tag(tag);
				return;
			}
			if (inSelect && tag.matchTagName(OPTION)) {
				processOptionOpenTag(tag);
				super.tag(tag);
				return;
			}
		}

		if (tag.getType() == TagType.START) {
			if (tag.matchTagName(TEXTAREA)) {
				processTextareaStartTag(tag);
			}
			else if (tag.matchTagName(SELECT)) {
				processSelectOpenTag(tag);
			}
		}
		else if (tag.getType() == TagType.END) {
			if (inTextArea && tag.matchTagName(TEXTAREA)) {
				processTextareaEndTag();
			}
			else if (inSelect && tag.matchTagName(SELECT)) {
				processSelectEndTag();
			}
		}

		super.tag(tag);
	}

	@Override
	public void text(CharSequence text) {
		if (inTextArea) {
			return;
		}
		super.text(text);
	}

	// ---------------------------------------------------------------- input

	private void processInputStartTag(Tag tag) {
		// INPUT
		CharSequence tagType = tag.getAttributeValue(TYPE);
		if (tagType == null) {
			return;
		}
		CharSequence nameSequence = tag.getAttributeValue(NAME);
		if (nameSequence == null) {
			return;
		}

		String name = nameSequence.toString();

		Object valueObject = resolver.value(name);
		if (valueObject == null) {
			return;
		}

		String tagTypeName = tagType.toString().toLowerCase();

		if (
				tagTypeName.equals(TEXT) ||
				tagTypeName.equals(HIDDEN) ||
				tagTypeName.equals(IMAGE) ||
				tagTypeName.equals(PASSWORD)) {

			String value = valueToString(name, valueObject);

			if (value == null) {
				return;
			}

			tag.setAttribute(VALUE, value);
		}
		else if (tagTypeName.equals(CHECKBOX)) {
			CharSequence tagValue = tag.getAttributeValue(VALUE);
			if (tagValue == null) {
				tagValue = TRUE;
			}
			tagValue = tagValue.toString();

			if (valueObject.getClass().isArray()) {
				// checkbox group
				String vs[] = StringUtil.toStringArray(valueObject);
				for (String vsk : vs) {
					if ((vsk != null) && (vsk.equals(tagValue))) {
						tag.setAttribute(CHECKED, null);
					}
				}
			} else if (tagValue.equals(valueObject.toString())) {
				tag.setAttribute(CHECKED, null);
			}
		}
		else if (tagType.equals(RADIO)) {
			CharSequence tagValue = tag.getAttributeValue(VALUE);
			if (tagValue != null) {
				tagValue = tagValue.toString();
				if (tagValue.equals(valueObject.toString())) {
					tag.setAttribute(CHECKED, null);
				}
			}
		}
	}

	// ---------------------------------------------------------------- convert values to string

	protected Map<String, MutableInteger> valueNameIndexes;

	/**
	 * Converts value to a string.
	 */
	protected String valueToString(String name, Object valueObject) {
		if (!valueObject.getClass().isArray()) {
			return valueObject.toString();
		}

		// array
		String[] array = (String[]) valueObject;

		if (valueNameIndexes == null) {
			valueNameIndexes = new HashMap<>();
		}

		MutableInteger index = valueNameIndexes.get(name);
		if (index == null) {
			index = new MutableInteger(0);
			valueNameIndexes.put(name, index);
		}

		if (index.value >= array.length) {
			return null;
		}

		String result = array[index.value];

		index.value++;

		return result;
	}

	// ---------------------------------------------------------------- select

	private boolean inSelect;
	private String currentSelectName;

	private void processSelectOpenTag(Tag tag) {
		CharSequence name = tag.getAttributeValue(NAME);

		if (name == null) {
			return;
		}

		currentSelectName = name.toString();
		inSelect = true;
	}

	private void processSelectEndTag() {
		inSelect = false;
		currentSelectName = null;
	}

	private void processOptionOpenTag(Tag tag) {
		CharSequence tagValue = tag.getAttributeValue(VALUE);
		if (tagValue == null) {
			return;
		}

		Object vals = resolver.value(currentSelectName);
		if (vals == null) {
			return;
		}
		tagValue = tagValue.toString();

		if (vals.getClass().isArray()) {
			String vs[] = StringUtil.toStringArray(vals);
			for (String vsk : vs) {
				if ((vsk != null) && (vsk.equals(tagValue))) {
					tag.setAttribute(SELECTED, null);
				}
			}
		} else {
			String value = StringUtil.toString(vals);
			if (value.equals(tagValue)) {
				tag.setAttribute(SELECTED, null);
			}
		}
	}

	// ---------------------------------------------------------------- textarea

	private String textAreaValue;
	private boolean inTextArea;

	private void processTextareaStartTag(Tag tag) {
		inTextArea = true;

		CharSequence name = tag.getAttributeValue(NAME);
		if (name == null) {
			return;
		}
		Object valueObject = resolver.value(name.toString());
		if (valueObject != null) {
			textAreaValue = valueObject.toString();
		}
	}

	private void processTextareaEndTag() {
		inTextArea = false;
		if (textAreaValue == null) {
			return;
		}
		super.text(textAreaValue);
		textAreaValue = null;
	}

}