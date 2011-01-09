// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.Action;
import jodd.servlet.upload.FileUpload;

@MadvocAction
public class UploadAction {         // uses default interceptor stack
	@In
	String boo;

	@In
	FileUpload file3;

	@In
	UploadFormBean foo;

	@Action
	public String exec() {
		System.out.println("UploadAction.exec");
		System.out.println(boo);
		System.out.println(foo);
		System.out.println(file3);
		return "ok";
	}
}
