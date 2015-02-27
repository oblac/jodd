// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action.mv;

import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.InOut;
import jodd.madvoc.meta.MadvocAction;
import jodd.upload.FileUpload;

@MadvocAction
public class UploadAction {

	@InOut
	FileUpload[] uploadFiles;
	@InOut
	String[] uploadFileNames;

	@Action
	public String execute() {
		return "move:/mv/user.importList.html" ;
	}

}