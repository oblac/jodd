// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.jspp;

import jodd.io.FileUtil;
import jodd.servlet.HtmlTag;
import jodd.util.StringTemplateParser;
import jodd.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * JSP pre-processor. Process input content and replace all macro calls
 * with content of macro files.
 */
public class Jspp {

	protected String tagPrefix = "pp:";
	protected String macroExtension = ".jspf";
	protected File jsppMacroFolder;
	protected String macroPrefix = "${";
	protected String macroSuffix = "}";

	// ---------------------------------------------------------------- get/set

	public String getTagPrefix() {
		return tagPrefix;
	}

	/**
	 * Defines macro tag prefix for JSP files.
	 */
	public void setTagPrefix(String tagPrefix) {
		this.tagPrefix = tagPrefix;
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
	public String process(String input) {
		StringTemplateParser tagParser = createStringTemplateParser();

		return tagParser.parse(input, new StringTemplateParser.MacroResolver() {
			public String resolve(String macro) {
				HtmlTag htmlTag = new HtmlTag("<" + macro + "/>");
				String tagName = htmlTag.getTagName();

				String macroBody = loadMacro(tagName);

				Map<String, String> attributes = htmlTag.getAttributes();

				// replace attributes with values
				for (Map.Entry<String, String> entry : attributes.entrySet()) {
					String key = macroPrefix + entry.getKey() + macroSuffix;
					macroBody = StringUtil.replace(macroBody, key, entry.getValue());
				}
				return macroBody;
			}
		});
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

	/**
	 * Creates new string template parser.
	 */
	protected StringTemplateParser createStringTemplateParser() {
		StringTemplateParser tagParser = new StringTemplateParser();
		tagParser.setMacroStart("<" + tagPrefix);
		tagParser.setMacroEnd("/>");
		tagParser.setEscapeChar((char) 0);
		return tagParser;
	}

}