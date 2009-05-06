// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Extended File object that adapts {@link FileUtil}s.
 */
public class FileEx extends File {

	// ---------------------------------------------------------------- constructors

	public FileEx(String pathname) {
		super(pathname);
	}

	public FileEx(String parent, String child) {
		super(parent, child);
	}

	public FileEx(File parent, String child) {
		super(parent, child);
	}

	public FileEx(URI uri) {
		super(uri);
	}


	// ---------------------------------------------------------------- settings

	protected FileUtil.Settings settings = FileUtil.cloneSettings();

	public void setPreserveDate(boolean value) {
		settings.preserveDate = value;
	}
	public boolean isPreserveDate() {
		return settings.preserveDate;
	}

	public void setOverwriteExisting(boolean value) {
		settings.overwrite = value;
	}
	public boolean isOverwriteExisting() {
		return settings.overwrite;
	}

	public void setCreateDirs(boolean value) {
		settings.createDirs = value;
	}
	public boolean isCreateDirs() {
		return settings.createDirs;
	}

	public void setRecursive(boolean value) {
		settings.recursive = value;
	}
	public boolean isRecursive() {
		return settings.recursive;
	}

	public boolean isContinueOnError() {
		return settings.continueOnError;
	}
	public void setContinueOnError(boolean continueOnError) {
		settings.continueOnError = continueOnError;
	}

	public String getEncoding() {
		return settings.encoding;
	}
	public void setEncoding(String encoding) {
		settings.encoding = encoding;
	}

	// ---------------------------------------------------------------- operations

	public void touch() throws IOException {
		FileUtil.touch(this);
	}

	public void copyFile(File dest) throws IOException {
		FileUtil.copyFile(this, dest, settings);
	}
	public void copyToDir(File dest) throws IOException {
		FileUtil.copyFileToDir(this, dest, settings);
	}
	public void copyDir(File dest) throws IOException {
		FileUtil.copyDir(this, dest, settings);
	}

	public void moveFile(File dest) throws IOException {
		FileUtil.moveFile(this, dest, settings);
	}
    public void moveToDir(File dest) throws IOException {
		FileUtil.moveFileToDir(this, dest, settings);
	}
	public void moveDir(File dest) throws IOException {
		FileUtil.moveDir(this, dest);
	}

	public void deleteFile() throws IOException {
		FileUtil.deleteFile(this);
	}
	public void deleteDir() throws IOException {
		FileUtil.deleteDir(this, settings);
	}
	public void cleanDir() throws IOException {
		FileUtil.cleanDir(this, settings);
	}

	public String readString() throws IOException {
		return FileUtil.readString(this);
	}
	public String readString(String encoding) throws IOException {
		return FileUtil.readString(this, encoding);
	}
	public void writeString(String data) throws IOException {
		FileUtil.writeString(this, data);
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
		FileUtil.copy(this, dest, settings);
	}
	public void move(File dest) throws IOException {
		FileUtil.move(this, dest, settings);
	}
	public void delete(File dest) throws IOException {
		FileUtil.delete(this, settings);
	}

}
