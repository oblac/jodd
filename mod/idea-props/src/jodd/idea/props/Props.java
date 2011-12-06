// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;

import javax.swing.Icon;

/**
 * Various Props constants.
 * Note; order is important!
 */
public interface Props {

	// ---------------------------------------------------------------- icons

	Icon FILE_ICON = IconLoader.findIcon("/gfx/props16.png");

	Icon ICON_16 = IconLoader.findIcon("/gfx/props16.png");

	Icon ICON_32 = IconLoader.findIcon("/gfx/props32.png");

	// ---------------------------------------------------------------- strings

	String FILE_DESCRIPTION = "Jodd props file";

	String LANGUAGE_NAME = "Props";

	String EXTENSION = "props";

	// ---------------------------------------------------------------- props

	PropsLanguage LANGUAGE = new PropsLanguage();

	LanguageFileType FILE_TYPE = new PropsFileType();

}
