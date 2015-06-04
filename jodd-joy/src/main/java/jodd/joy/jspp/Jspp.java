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

package jodd.joy.jspp;

import jodd.io.FileUtil;
import jodd.lagarto.EmptyTagVisitor;
import jodd.lagarto.LagartoParser;
import jodd.lagarto.Tag;
import jodd.lagarto.TagType;
import jodd.mutable.MutableInteger;
import jodd.util.StringUtil;

import java.io.File;
import java.io.IOException;

/**
 * JSP pre-processor. Process input content and replace all macro calls
 * with content of macro files.
 */
public class Jspp {

	protected char[] tagPrefix = "pp:".toCharArray();
	protected String macroExtension = ".jspf";
	protected File jsppMacroFolder;
	protected String macroPrefix = "${";
	protected String macroSuffix = "}";

	// ---------------------------------------------------------------- get/set

	public String getTagPrefix() {
		return new String(tagPrefix);
	}

	/**
	 * Defines macro tag prefix for JSP files.
	 */
	public void setTagPrefix(String tagPrefix) {
		this.tagPrefix = tagPrefix.toCharArray();
	}

	public String getMacroExtension() {
		return macroExtension;
	}

	/**
	 * Defines macro file name extension.
	 */
	public void setMacroExtension(String macroExtension) {
		this.macroExtension = macroExtension;
	}

	public File getJsppMacroFolder() {
		return jsppMacroFolder;
	}

	/**
	 * Defines folder for macro files.
	 */
	public void setJsppMacroFolder(File jsppMacroFolder) {
		this.jsppMacroFolder = jsppMacroFolder;
	}

	public String getMacroPrefix() {
		return macroPrefix;
	}

	/**
	 * Defines macro prefix for replacements in macro files.
	 */
	public void setMacroPrefix(String macroPrefix) {
		this.macroPrefix = macroPrefix;
	}

	public String getMacroSuffix() {
		return macroSuffix;
	}

	/**
	 * Defines macro suffix for replacements in macro file.
	 */
	public void setMacroSuffix(String macroSuffix) {
		this.macroSuffix = macroSuffix;
	}

	// ---------------------------------------------------------------- process

	/**
	 * Processes input JSP content and replace macros.
	 */
	public String process(final String input) {
		LagartoParser lagartoParser = new LagartoParser(input, true);

		final MutableInteger lastPosition = new MutableInteger(0);
		final StringBuilder sb = new StringBuilder();

		lagartoParser.parse(new EmptyTagVisitor() {
			@Override
			public void tag(Tag tag) {
				if (tag.getType() == TagType.SELF_CLOSING) {
					if (tag.matchTagNamePrefix(tagPrefix)) {
						int tagStart = tag.getTagPosition();

						sb.append(input.substring(lastPosition.getValue(), tagStart));

						String tagName = tag.getName().toString();
						tagName = tagName.substring(tagPrefix.length);

						String macroBody = loadMacro(tagName);

						int attrCount = tag.getAttributeCount();

						for (int i = 0; i < attrCount; i++) {
							String key = macroPrefix + tag.getAttributeName(i) + macroSuffix;
							macroBody = StringUtil.replace(macroBody, key, tag.getAttributeValue(i).toString());
						}

						sb.append(macroBody);

						lastPosition.setValue(tagStart + tag.getTagLength());
					}
				}
			}
		});

		sb.append(input.substring(lastPosition.getValue()));

		return sb.toString();
	}

	/**
	 * Loads macro file body. By default it loads it from
	 * defined macro folder using default macro extension.
	 */
	protected String loadMacro(String macroName) {
		String macroBody;
		File fileMacro = new File(jsppMacroFolder, macroName + macroExtension);
		try {
			macroBody = FileUtil.readString(fileMacro);
		} catch (IOException ioex) {
			throw new JsppException(ioex);
		}
		return macroBody;
	}

}