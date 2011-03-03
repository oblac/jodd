// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import jodd.idea.props.Props;

/**
 * Props PSI file definition.
 */
public class PropsFile extends PsiFileBase {

	public PropsFile(FileViewProvider viewProvider) {
		super(viewProvider, Props.LANGUAGE);
	}

	@NotNull
	public FileType getFileType() {
		return Props.FILE_TYPE;
	}
}

