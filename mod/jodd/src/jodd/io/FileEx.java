// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Extended <code>File</code> with additional {@link FileUtil} methods.
 */
public class FileEx extends File {

	protected final FileUtilParams params;

	// ---------------------------------------------------------------- constructors

	public FileEx(String pathname) {
		this(pathname, FileUtil.cloneParams());
	}

	public FileEx(String parent, String child) {
		this(parent, child, FileUtil.cloneParams());
	}

	public FileEx(File parent, String child) {
		this(parent, child, FileUtil.cloneParams());
	}

	public FileEx(URI uri) {
		this(uri, FileUtil.cloneParams());
	}

	public FileEx(String pathname, FileUtilParams params) {
		super(pathname);
		this.params = params;
	}

	public FileEx(String parent, String child, FileUtilParams params) {
		super(parent, child);
		this.params = params;
	}

	public FileEx(File parent, String child, FileUtilParams params) {
		super(parent, child);
		this.params = params;
	}

	public FileEx(URI uri, FileUtilParams params) {
		super(uri);
		this.params = params;
	}

	// ---------------------------------------------------------------- settings

	/**
	 * Returns paramters.
	 */
	public FileUtilParams getParams() {
		return params;
	}

	// ---------------------------------------------------------------- operations

	public void touch() throws IOException {
		FileUtil.touch(this);
	}

	public void copyFile(File dest) throws IOException {
		FileUtil.copyFile(this, dest, params);
	}
	public void copyToDir(File dest) throws IOException {
		FileUtil.copyFileToDir(this, dest, params);
	}
	public void copyDir(File dest) throws IOException {
		FileUtil.copyDir(this, dest, params);
	}

	public void moveFile(File dest) throws IOException {
		FileUtil.moveFile(this, dest, params);
	}
    public void moveToDir(File dest) throws IOException {
		FileUtil.moveFileToDir(this, dest, params);
	}
	public void moveDir(File dest) throws IOException {
		FileUtil.moveDir(this, dest);
	}

	public void deleteFile() throws IOException {
		FileUtil.deleteFile(this);
	}
	public void deleteDir() throws IOException {
		FileUtil.deleteDir(this, params);
	}
	public void cleanDir() throws IOException {
		FileUtil.cleanDir(this, params);
	}

	public String readString() throws IOException {
		return FileUtil.readString(this, params.encoding);
	}
	public String readString(String encoding) throws IOException {
		return FileUtil.readString(this, encoding);
	}
	public void writeString(String data) throws IOException {
		FileUtil.writeString(this, data, params.encoding);
	}
	public void writeString(String data, String encoding) throws IOException {
		FileUtil.writeString(this, data, encoding);
	}

	public byte[] readBytes() throws IOException {
		return FileUtil.readBytes(this);
	}
	public void writeBytes(byte[] data) throws IOException {
		FileUtil.writeBytes(this, data);
	}

	public boolean compare(File dest) throws IOException {
		return FileUtil.compare(this, dest);
	}

	public boolean isNewer(File reference) {
		return FileUtil.isNewer(this, reference);
	}
	public boolean isNewer(long timeMillis) {
		return FileUtil.isNewer(this, timeMillis);
	}
	public boolean isOlder(File reference) {
		return FileUtil.isOlder(this, reference);
	}
	public boolean isOlder(long timeMillis) {
		return FileUtil.isOlder(this, timeMillis);
	}


	public void copy(File dest) throws IOException {
		FileUtil.copy(this, dest, params);
	}
	public void move(File dest) throws IOException {
		FileUtil.move(this, dest, params);
	}
	public void remove() throws IOException {
		FileUtil.delete(this, params);
	}

}
