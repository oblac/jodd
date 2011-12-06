// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.encoding.EncodingManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.nio.charset.Charset;

/**
 * Definition of IntelliJ file type for {@link PropsLanguage language}.
 */
public class PropsFileType extends LanguageFileType {

	public PropsFileType() {
		super(Props.LANGUAGE);
	}

	@NotNull
	public String getName() {
		return Props.LANGUAGE_NAME;
	}

	@NotNull
	public String getDescription() {
		return Props.FILE_DESCRIPTION;
	}

	@NotNull
	public String getDefaultExtension() {
		return Props.EXTENSION;
	}

	public Icon getIcon() {
		return Props.FILE_ICON;
	}

	@Override
	public String getCharset(@NotNull VirtualFile file, final byte[] content) {
		Charset charset = EncodingManager.getInstance().getDefaultCharset();
		return charset == null ? CharsetToolkit.getDefaultSystemCharset().name() : charset.name();
	}

}
