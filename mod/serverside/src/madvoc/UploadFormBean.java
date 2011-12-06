// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.servlet.upload.FileUpload;

public class UploadFormBean {

	private String data1;
	public String getData1() {
		return data1;
	}
	public void setData1(String v) {
		data1 = v;
	}

	private String data2;
	public String getData2() {
		return data2;
	}
	public void setData2(String v) {
		data2 = v;
	}

	private String[] data3;
	public String[] getData3() {
		return data3;
	}
	public void setData3(String[] v) {
		data3 = v;
	}




	private FileUpload file1;
	public FileUpload getFile1() {
		return file1;
	}
	public void setFile1(FileUpload v) {
		file1 = v;
	}

	private FileUpload file2;
	public FileUpload getFile2() {
		return file2;
	}
	public void setFile2(FileUpload v) {
		file2 = v;
	}

	@Override
	public String toString() {
		String result = "UploadFormBean";
		result += "data1 " + data1;
		result += "\ndata2 "  + data2;
		result += "\ndata3 "  + data3;
		result += "\nfile1 "  + file1;
		result += "\nfile2 "  + file2;
		result += "\n";
		return result;
	}



}
