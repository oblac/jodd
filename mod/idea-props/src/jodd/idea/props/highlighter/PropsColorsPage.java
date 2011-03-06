// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import jodd.idea.props.Props;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.util.Map;

/**
 * Color page for Editor &gt; Color &amp; Fonts.
 * Attributes and color descriptors are build from syntax highlighter.
 */
public class PropsColorsPage implements ColorSettingsPage {

	private static final AttributesDescriptor[] ATTRS;

	static {
		ATTRS = new AttributesDescriptor[PropsSyntaxHighlighter.DISPLAY_NAMES.size()];
		TextAttributesKey[] keys = PropsSyntaxHighlighter.DISPLAY_NAMES.keySet().toArray(
				new TextAttributesKey[PropsSyntaxHighlighter.DISPLAY_NAMES.keySet().size()]);

		for (int i = 0; i < keys.length; i++) {
			TextAttributesKey key = keys[i];
			String name = PropsSyntaxHighlighter.DISPLAY_NAMES.get(key).getFirst();
			ATTRS[i] = new AttributesDescriptor(name, key);
		}
	}

	@NotNull
	public String getDisplayName() {
		return Props.FILE_DESCRIPTION;
	}

	public Icon getIcon() {
		return Props.FILE_ICON;
	}

	@NotNull
	public AttributesDescriptor[] getAttributeDescriptors() {
		return ATTRS;
	}

	@NotNull
	public ColorDescriptor[] getColorDescriptors() {
		return ColorDescriptor.EMPTY_ARRAY;
	}

	@NotNull
	public SyntaxHighlighter getHighlighter() {
		return new PropsSyntaxHighlighter();
	}

	@NotNull
	public String getDemoText() {
		return "# Comment \n" +
				"key1=value1\n" +
				"key2:value${macro}\n" +
				"[section]\n"+
				"key<profile>=value3\n"+
				"; more values:\n" +
				"a\\=\\fb : x\\ty\\n\\x\\uzzzz\n"
				;
	}

	public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
		return null;
	}
}