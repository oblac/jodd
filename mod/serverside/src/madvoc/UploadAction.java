// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.Action;
import jodd.servlet.upload.FileUpload;

import java.io.File;

@MadvocAction
public class UploadAction {         // uses default interceptor stack
	@In
	String boo;

	@In
	FileUpload file3;

	@In
	File file4;

	@In
	UploadFormBean foo;

	@Action
	public String exec() {
		System.out.println("UploadAction.exec");
		System.out.println("boo = " + boo);
		System.out.println(foo);
		System.out.println(file3);
		System.out.println(file4);
		return "ok";
	}
}
