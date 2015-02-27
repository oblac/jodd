// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action.mv;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.Out;
import jodd.upload.FileUpload;

import java.io.IOException;

@MadvocAction
public class UserAction {

    @In
    FileUpload[] uploadFiles;
    @In
    String[] uploadFileNames;

	@Out
	String stuff;

    @Action
    public String importList() throws IOException {
		stuff = "";

		for (FileUpload uploadFile : uploadFiles) {
			stuff += uploadFile.getFileContent().length;
			stuff += uploadFile.getSize();
			stuff += uploadFile.getHeader().getFileName();
			stuff += " ";
		}

		for (String uploadFileName : uploadFileNames) {
			stuff += uploadFileName;
			stuff += " ";
		}

		return "ok";
    }
}